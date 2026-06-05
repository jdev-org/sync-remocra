package fr.eaudeparis.syncremocra.repository.pei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class RemocraAnomalieMapperTest {

  @Test
  public void shouldReadV3AnomalieFields() {
    Map<String, Object> anomalie = new HashMap<>();
    anomalie.put("anomalieCode", "BSPP_PISD");
    anomalie.put("poidsAnomalieValIndispoTerrestre", 5);
    anomalie.put("listTypeVisite", Arrays.asList("CTP", "NP"));

    assertEquals("BSPP_PISD", RemocraAnomalieMapper.getCode(anomalie));
    assertTrue(RemocraAnomalieMapper.isBloquante(anomalie));
    assertTrue(RemocraAnomalieMapper.supportsTypeVisite(anomalie, "CTP"));
    assertFalse(RemocraAnomalieMapper.supportsTypeVisite(anomalie, "ROP"));
  }

  @Test
  public void shouldHandleNonBlockingAnomalies() {
    Map<String, Object> anomalie = new HashMap<>();
    anomalie.put("anomalieCode", "BSPP_TEST");
    anomalie.put("poidsAnomalieValIndispoTerrestre", 2);

    assertFalse(RemocraAnomalieMapper.isBloquante(anomalie));
  }

  @Test
  public void shouldAllowAnomaliesWithoutVisitTypeRestriction() {
    Map<String, Object> anomalie = new HashMap<>();
    anomalie.put("anomalieCode", "BSPP_TEST");
    anomalie.put("poidsAnomalieValIndispoTerrestre", 5);

    assertTrue(RemocraAnomalieMapper.supportsTypeVisite(anomalie, "NP"));
  }
}
