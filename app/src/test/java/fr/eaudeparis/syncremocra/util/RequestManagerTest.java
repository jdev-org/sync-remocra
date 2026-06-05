package fr.eaudeparis.syncremocra.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ImmutableApiSettings;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Test;

public class RequestManagerTest {

  private HttpServer server;

  @After
  public void tearDown() {
    if (server != null) {
      server.stop(0);
      server = null;
    }
  }

  @Test
  public void shouldUseLegacyJwtAndBasePathForGetRequest() throws Exception {
    AtomicInteger authCalls = new AtomicInteger();
    AtomicInteger apiCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/remocra/authentication/jwt",
        exchange -> {
          authCalls.incrementAndGet();
          assertEquals("POST", exchange.getRequestMethod());
          assertEquals("email=test%40example.com", exchange.getRequestURI().getRawQuery());
          assertEquals("secret", exchange.getRequestHeaders().getFirst("X-password"));
          exchange.getResponseHeaders().add("Authorization", "Bearer legacy-token");
          respond(exchange, 200, "");
        });
    server.createContext(
        "/remocra/deci/pei",
        exchange -> {
          apiCalls.incrementAndGet();
          assertEquals("GET", exchange.getRequestMethod());
          assertEquals(
              "Bearer legacy-token", exchange.getRequestHeaders().getFirst("Authorization"));
          assertEquals("numero=PEI%201&statut=EN%20COURS", exchange.getRequestURI().getRawQuery());
          respond(exchange, 200, "{\"ok\":true}");
        });
    server.start();

    fr.eaudeparis.syncremocra.util.RequestManager requestManager =
        new fr.eaudeparis.syncremocra.util.RequestManager(
            ImmutableApiSettings.builder()
                .host(serverBaseUrl())
                .basePath("/remocra")
                .mail("test@example.com")
                .password("secret")
                .build(),
            new ApiEndpoints(),
            (codeErreur, message, idMessage) -> {});

    Map<String, String> params = new LinkedHashMap<>();
    params.put("numero", "PEI 1");
    params.put("statut", "EN COURS");

    String response = requestManager.sendGetRequest("/deci/pei", params);

