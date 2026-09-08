package fr.eaudeparis.syncremocra.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ImmutableApiSettings;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.PullHydrant;
import fr.eaudeparis.syncremocra.repository.pullmessage.PullHydrantMapper;
import fr.eaudeparis.syncremocra.repository.pullmessage.model.PeiDiffModel;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Test;

public class RemocraPullPeiContractTest {

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
  public void shouldReadDiffThenFetchPeiAndCaracteristiquesUsingV3Contract() throws Exception {
    AtomicInteger diffCalls = new AtomicInteger();
    AtomicInteger peiCalls = new AtomicInteger();
    AtomicInteger caracCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/pei/diff",
        exchange -> {
          diffCalls.incrementAndGet();
          assertEquals("moment=2026-06-05%2008%3A00%3A00", exchange.getRequestURI().getRawQuery());
          respond(
              exchange,
              200,
              "[{\"numeroComplet\":\"PEI-001\","
                  + "\"momentModification\":\"2026-06-05T08:15:00Z\","
                  + "\"auteurModification\":\"SDIS\","
                  + "\"auteur\":{\"typeSourceModification\":\"API\"},"
                  + "\"typeOperation\":\"UPDATE\","
                  + "\"typeObjet\":\"PEI\"}]");
        });
    server.createContext(
        "/deci/pei/PEI-001",
        exchange -> {
          peiCalls.incrementAndGet();
          respond(
              exchange,
              200,
              "{"
                  + "\"peiComplementAdresse\":\"Cour interieure\","
                  + "\"peiDisponibiliteTerrestre\":\"AVAILABLE\","
                  + "\"penaDisponibiliteHbe\":\"LIMITED\","
                  + "\"peiNumeroVoie\":\"24\","
                  + "\"peiSuffixeVoie\":\"TER\","
                  + "\"peiNiveauId\":\"level-uuid\","
                  + "\"peiVoieTexte\":\"Rue du Contrat\","
                  + "\"peiCroisementId\":\"crossroad-uuid\","
                  + "\"peiEnFace\":false,"
                  + "\"peiDomaineId\":\"domaine-uuid\","
                  + "\"peiCommuneId\":\"commune-uuid\","
                  + "\"peiNatureId\":\"nature-uuid\","
                  + "\"peiNatureDeciId\":\"nature-deci-uuid\","
                  + "\"peiIndispoTemporaire\":true,"
                  + "\"peiAnneeFabrication\":\"2004\""
                  + "}");
        });
    server.createContext(
        "/deci/pei/PEI-001/caracteristiques",
        exchange -> {
          caracCalls.incrementAndGet();
          respond(
              exchange,
              200,
              "{"
                  + "\"pibiDiametreId\":\"diametre-uuid\","
                  + "\"pibiMarqueId\":\"marque-uuid\","
                  + "\"pibiModeleId\":\"modele-uuid\","
                  + "\"pibiDiametreCanalisation\":\"200\""
                  + "}");
        });
    server.start();

    RequestManager requestManager = createRequestManager();
    String diffJson =
        requestManager.sendGetRequest(
            apiEndpoints.peiDiff(), Map.of("moment", "2026-06-05 08:00:00"));

    List<PeiDiffModel> diffs =
        mapper.readValue(diffJson, new TypeReference<List<PeiDiffModel>>() {});
    assertEquals(1, diffs.size());
    assertEquals("PEI-001", diffs.get(0).getNumero());
    assertEquals("CARACTERISTIQUES", diffs.get(0).getType());
    assertEquals("UPDATE", diffs.get(0).getOperation());
    assertTrue(diffs.get(0).getDateModification().getTime() > 0);

    Map<String, Object> pei =
        mapper.readValue(
            requestManager.sendGetRequest(apiEndpoints.pei("PEI-001")),
            new TypeReference<Map<String, Object>>() {});
    Map<String, Object> carac =
        mapper.readValue(
            requestManager.sendGetRequest(apiEndpoints.peiCaracteristiques("PEI-001")),
            new TypeReference<Map<String, Object>>() {});

    PullHydrant hydrant = PullHydrantMapper.map(pei, carac);

    assertEquals("diametre-uuid", hydrant.getDiametre());
    assertEquals("marque-uuid", hydrant.getMarque());
    assertEquals("modele-uuid", hydrant.getModele());
    assertEquals(Integer.valueOf(200), hydrant.getDiametreCanalisation());
    assertEquals("Cour interieure", hydrant.getComplement());
    assertEquals("AVAILABLE", hydrant.getDispoTerrestre());
    assertEquals("LIMITED", hydrant.getDispoHbe());
    assertEquals(Integer.valueOf(24), hydrant.getNumeroVoie());
    assertEquals("TER", hydrant.getSuffixeVoie());
    assertEquals("Rue du Contrat", hydrant.getVoie());
    assertEquals("nature-uuid", hydrant.getNature());
    assertEquals(Boolean.TRUE, hydrant.getIndispoTemporaire());
    assertEquals(1, diffCalls.get());
    assertEquals(1, peiCalls.get());
    assertEquals(1, caracCalls.get());
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
