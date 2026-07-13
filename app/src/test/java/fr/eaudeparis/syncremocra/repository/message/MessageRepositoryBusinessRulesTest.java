package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TracabilitePei;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class MessageRepositoryBusinessRulesTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void shouldClearAllAnomaliesForControlRealiseAndEnService() {
    assertTrue(
        MessageRepository.shouldClearAllAnomalies(
            "Contrôle périodique - Point eau incendie Controle Réalisé"));
    assertTrue(MessageRepository.shouldClearAllAnomalies("PEI EN SERVICE"));
    assertFalse(MessageRepository.shouldClearAllAnomalies("Intervention PB signaletique"));
    assertFalse(MessageRepository.shouldClearAllAnomalies(null));
  }

  @Test
  public void shouldAppendHydraulicMeasurementsWhenPresent() {
    TracabilitePei traca = new TracabilitePei();
    traca.setEssaiPressionStatique(BigDecimal.valueOf(2.4d));
    traca.setEssaiPressionDynamique(BigDecimal.valueOf(1.3d));
    traca.setEssaiDebit(BigDecimal.valueOf(90));

    ObjectNode payload =
        MessageRepository.appendHydraulicMeasurementsIfPresent(mapper.createObjectNode(), traca);

    assertTrue(MessageRepository.hasHydraulicMeasurements(traca));
    assertEquals(2.4d, payload.get("pression").asDouble(), 0.0d);
    assertEquals(1.3d, payload.get("pressionDynamique").asDouble(), 0.0d);
    assertEquals(90, payload.get("debit").asInt());
  }

  @Test
  public void shouldKeepPayloadUnchangedWhenHydraulicMeasurementsAreMissing() {
    TracabilitePei traca = new TracabilitePei();
    ObjectNode payload = mapper.createObjectNode();
    payload.put("typeVisite", "NP");

    ObjectNode enrichedPayload =
        MessageRepository.appendHydraulicMeasurementsIfPresent(payload, traca);

    assertFalse(MessageRepository.hasHydraulicMeasurements(traca));
    assertEquals("NP", enrichedPayload.get("typeVisite").asText());
    assertFalse(enrichedPayload.has("pression"));
    assertFalse(enrichedPayload.has("pressionDynamique"));
    assertFalse(enrichedPayload.has("debit"));
  }

  @Test
  public void shouldCreateTemporaryUnavailabilityOnlyForAuthorizedMotifs() {
    assertTrue(
        MessageRepository.shouldCreateTemporaryUnavailability(
            Arrays.asList("APPAREIL A RENOUVELER")));
    assertTrue(
        MessageRepository.shouldCreateTemporaryUnavailability(
            Arrays.asList("ARRET EAU", "INACCESSIBLE : SOUS TERRASSE")));
    assertFalse(
        MessageRepository.shouldCreateTemporaryUnavailability(Arrays.asList("SANS EAU")));
    assertFalse(
        MessageRepository.shouldCreateTemporaryUnavailability(
            Arrays.asList("APP CHANTIER", "INACCESSIBLE : DANS EMPRISE DE CHANTIER")));
    assertFalse(
        MessageRepository.shouldCreateTemporaryUnavailability(Collections.emptyList()));
    assertFalse(MessageRepository.shouldCreateTemporaryUnavailability(null));
  }
}
