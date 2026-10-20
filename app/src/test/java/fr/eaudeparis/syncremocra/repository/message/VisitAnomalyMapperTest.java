package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

/** Verifies the visit anomaly mappings whose REMOcRA codes are validated. */
public class VisitAnomalyMapperTest {

  @Test
  public void shouldClearEveryAccessibleAnomalyForControleRealise() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map(
            "PIQP - CONTROLE REALISE", Arrays.asList("BSPP_APDP", "BSPP_APSE", "BSPP_BISD"));

    assertEquals(
        Arrays.asList("BSPP_APDP", "BSPP_APSE", "BSPP_BISD"), anomalies.getAnomaliesControlees());
    assertEquals(Collections.emptyList(), anomalies.getAnomaliesConstatees());
  }

  @Test
  public void shouldClearEveryAccessibleAnomalyForEnService() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map("NP - EN SERVICE", Arrays.asList("BSPP_APDP", "BSPP_PISD"));

    assertEquals(Arrays.asList("BSPP_APDP", "BSPP_PISD"), anomalies.getAnomaliesControlees());
    assertEquals(Collections.emptyList(), anomalies.getAnomaliesConstatees());
  }

  @Test
  public void shouldMapDeposeOuEnTravauxToBsppApdp() {
    assertMappedInBothLists("CTP - DEPOSE OU EN TRAVAUX", "BSPP_APDP");
  }

  @Test
  public void shouldMapARenouvelerToBsppApdp() {
    assertMappedInBothLists("NP - A RENOUVELER", "BSPP_APDP");
  }

  @Test
  public void shouldMapSansEauToBsppApse() {
    assertMappedInBothLists("PICF - SANS EAU", "BSPP_APSE");
  }

  @Test
  public void shouldMapPbSignaletiqueToBsppBisdForBoucheIncendie() {
    assertMappedInBothLists("NP - PB SIGNALETIQUE", "BSPP_BISD");
  }

  @Test
  public void shouldMapPbSignaletiqueToBsppPisdForPoteauIncendie() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map("CTP - PB SIGNALETIQUE", Arrays.asList("BSPP_PISD", "BSPP_APSE"));

    assertEquals(Collections.singletonList("BSPP_PISD"), anomalies.getAnomaliesControlees());
    assertEquals(Collections.singletonList("BSPP_PISD"), anomalies.getAnomaliesConstatees());
  }

  @Test
  public void shouldLeaveActionsWithoutValidatedCodeUnmapped() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map("NP - ENCOMBRE OU INACCESSIBLE", Collections.emptyList());

    assertEquals(Collections.emptyList(), anomalies.getAnomaliesControlees());
    assertEquals(Collections.emptyList(), anomalies.getAnomaliesConstatees());
  }

  private void assertMappedInBothLists(String localVisitType, String expectedCode) {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map(localVisitType, Collections.singletonList(expectedCode));

    assertEquals(Collections.singletonList(expectedCode), anomalies.getAnomaliesControlees());
    assertEquals(Collections.singletonList(expectedCode), anomalies.getAnomaliesConstatees());
  }
}
