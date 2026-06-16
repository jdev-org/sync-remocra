package fr.eaudeparis.syncremocra.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ApiSettings;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestManager {

  private static final long TOKEN_EXPIRY_SAFETY_WINDOW_MILLIS = 60_000L;
  private static final String API_AUTHENT_ERROR_CODE = "0200";
  private static final String API_AUTHENT_ERROR_MESSAGE =
      "Authentification refusée à l'API Remocra";
  private static final String API_CONNECTION_ERROR_CODE = "0003";
  private static final String API_CONNECTION_ERROR_MESSAGE =
      "Impossible d'établir une connexion avec l'API Remocra";
  private static final Pattern ERROR_CODE_PATTERN = Pattern.compile("^([A-Z]?\\d{4})\\b");
  private static Logger logger = LoggerFactory.getLogger(RequestManager.class);

  private final ApiSettings settings;
  private final ApiEndpoints apiEndpoints;
  private final ErrorReporter errorReporter;
  private final ObjectMapper mapper = new ObjectMapper();
  private String cachedAuthorizationHeader;
  private long cachedAuthorizationHeaderExpiresAtMillis;

  @Inject private ErreurRepository erreurRepository;

  @Inject
  RequestManager(ApiSettings settings, ApiEndpoints apiEndpoints) {
    this(settings, apiEndpoints, null);
  }

  /**
   * Constructeur dédié aux tests pour remplacer le mécanisme de remontée d'erreurs.
   *
   * @param settings Configuration API
   * @param apiEndpoints Fournisseur des chemins d'API centralisés
   * @param errorReporter Reporteur d'erreurs de test, ou {@code null} pour utiliser le dépôt
   */
  RequestManager(ApiSettings settings, ApiEndpoints apiEndpoints, ErrorReporter errorReporter) {
    this.settings = settings;
    this.apiEndpoints = apiEndpoints;
    this.errorReporter = errorReporter;
  }

  /**
   * Fonction d'authentification à l'API REMOcRA. Cette fonction est appelée avant chaque requête
   *
   * @return String le header Authorization complet
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  private String authenticateToRemocra() throws APIConnectionException, APIAuthentException {
    if (!"keycloak".equalsIgnoreCase(settings.authType())) {
      logger.warn(
          "Mode d'authentification API REMOcRA non supporté en v3 only: {}", settings.authType());
      throwAuthenticationException();
    }
    return authenticateToKeycloak();
  }

  private synchronized String authenticateToKeycloak()
      throws APIConnectionException, APIAuthentException {
    long now = System.currentTimeMillis();
    if (cachedAuthorizationHeader != null
        && now + TOKEN_EXPIRY_SAFETY_WINDOW_MILLIS < cachedAuthorizationHeaderExpiresAtMillis) {
      return cachedAuthorizationHeader;
    }

    validateKeycloakSettings();

    URL url;
    HttpURLConnection conn = null;
    try {
      url =
          new URL(
              trimTrailingSlash(settings.keycloakUrl())
                  + "/realms/"
                  + encodePathSegment(settings.keycloakRealm())
                  + "/protocol/openid-connect/token");
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

      String formData =
          "grant_type=client_credentials"
              + "&client_id="
              + encode(settings.keycloakClientId())
              + "&client_secret="
              + encode(settings.keycloakClientSecret());
      byte[] out = formData.getBytes(StandardCharsets.UTF_8);
      conn.setFixedLengthStreamingMode(out.length);
      conn.connect();
      try (OutputStream os = conn.getOutputStream()) {
        os.write(out);
      }

      int codeRetour = conn.getResponseCode();
      if (codeRetour == HttpURLConnection.HTTP_OK) {
        Map<?, ?> tokenResponse = mapper.readValue(readStream(conn.getInputStream()), Map.class);
        Object accessToken = tokenResponse.get("access_token");
        if (accessToken == null || String.valueOf(accessToken).isEmpty()) {
          throwAuthenticationException();
        }

        Number expiresIn =
            tokenResponse.get("expires_in") instanceof Number
                ? (Number) tokenResponse.get("expires_in")
                : Integer.valueOf(300);
        cachedAuthorizationHeader = "Bearer " + accessToken;
        cachedAuthorizationHeaderExpiresAtMillis = now + expiresIn.longValue() * 1000L;
        return cachedAuthorizationHeader;
      }

      logger.warn("Keycloak authentication refused: {}", readStream(conn.getErrorStream()));
      throwAuthenticationException();
    } catch (IOException e) {
      throwConnectionException(e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return null;
  }

  /**
   * Envoie une requête POST ou PUT
   *
   * @param method La méthode utilisée "POST" ou "PUT"
   * @param path Le chemin du endpoint
   * @param jsonData Les données à envoyer
   * @return Le code de retour de la requête (200 ou 201)
   * @throws RequestException Erreur retournée par l'API
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  public Integer sendRequest(String method, String path, String jsonData)
      throws RequestException, APIConnectionException, APIAuthentException {

    logger.debug("Send request to  " + path + " with content " + jsonData);

    String response = "";
    URL url;
    HttpURLConnection conn = null;
    String token = this.authenticateToRemocra();
    try {
      url = new URL(buildUrl(path));
      conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod(method);
      conn.setDoOutput(true);

      conn.setRequestProperty("Authorization", token);

      if (jsonData != null) {
        byte[] out = jsonData.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        conn.setFixedLengthStreamingMode(length);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.connect();
        try (OutputStream os = conn.getOutputStream()) {
          os.write(out);
        }
      }

      int codeRetour = conn.getResponseCode();
      logger.debug("Code return" + codeRetour);

      if (codeRetour == HttpURLConnection.HTTP_OK
          || codeRetour == HttpURLConnection.HTTP_CREATED) {
        return codeRetour;
      } else if (codeRetour == HttpURLConnection.HTTP_UNAUTHORIZED) {
        logAuthenticationFailure(method, path, codeRetour, readStream(conn.getErrorStream()));
        throwAuthenticationException();
      } else {
        response = readStream(conn.getErrorStream());
        throw buildRequestException(method, path, codeRetour, response);
      }
    } catch (IOException e) {
      throwConnectionException(e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return null;
  }

  /**
   * Envoie une requête GET à l'API
   *
   * @param path Le chemin du endpoint
   * @return Les données JSON renvoyées par la requête
   * @throws RequestException Erreur retournée par l'API
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  public String sendGetRequest(String path, Map<String, String> parameters)
      throws RequestException, APIConnectionException, APIAuthentException {
    String response = "";
    URL url;
    HttpURLConnection conn = null;
    try {

      // Si des paramètres sont fournis
      if (parameters != null && parameters.size() > 0) {
        path = path + (path.contains("?") ? "&" : "?");
        int nb = 0;
        for (String i : parameters.keySet()) {
          if (parameters.get(i) == null) {
            continue;
          }
          if (nb > 0) {
            path = path + "&";
          }
          path = path + encode(i) + "=" + encode(parameters.get(i));
          nb++;
        }
      }

      logger.info("Send request to  : " + path);

      url = new URL(buildUrl(path));
      conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("GET");
      conn.setDoOutput(true);
      String token = this.authenticateToRemocra();

      conn.setRequestProperty("Authorization", token);

      int codeRetour = conn.getResponseCode();

      if (codeRetour == HttpURLConnection.HTTP_OK
          || codeRetour == HttpURLConnection.HTTP_CREATED) {
        response = readStream(conn.getInputStream());
        logger.debug("get response  : " + response);
        return response;
      } else if (codeRetour == HttpURLConnection.HTTP_UNAUTHORIZED) {
        logAuthenticationFailure("GET", path, codeRetour, readStream(conn.getErrorStream()));
        throwAuthenticationException();
      } else {
        response = readStream(conn.getErrorStream());
        throw buildRequestException("GET", path, codeRetour, response);
      }
    } catch (IOException e) {
      throwConnectionException(e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return null;
  }

  public String sendGetRequest(String path)
      throws APIConnectionException, RequestException, APIAuthentException {
    return this.sendGetRequest(path, null);
  }

  /**
   * Construit l'URL absolue à partir de l'hôte, d'un préfixe optionnel et du chemin demandé.
   *
   * @param path Chemin relatif de l'endpoint
   * @return URL absolue prête à être appelée
   */
  private String buildUrl(String path) {
    return trimTrailingSlash(settings.host())
        + normalizePath(settings.basePath())
        + normalizePath(path);
  }

  /**
   * Normalise un chemin afin qu'il soit vide ou préfixé par un slash.
   *
   * @param path Chemin à normaliser
   * @return Chemin normalisé
   */
  private String normalizePath(String path) {
    if (path == null || path.isEmpty() || "/".equals(path)) {
      return "";
    }
    return path.startsWith("/") ? path : "/" + path;
  }

  /**
   * Supprime les slashs terminaux d'une URL ou d'un chemin de base.
   *
   * @param value Valeur à nettoyer
   * @return Valeur sans slash terminal
   */
  private String trimTrailingSlash(String value) {
    if (value == null) {
      return "";
    }
    while (value.endsWith("/")) {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  /**
   * Encode une valeur pour l'utiliser dans une query string.
   *
   * @param value Valeur à encoder
   * @return Valeur encodée en UTF-8
   */
  private String encode(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20");
    } catch (IOException e) {
      throw new IllegalStateException("UTF-8 encoding is not available", e);
    }
  }

  /**
   * Encode un segment de chemin en conservant les slashs déjà présents.
   *
   * @param value Valeur à encoder
   * @return Segment encodé
   */
  private String encodePathSegment(String value) {
    return encode(value).replace("%2F", "/");
  }

  /**
   * Lit entièrement un flux HTTP en chaîne UTF-8.
   *
   * @param inputStream Flux à lire
   * @return Contenu du flux, ou chaîne vide si le flux est nul
   * @throws IOException Erreur de lecture
   */
  private String readStream(InputStream inputStream) throws IOException {
    if (inputStream == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String output;
      while ((output = br.readLine()) != null) {
        sb.append(output);
      }
    }
    return sb.toString();
  }

  /**
   * Vérifie que la configuration minimale Keycloak est présente avant d'appeler OIDC.
   *
   * @throws APIAuthentException Configuration incomplète
   */
  private void validateKeycloakSettings() throws APIAuthentException {
    if (settings.keycloakUrl().isEmpty()
        || settings.keycloakRealm().isEmpty()
        || settings.keycloakClientId().isEmpty()
        || settings.keycloakClientSecret().isEmpty()) {
      logger.warn("Configuration Keycloak incomplète pour l'authentification API REMOcRA");
      throwAuthenticationException();
    }
  }

  /**
   * Remonte l'erreur métier historique d'authentification API avant de lever l'exception.
   *
   * @throws APIAuthentException Toujours levée
   */
  private void throwAuthenticationException() throws APIAuthentException {
    reportError(API_AUTHENT_ERROR_CODE, API_AUTHENT_ERROR_MESSAGE, null);
    throw new APIAuthentException();
  }

  /**
   * Remonte une erreur de connexion puis lève l'exception dédiée.
   *
   * @param e Cause d'origine
   * @throws APIConnectionException Toujours levée
   */
  private void throwConnectionException(IOException e) throws APIConnectionException {
    logger.warn("Error  : ", e);
    reportError(API_CONNECTION_ERROR_CODE, API_CONNECTION_ERROR_MESSAGE, null);
    throw new APIConnectionException();
  }

  /**
   * Journalise un refus d'authentification retourné par l'API métier.
   *
   * @param method Méthode HTTP appelée
   * @param path Chemin relatif appelé
   * @param statusCode Code HTTP retourné
   * @param responseBody Corps de réponse éventuel
   */
  private void logAuthenticationFailure(
      String method, String path, int statusCode, String responseBody) {
    logger.warn(
        "HTTP {} lors de l'appel {} {}. Reponse: {}",
        statusCode, method, path, normalizeResponseBody(responseBody));
  }

  /**
   * Construit une exception explicite à partir d'un retour HTTP en erreur.
   *
   * @param method Méthode HTTP appelée
   * @param path Chemin relatif appelé
   * @param statusCode Code HTTP retourné
   * @param responseBody Corps de réponse d'erreur éventuel
   * @return Exception métier prête à être propagée
   */
  private RequestException buildRequestException(
      String method, String path, int statusCode, String responseBody) {
    String normalizedBody = normalizeResponseBody(responseBody);
    String errorCode = extractErrorCode(normalizedBody);
    String message =
        String.format(
            "HTTP %d lors de l'appel %s %s. Reponse: %s", statusCode, method, path, normalizedBody);
    logger.warn(message);
    return new RequestException(statusCode, errorCode, message);
  }

  /**
   * Extrait le code d'erreur métier lorsque le corps de réponse commence par un format reconnu.
   *
   * @param responseBody Corps de réponse HTTP
   * @return Code métier ou {@code null}
   */
  private String extractErrorCode(String responseBody) {
    Matcher matcher = ERROR_CODE_PATTERN.matcher(responseBody);
    return matcher.find() ? matcher.group(1) : null;
  }

  /**
   * Nettoie le corps d'erreur pour le rendre plus lisible dans les logs.
   *
   * @param responseBody Corps brut
   * @return Corps nettoyé ou marqueur explicite si vide
   */
  private String normalizeResponseBody(String responseBody) {
    if (responseBody == null) {
      return "<empty>";
    }
    String normalized = responseBody.trim().replaceAll("\\s+", " ");
    return normalized.isEmpty() ? "<empty>" : normalized;
  }

  /**
   * Centralise la remontée d'erreurs pour permettre les tests sans base de données.
   *
   * @param codeErreur Code métier d'erreur
   * @param message Message descriptif
   * @param idMessage Message métier associé
   */
  private void reportError(String codeErreur, String message, Long idMessage) {
    if (errorReporter != null) {
      errorReporter.addError(codeErreur, message, idMessage);
      return;
    }
    this.erreurRepository.addError(codeErreur, message, idMessage);
  }

  @FunctionalInterface
  interface ErrorReporter {
    void addError(String codeErreur, String message, Long idMessage);
  }
}
