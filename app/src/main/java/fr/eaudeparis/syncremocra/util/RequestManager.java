package fr.eaudeparis.syncremocra.util;

import fr.eaudeparis.syncremocra.api.ApiSettings;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.inject.Inject;

public class RequestManager {

  private final ApiSettings settings;

  @Inject private ErreurRepository erreurRepository;

  @Inject
  RequestManager(ApiSettings settings) {
    this.settings = settings;
  }

  /**
   * Fonction d'authentification à l'API REMOcRA. Cette fonction est appelée avant chaque requête
   *
   * @return String le token d'identification JWT
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  private String authenticateToRemocra() throws APIConnectionException, APIAuthentException {
    URL url;
    HttpURLConnection conn = null;
    try {
      url = new URL(settings.host() + "/authentication/jwt?email=" + settings.mail());
      conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("X-password", settings.password());

      Integer codeRetour = conn.getResponseCode();

      if (codeRetour != null && conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return conn.getHeaderField("Authorization");
      } else {
        // Erreur authentification à l'API
        this.erreurRepository.addError("0200", "Authentification refusée à l'API Remocra", null);
        throw new APIAuthentException();
      }
    } catch (IOException e) {
      // Impossible de contacter l'API
      this.erreurRepository.addError(
          "0003", "Impossible d'établir une connexion avec l'API Remocra", null);
      throw new APIConnectionException();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
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
    String response = "";
    URL url;
    HttpURLConnection conn = null;
    String token = this.authenticateToRemocra();
    try {
      url = new URL(settings.host() + path);
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

      if (codeRetour == HttpURLConnection.HTTP_OK || codeRetour == HttpURLConnection.HTTP_CREATED) {
        return codeRetour;
      } else {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String output;
        while ((output = br.readLine()) != null) {
          sb.append(output);
        }
        response = sb.toString();
        br.close();

        String errorCode = (response.split(" ").length > 0) ? response.split(" ")[0] : null;

        // La requête a bien été envoyée mais l'API a retourné une erreur
        throw new RequestException(conn.getResponseCode(), errorCode, response);
      }
    } catch (IOException e) {
      e.printStackTrace();
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
        path = path + "?";
        int nb = 0;
        for (String i : parameters.keySet()) {
          if (nb > 0) {
            path = path + "&";
          }
          path = path + i + "=" + parameters.get(i).replaceAll(" ", "%20");
          nb++;
        }
      }

      url = new URL(settings.host() + path);
      conn = (HttpURLConnection) url.openConnection();

      conn.setRequestMethod("GET");
      conn.setDoOutput(true);
      String token = this.authenticateToRemocra();

      conn.setRequestProperty("Authorization", token);

      int codeRetour = conn.getResponseCode();

      if (codeRetour == HttpURLConnection.HTTP_OK || codeRetour == HttpURLConnection.HTTP_CREATED) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          content.append(inputLine);
        }
        in.close();
        return content.toString();
      } else {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String output;
        while ((output = br.readLine()) != null) {
          sb.append(output);
        }
        response = sb.toString();
        br.close();

        String errorCode = (response.split(" ").length > 0) ? response.split(" ")[0] : null;
        // La requête a bien été envoyée mais l'API a retourné une erreur
        throw new RequestException(conn.getResponseCode(), errorCode, response);
      }
    } catch (IOException e) {
      e.printStackTrace();
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
}