    assertEquals("{\"ok\":true}", response);
    assertEquals(1, authCalls.get());
    assertEquals(1, apiCalls.get());
  }

  @Test
  public void shouldUseKeycloakAndCacheBearerToken() throws Exception {
    AtomicInteger tokenCalls = new AtomicInteger();
    AtomicInteger apiCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> {
          tokenCalls.incrementAndGet();
          assertEquals("POST", exchange.getRequestMethod());
          String body =
              new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          assertEquals(
              "grant_type=client_credentials&client_id=sync-remocra&client_secret=top-secret",
              body);
          respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}");
        });
    server.createContext(
        "/api/deci/pei",
        exchange -> {
          apiCalls.incrementAndGet();
          assertEquals("Bearer kc-token", exchange.getRequestHeaders().getFirst("Authorization"));
          respond(exchange, 200, "{\"ok\":true}");
        });
    server.start();

    fr.eaudeparis.syncremocra.util.RequestManager requestManager =
        new fr.eaudeparis.syncremocra.util.RequestManager(
            ImmutableApiSettings.builder()
                .host(serverBaseUrl())
                .basePath("/api")
                .authType("keycloak")
                .keycloakUrl(serverBaseUrl())
                .keycloakRealm("remocra")
                .keycloakClientId("sync-remocra")
                .keycloakClientSecret("top-secret")
                .mail("unused@example.com")
                .password("unused")
                .build(),
            new ApiEndpoints(),
            (codeErreur, message, idMessage) -> {});

    assertEquals("{\"ok\":true}", requestManager.sendGetRequest("/deci/pei"));
    assertEquals("{\"ok\":true}", requestManager.sendGetRequest("/deci/pei"));
    assertEquals(1, tokenCalls.get());
    assertEquals(2, apiCalls.get());
  }

  @Test
  public void shouldRaiseConnectionErrorWhenApiIsUnavailable() {
    List<ReportedError> reportedErrors = new ArrayList<>();
    fr.eaudeparis.syncremocra.util.RequestManager requestManager =
        new fr.eaudeparis.syncremocra.util.RequestManager(
            ImmutableApiSettings.builder()
                .host("http://127.0.0.1:1")
                .mail("test@example.com")
                .password("secret")
                .build(),
            new ApiEndpoints(),
            (codeErreur, message, idMessage) ->
                reportedErrors.add(new ReportedError(codeErreur, message, idMessage)));

    boolean thrown = false;
    try {
      requestManager.sendGetRequest("/deci/pei");
    } catch (fr.eaudeparis.syncremocra.util.APIConnectionException e) {
      thrown = true;
    } catch (Exception e) {
      throw new AssertionError("Unexpected exception", e);
    }

    assertTrue(thrown);
    assertEquals(1, reportedErrors.size());
    assertEquals("0003", reportedErrors.get(0).codeErreur);
    assertEquals(
        "Impossible d'établir une connexion avec l'API Remocra", reportedErrors.get(0).message);
    assertEquals(null, reportedErrors.get(0).idMessage);
  }

  @Test
  public void shouldRaiseAuthenticationErrorWhenKeycloakConfigIsIncomplete() {
    List<ReportedError> reportedErrors = new ArrayList<>();
    fr.eaudeparis.syncremocra.util.RequestManager requestManager =
        new fr.eaudeparis.syncremocra.util.RequestManager(
            ImmutableApiSettings.builder()
                .host("http://localhost")
                .authType("keycloak")
                .mail("unused@example.com")
                .password("unused")
                .build(),
            new ApiEndpoints(),
            (codeErreur, message, idMessage) ->
                reportedErrors.add(new ReportedError(codeErreur, message, idMessage)));

    boolean thrown = false;
    try {
      requestManager.sendGetRequest("/deci/pei");
    } catch (fr.eaudeparis.syncremocra.util.APIAuthentException e) {
      thrown = true;
    } catch (Exception e) {
      throw new AssertionError("Unexpected exception", e);
    }

    assertTrue(thrown);
    assertEquals(1, reportedErrors.size());
    assertEquals("0200", reportedErrors.get(0).codeErreur);
    assertEquals("Authentification refusée à l'API Remocra", reportedErrors.get(0).message);
    assertEquals(null, reportedErrors.get(0).idMessage);
  }

  @Test
  public void shouldRaiseAuthenticationErrorWhenLegacyJwtIsRefused() throws Exception {
    List<ReportedError> reportedErrors = new ArrayList<>();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/authentication/jwt",
        exchange -> {
          assertEquals("POST", exchange.getRequestMethod());
          respond(exchange, 401, "");
        });
    server.start();

    fr.eaudeparis.syncremocra.util.RequestManager requestManager =
        new fr.eaudeparis.syncremocra.util.RequestManager(
            ImmutableApiSettings.builder()
                .host(serverBaseUrl())
                .mail("test@example.com")
                .password("secret")
                .build(),
            new ApiEndpoints(),
            (codeErreur, message, idMessage) ->
                reportedErrors.add(new ReportedError(codeErreur, message, idMessage)));

    boolean thrown = false;
    try {
      requestManager.sendGetRequest("/deci/pei");
    } catch (fr.eaudeparis.syncremocra.util.APIAuthentException e) {
      thrown = true;
    } catch (Exception e) {
      throw new AssertionError("Unexpected exception", e);
    }

    assertTrue(thrown);
    assertEquals(1, reportedErrors.size());
    assertEquals("0200", reportedErrors.get(0).codeErreur);
    assertEquals("Authentification refusée à l'API Remocra", reportedErrors.get(0).message);
    assertEquals(null, reportedErrors.get(0).idMessage);
  }

  private static final class ReportedError {
    private final String codeErreur;
    private final String message;
    private final Long idMessage;

    private ReportedError(String codeErreur, String message, Long idMessage) {
      this.codeErreur = codeErreur;
      this.message = message;
      this.idMessage = idMessage;
    }
  }

  private String serverBaseUrl() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  private void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
    byte[] content = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(statusCode, content.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(content);
    }
    exchange.close();
  }
}
