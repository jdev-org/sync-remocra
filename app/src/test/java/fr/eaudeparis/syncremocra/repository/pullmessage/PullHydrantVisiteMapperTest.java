package fr.eaudeparis.syncremocra.repository.pullmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PullHydrantVisiteMapperTest {

  @Test
  public void shouldResolveVisitIdentifiersFromBothContracts() {
    Map<String, Object> legacyVisite = new HashMap<>();
    legacyVisite.put("identifiant", "42");

    Map<String, Object> v3Visite = new HashMap<>();
    v3Visite.put("visiteId", "uuid-1");

    assertEquals("42", PullHydrantVisiteMapper.getVisiteId(legacyVisite));
    assertEquals("uuid-1", PullHydrantVisiteMapper.getVisiteId(v3Visite));
  }

  @Test
  public void shouldResolveVisitTypeAndMeasurementsFromBothContracts() {
    Map<String, Object> v3Visite = new HashMap<>();
    v3Visite.put("typeVisite", "CTP");
    v3Visite.put("pressionDynamique", 1.5d);
    v3Visite.put("pressionDynamiqueDebitMax", 0.8d);

    assertEquals("CTRL", PullHydrantVisiteMapper.getLocalVisitType(v3Visite));
    assertEquals(
        Double.valueOf(1.5d),
        PullHydrantVisiteMapper.getDouble(v3Visite, "pressionDyn", "pressionDynamique"));
    assertEquals(
        Double.valueOf(0.8d),
        PullHydrantVisiteMapper.getDouble(v3Visite, "pressionDynDeb", "pressionDynamiqueDebitMax"));
  }

  @Test
  public void shouldResolveVisitDatesFromLegacyAndV3Contracts() {
    Map<String, Object> legacyVisite = new HashMap<>();
    legacyVisite.put("date", "2026-06-05 09:30");

    Map<String, Object> v3Visite = new HashMap<>();
    v3Visite.put("moment", "2026-06-05T09:30:00+02:00");

    assertEquals(
        LocalDateTime.of(2026, 6, 5, 9, 30), PullHydrantVisiteMapper.getVisitDate(legacyVisite));
    assertEquals(
        LocalDateTime.of(2026, 6, 5, 9, 30), PullHydrantVisiteMapper.getVisitDate(v3Visite));
    assertNull(PullHydrantVisiteMapper.getVisitDate(new HashMap<>()));
  }
}
