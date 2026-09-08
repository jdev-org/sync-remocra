package fr.eaudeparis.syncremocra.repository.pei;

import static org.junit.Assert.assertEquals;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.api.ImmutableApiSettings;
import fr.eaudeparis.syncremocra.util.RequestManager;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Test;

public class PeiRepositoryContractTest {

  private HttpServer server;

  @After
  public void tearDown() {
    if (server != null) {
      server.stop(0);
      server = null;
    }
  }

  @Test
  public void shouldResolveV3NatureAndFilterAnomaliesByVisitType() throws Exception {
    AtomicInteger anomaliesCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/pei/PEI-001", exchange -> respond(exchange, 200, "{\"peiNatureId\":\"pena-id\"}"));
    server.createContext(
        "/deci/referentiel/pibi/naturesPEI",
        exchange -> respond(exchange, 200, "[{\"natureId\":\"pibi-id\",\"natureCode\":\"PI\"}]"));
    server.createContext(
        "/deci/referentiel/pena/naturesPEI",
        exchange -> respond(exchange, 200, "[{\"natureId\":\"pena-id\",\"natureCode\":\"PA\"}]"));
    server.createContext(
        "/deci/referentiel/pena/PA/naturesAnomalies",
        exchange -> {
          anomaliesCalls.incrementAndGet();
          assertEquals("typeVisite=NP", exchange.getRequestURI().getRawQuery());
          respond(
              exchange,
              200,
              "[{\"anomalieCode\":\"AN1\",\"poidsAnomalieValIndispoTerrestre\":5,"
                  + "\"listTypeVisite\":[\"NP\"]},"
                  + "{\"anomalieCode\":\"AN2\",\"poidsAnomalieValIndispoTerrestre\":3,"
                  + "\"listTypeVisite\":[\"NP\",\"CTP\"]},"
                  + "{\"anomalieCode\":\"AN3\",\"poidsAnomalieValIndispoTerrestre\":5,"
                  + "\"listTypeVisite\":[\"CTP\"]}]");
        });
    server.start();

    PeiRepository repository = createRepository();

    ArrayList<String> bloquantes = repository.getNaturesAnomaliesAccessibles("PEI-001", "NP", true);
    ArrayList<String> allAccessible =
        repository.getNaturesAnomaliesAccessibles("PEI-001", "NP", false);

    assertEquals(new ArrayList<String>(Arrays.asList("AN1")), bloquantes);
    assertEquals(new ArrayList<String>(Arrays.asList("AN1", "AN2")), allAccessible);
    assertEquals(2, anomaliesCalls.get());
  }

  @Test
  public void shouldReturnEmptyWhenNatureCannotBeResolved() throws Exception {
    AtomicInteger anomaliesCalls = new AtomicInteger();

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/realms/remocra/protocol/openid-connect/token",
        exchange -> respond(exchange, 200, "{\"access_token\":\"kc-token\",\"expires_in\":300}"));
    server.createContext(
        "/deci/pei/PEI-404", exchange -> respond(exchange, 200, "{\"peiNatureId\":\"unknown\"}"));
    server.createContext(
        "/deci/referentiel/pibi/naturesPEI",
        exchange -> respond(exchange, 200, "[{\"natureId\":\"pibi-id\",\"natureCode\":\"PI\"}]"));
    server.createContext(
        "/deci/referentiel/pena/naturesPEI",
        exchange -> respond(exchange, 200, "[{\"natureId\":\"pena-id\",\"natureCode\":\"PA\"}]"));
    server.createContext(
        "/deci/referentiel/pena/PA/naturesAnomalies",
        exchange -> {
          anomaliesCalls.incrementAndGet();
          respond(exchange, 200, "[]");
        });
    server.start();

    PeiRepository repository = createRepository();

    assertEquals(
        new ArrayList<String>(), repository.getNaturesAnomaliesAccessibles("PEI-404", "NP", false));
    assertEquals(0, anomaliesCalls.get());
  }

  private PeiRepository createRepository() {
    PeiRepository repository = new PeiRepository(null);
    repository.apiEndpoints = new ApiEndpoints();
    repository.requestManager = createRequestManager(repository.apiEndpoints);
    return repository;
  }

  private RequestManager createRequestManager(ApiEndpoints apiEndpoints) {
    try {
      Constructor<?> constructor = null;
      for (Constructor<?> candidate : RequestManager.class.getDeclaredConstructors()) {
        if (candidate.getParameterCount() == 3) {
          constructor = candidate;
          break;
        }
      }
      if (constructor == null) {
        throw new AssertionError("Test constructor not found on RequestManager");
      }
      constructor.setAccessible(true);
      Class<?> reporterType = constructor.getParameterTypes()[2];
      Object reporter =
          Proxy.newProxyInstance(
              reporterType.getClassLoader(),
              new Class<?>[] {reporterType},
              (proxy, method, args) -> null);
      return (RequestManager)
          constructor.newInstance(
              ImmutableApiSettings.builder()
                  .host(serverBaseUrl())
                  .authType("keycloak")
                  .keycloakUrl(serverBaseUrl())
                  .keycloakRealm("remocra")
                  .keycloakClientId("sync-remocra")
                  .keycloakClientSecret("top-secret")
                  .build(),
              apiEndpoints,
              reporter);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError("Unable to instantiate RequestManager for contract test", e);
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
