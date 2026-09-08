package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

public class VisitPayloadTrackerTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void shouldAddLegacyContexteForTrackedPayload() {
    ObjectNode payload = mapper.createObjectNode();
    payload.put("typeVisite", "CTP");
    payload.put("date", "2026-06-05 10:15");

    ObjectNode trackedPayload = VisitPayloadTracker.toTrackedPayload(payload);

    assertFalse(payload.has("contexte"));
    assertEquals("CTRL", trackedPayload.get("contexte").asText());
    assertEquals("CTP", trackedPayload.get("typeVisite").asText());
  }
}
