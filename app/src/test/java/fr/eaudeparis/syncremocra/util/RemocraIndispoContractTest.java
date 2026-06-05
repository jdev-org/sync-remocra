package fr.eaudeparis.syncremocra.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ImmutableApiSettings;
import fr.eaudeparis.syncremocra.repository.message.IndispoTemporaireMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Test;

public class RemocraIndispoContractTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final ApiEndpoints apiEndpoints = new ApiEndpoints();
  private HttpServer server;

  @After
  public void tearDown() {
    if (server != null) {
      server.stop(0);
      server = null;
    }
  }

  @Test
  public void shouldSendCreateIndispoUsingV3Payload() throws Exception {
    AtomicInteger postCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/indispoTemporaire",
        exchange -> {
          if ("POST".equals(exchange.getRequestMethod())) {
            postCalls.incrementAndGet();
            assertEquals("Bearer kc-token", exchange.getRequestHeaders().getFirst("Authorization"));

            String body =
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> payload =
                mapper.readValue(body, new TypeReference<Map<String, Object>>() {});

            assertEquals("Mise en indisponibilité Eau de Paris", payload.get("motif"));
            assertEquals(Boolean.TRUE, payload.get("mailAvantIndisponibilite"));
            assertEquals(Boolean.TRUE, payload.get("mailApresIndisponibilite"));
            assertEquals(Boolean.TRUE, payload.get("basculeAutoDisponible"));
            assertEquals(Boolean.TRUE, payload.get("basculeAutoIndisponible"));
            assertTrue(String.valueOf(payload.get("dateDebut")).startsWith("2026-06-05T08:30:00"));
            assertEquals(List.of("PEI-001"), payload.get("listeNumeroPei"));

            respond(exchange, 201, "");
            return;
          }
          respond(exchange, 405, "");
        });
    server.start();

    ObjectNode payload =
        IndispoTemporaireMapper.buildCreatePayload(
            mapper, "PEI-001", LocalDateTime.of(2026, 6, 5, 8, 30));

    RequestManager requestManager = createRequestManager();
    Integer responseCode =
        requestManager.sendRequest("POST", apiEndpoints.indispoTemporaire(), payload.toString());

    assertEquals(Integer.valueOf(201), responseCode);
    assertEquals(1, postCalls.get());
  }

  @Test
  public void shouldReadAndCloseActiveIndispoUsingV3Contract() throws Exception {
    AtomicInteger getCalls = new AtomicInteger();
    AtomicInteger putCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/indispoTemporaire",
        exchange -> {
          if ("GET".equals(exchange.getRequestMethod())) {
            getCalls.incrementAndGet();
            assertEquals("organismeApi=EAU_DE_PARIS", exchange.getRequestURI().getRawQuery());
            respond(
                exchange,
                200,
                "[{\"indisponibiliteTemporaireId\":\"uuid-1\","
                    + "\"indisponibiliteTemporaireDateDebut\":\"2026-06-05T08:30:00+02:00\","
                    + "\"listeNumeroPei\":[\"PEI-001\"]}]");
            return;
          }
          respond(exchange, 405, "");
        });
    server.createContext(
        "/deci/indispoTemporaire/uuid-1",
        exchange -> {
          putCalls.incrementAndGet();
          assertEquals("PUT", exchange.getRequestMethod());

          String body =
              new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          Map<String, Object> payload =
              mapper.readValue(body, new TypeReference<Map<String, Object>>() {});

          assertEquals("2026-06-05T08:30:00+02:00", payload.get("dateDebut"));
          assertTrue(String.valueOf(payload.get("dateFin")).startsWith("2026-06-05T12:45:00"));
          assertEquals(List.of("PEI-001"), payload.get("listeNumeroPei"));

          respond(exchange, 200, "");
        });
    server.start();

    RequestManager requestManager = createRequestManager();
    String response =
        requestManager.sendGetRequest(
            apiEndpoints.indispoTemporaire(), IndispoTemporaireMapper.buildSearchParams());
    List<Map<String, Object>> indispos =
        mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

    Map<String, Object> activeIndispo =
        IndispoTemporaireMapper.findActiveIndispo(indispos, "PEI-001");
    assertEquals("uuid-1", String.valueOf(IndispoTemporaireMapper.getIndispoId(activeIndispo)));
    assertNull(IndispoTemporaireMapper.findActiveIndispo(indispos, "PEI-999"));

    ObjectNode payload =
        IndispoTemporaireMapper.buildUpdatePayload(
            mapper, "PEI-001", activeIndispo, LocalDateTime.of(2026, 6, 5, 12, 45));
    Integer responseCode =
        requestManager.sendRequest(
            "PUT",
            apiEndpoints.indispoTemporaire(IndispoTemporaireMapper.getIndispoId(activeIndispo)),
            payload.toString());

    assertEquals(Integer.valueOf(200), responseCode);
    assertEquals(1, getCalls.get());
    assertEquals(1, putCalls.get());
  }

  private RequestManager createRequestManager() {
    return new RequestManager(
        ImmutableApiSettings.builder()
            .host(serverBaseUrl())
            .authType("keycloak")
            .keycloakUrl(serverBaseUrl())
            .keycloakRealm("remocra")
            .keycloakClientId("sync-remocra")
            .keycloakClientSecret("top-secret")
            .build(),
        apiEndpoints,
        (codeErreur, message, idMessage) -> {});
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
