package fr.eaudeparis.syncremocra.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ImmutableApiSettings;
import fr.eaudeparis.syncremocra.repository.message.VisitPayloadTracker;
import fr.eaudeparis.syncremocra.repository.message.VisitTypeMapper;
import fr.eaudeparis.syncremocra.repository.pullmessage.PullHydrantVisiteMapper;
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

public class RemocraVisitesContractTest {

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
  public void shouldSendVisitCreationUsingV3Payload() throws Exception {
    AtomicInteger authCalls = new AtomicInteger();
    AtomicInteger visitCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> {
          authCalls.incrementAndGet();
          respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}");
        });
    server.createContext(
        "/deci/pei/PEI-001/visites",
        exchange -> {
          visitCalls.incrementAndGet();
          assertEquals("POST", exchange.getRequestMethod());
          assertEquals("Bearer kc-token", exchange.getRequestHeaders().getFirst("Authorization"));

          String body =
              new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          Map<String, Object> payload =
              mapper.readValue(body, new TypeReference<Map<String, Object>>() {});

          assertEquals("CTP", payload.get("typeVisite"));
          assertEquals("Eau de Paris", payload.get("agent1"));
          assertEquals("2026-06-05 10:15", payload.get("date"));
          assertEquals(80, payload.get("debit"));
          assertEquals(
              1.7d, Double.valueOf(String.valueOf(payload.get("pressionDynamique"))), 0.0d);
          assertFalse(payload.containsKey("contexte"));

          respond(exchange, 201, "");
        });
    server.start();

    ObjectNode payload = mapper.createObjectNode();
    payload.put("typeVisite", VisitTypeMapper.toRemocraType("CTRL"));
    payload.put("date", "2026-06-05 10:15");
    payload.put("agent1", "Eau de Paris");
    payload.put("debit", 80);
    payload.put("pressionDynamique", 1.7d);

    RequestManager requestManager = createRequestManager();
    Integer codeRetour =
        requestManager.sendRequest("POST", apiEndpoints.peiVisites("PEI-001"), payload.toString());

    assertEquals(Integer.valueOf(201), codeRetour);
    assertEquals(1, authCalls.get());
    assertEquals(1, visitCalls.get());
  }

  @Test
  public void shouldReadVisitsUsingV3Contract() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/pei/PEI-001/visites",
        exchange ->
            respond(
                exchange,
                200,
                "[{\"visiteId\":\"uuid-1\",\"moment\":\"2026-06-05T09:30:00+02:00\","
                    + "\"typeVisite\":\"CTP\",\"anomalies\":[\"BSPP_APSE\"]}]"));
    server.createContext(
        "/deci/pei/PEI-001/visites/uuid-1",
        exchange ->
            respond(
                exchange,
                200,
                "{\"visiteId\":\"uuid-1\",\"moment\":\"2026-06-05T09:30:00+02:00\","
                    + "\"typeVisite\":\"CTP\",\"agent1\":\"Eau de Paris\",\"debit\":60,"
                    + "\"pression\":2.1,\"pressionDynamique\":1.4,"
                    + "\"pressionDynamiqueDebitMax\":0.9,\"anomaliesConstatees\":[\"BSPP_APSE\"]}"));
    server.start();

    RequestManager requestManager = createRequestManager();

    List<Map<String, Object>> visites =
        mapper.readValue(
            requestManager.sendGetRequest(apiEndpoints.peiVisites("PEI-001")),
            new TypeReference<List<Map<String, Object>>>() {});
    assertEquals("uuid-1", String.valueOf(PullHydrantVisiteMapper.getVisiteId(visites.get(0))));

    Map<String, Object> visiteDetail =
        mapper.readValue(
            requestManager.sendGetRequest(apiEndpoints.peiVisite("PEI-001", "uuid-1")),
            new TypeReference<Map<String, Object>>() {});

    assertEquals("CTRL", PullHydrantVisiteMapper.getLocalVisitType(visiteDetail));
    assertEquals(
        LocalDateTime.of(2026, 6, 5, 9, 30), PullHydrantVisiteMapper.getVisitDate(visiteDetail));
    assertEquals(
        Double.valueOf(1.4d),
        PullHydrantVisiteMapper.getDouble(visiteDetail, "pressionDyn", "pressionDynamique"));
    assertEquals(
        Double.valueOf(0.9d),
        PullHydrantVisiteMapper.getDouble(
            visiteDetail, "pressionDynDeb", "pressionDynamiqueDebitMax"));
    assertTrue(
        VisitPayloadTracker.toTrackedPayload(
                mapper.createObjectNode().put("typeVisite", "CTP").put("date", "2026-06-05 09:30"))
            .has("contexte"));
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
