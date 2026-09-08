package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class IndispoTemporaireMapperTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void shouldBuildV3CreatePayload() {
    ObjectNode payload =
        IndispoTemporaireMapper.buildCreatePayload(
            mapper, "PEI-001", LocalDateTime.of(2026, 6, 5, 10, 15));

    assertEquals("Mise en indisponibilité Eau de Paris", payload.get("motif").asText());
    assertEquals(true, payload.get("mailAvantIndisponibilite").asBoolean());
    assertEquals(true, payload.get("mailApresIndisponibilite").asBoolean());
    assertEquals(true, payload.get("basculeAutoDisponible").asBoolean());
    assertEquals(true, payload.get("basculeAutoIndisponible").asBoolean());
    assertEquals("PEI-001", payload.get("listeNumeroPei").get(0).asText());
    assertTrue(payload.get("dateDebut").asText().startsWith("2026-06-05T10:15:00"));
  }

  @Test
  public void shouldBuildV3UpdatePayload() {
    Map<String, Object> v3Indispo = new HashMap<>();
    v3Indispo.put("indisponibiliteTemporaireId", "uuid-1");
    v3Indispo.put("indisponibiliteTemporaireDateDebut", "2026-06-05T09:00:00+02:00");

    ObjectNode payload =
        IndispoTemporaireMapper.buildUpdatePayload(
            mapper, "PEI-001", v3Indispo, LocalDateTime.of(2026, 6, 5, 11, 30));

    assertEquals("2026-06-05T09:00:00+02:00", payload.get("dateDebut").asText());
    assertTrue(payload.get("dateFin").asText().startsWith("2026-06-05T11:30:00"));
    assertEquals("uuid-1", String.valueOf(IndispoTemporaireMapper.getIndispoId(v3Indispo)));
    assertEquals("2026-06-05T09:00:00+02:00", IndispoTemporaireMapper.getDateDebut(v3Indispo));
  }

  @Test
  public void shouldFindActiveIndispoFromV3Contract() {
    List<Map<String, Object>> indispos = new ArrayList<Map<String, Object>>();

    Map<String, Object> v3Active = new HashMap<>();
    v3Active.put("indisponibiliteTemporaireId", "uuid-1");
    v3Active.put("listeNumeroPei", List.of("PEI-001", "PEI-002"));
    v3Active.put("indisponibiliteTemporaireDateDebut", "2026-06-05T09:00:00+02:00");
    indispos.add(v3Active);

    Map<String, Object> activeIndispo =
        IndispoTemporaireMapper.findActiveIndispo(indispos, "PEI-001");

    assertNotNull(activeIndispo);
    assertEquals("uuid-1", String.valueOf(activeIndispo.get("indisponibiliteTemporaireId")));
    assertNull(IndispoTemporaireMapper.findActiveIndispo(indispos, "PEI-999"));
  }
}
