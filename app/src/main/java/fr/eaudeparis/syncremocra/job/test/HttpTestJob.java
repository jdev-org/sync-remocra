package fr.eaudeparis.syncremocra.job.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.net.HttpHeaders;
import fr.eaudeparis.syncremocra.job.Job;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.inject.Inject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value.Enclosing
public class HttpTestJob implements Job {

  private static Logger logger = LoggerFactory.getLogger(HttpTestJob.class);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Inject
  public HttpTestJob(HttpClient httpClient, ObjectMapper objectMapper) {
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
  }

  public int run() {
    logger.info("Avant consommation WS");
    ThematiquesResponse tr;
    try {
      tr = readThematiques();
      logger.info(
          tr.data().length
              + " thématiques chargées sur "
              + tr.total()
              + ". Réponse globale : "
              + tr);
      return 0;
    } catch (Exception e) {
      logger.error("Erreur de chargement des thématiques", e);
      return 1;
    }
  }

  protected ThematiquesResponse readThematiques() throws Exception {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://bspp-remocra-preprod.atolcd.com/remocra/thematiques.json"))
            .setHeader(HttpHeaders.ACCEPT, "application/json")
            .build();

    final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
    if (response.statusCode() != 200) {
      throw new RuntimeException("Http response KO: " + response);
    }
    return objectMapper.readValue(response.body(), ThematiquesResponse.class);
  }

  @JsonDeserialize(as = ImmutableHttpTestJob.ThematiquesResponse.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Value.Immutable
  interface ThematiquesResponse {
    @JsonProperty("success")
    boolean success();

    @JsonProperty("message")
    @Nullable
    String message();

    @JsonProperty("total")
    @Nullable
    Integer total();

    @JsonProperty("data")
    @Nullable
    Thematique[] data();
  }

  @JsonDeserialize(as = ImmutableHttpTestJob.Thematique.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  @Value.Immutable
  interface Thematique {
    @JsonProperty("id")
    Integer id();

    @JsonProperty("code")
    String code();

    @JsonProperty("nom")
    String nom();

    @JsonProperty("version")
    @Nullable
    Integer version();
  }
}
