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
  public void shouldMapReparerAppareilToBsppApdp() {
    assertMappedInBothLists("NP - RÉPARER APPAREIL", "BSPP_APDP");
  }

  @Test
  public void shouldMapSansEauToBsppApse() {
    assertMappedInBothLists("PICF - SANS EAU", "BSPP_APSE");
  }

  @Test
  public void shouldMapPbDebitPressionToDebitNonConforme() {
    assertMappedInBothLists("CTP - PB DEBIT / PRESSION", "DEBIT_NON_CONFORME");
  }

  @Test
  public void shouldMapCouvercleManquantDeterioreToBsppCpmd() {
    assertMappedInBothLists("NP - COUVERCLE MANQU/DETERIOR", "BSPP_CPMD");
  }

  @Test
  public void shouldMapAppareilDeterioreToSignalementWhenCtpPeiIsAvailable() {
    assertMappedInBothLists("CTP - APPAREIL DETERIORE", "BSPP_SINC", "Disponible");
  }

  @Test
  public void shouldMapAppareilDeterioreToApdpWhenCtpPeiIsUnavailable() {
    assertMappedInBothLists("CTP - APPAREIL DETERIORE", "BSPP_APDP", "Indisponible");
  }

  @Test
  public void shouldMapAppareilDeterioreToApdpForNp() {
    assertMappedInBothLists("NP - APPAREIL DETERIORE", "BSPP_APDP", "Disponible");
  }

  @Test
  public void shouldMapHorsServiceToBsppApse() {
    assertMappedInBothLists("NP - HORS SERVICE", "BSPP_APSE");
  }

  @Test
  public void shouldMapRemainingValidatedVisitActions() {
    assertMappedInBothLists("NP - DIFFICILE A MANOEUVRER", "BSPP_DIFF");
    assertMappedInBothLists("NP - EN ECOULEMENT", "BSPP_AREE");
    assertMappedInBothLists("NP - PEI INTROUVABLE", "BSPP_INTR");
  }

  @Test
  public void shouldMapEncombreToBothAccessibilityAnomalies() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map("NP - ENCOMBRE OU INACCESSIBLE", Collections.emptyList());

    assertEquals(
        Arrays.asList("VEHICULE_EN_STATIONNEMENT_SUR_BI", "BSPP_ACPV"),
        anomalies.getAnomaliesControlees());
    assertEquals(
        Arrays.asList("VEHICULE_EN_STATIONNEMENT_SUR_BI", "BSPP_ACPV"),
        anomalies.getAnomaliesConstatees());
  }

  @Test
  public void shouldMapDegorgerToBothWaterAnomalies() {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map("NP - DEGORGER COFFRE/COLONNE", Collections.emptyList());

    assertEquals(Arrays.asList("BSPP_PODC", "BSPP_PDCL"), anomalies.getAnomaliesControlees());
    assertEquals(Arrays.asList("BSPP_PODC", "BSPP_PDCL"), anomalies.getAnomaliesConstatees());
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
        VisitAnomalyMapper.map("NP - ACTION NON GEREE", Collections.emptyList());

    assertEquals(Collections.emptyList(), anomalies.getAnomaliesControlees());
    assertEquals(Collections.emptyList(), anomalies.getAnomaliesConstatees());
  }

  private void assertMappedInBothLists(String localVisitType, String expectedCode) {
    assertMappedInBothLists(localVisitType, expectedCode, null);
  }

  private void assertMappedInBothLists(
      String localVisitType, String expectedCode, String peiState) {
    VisitAnomalyMapper.VisitAnomalies anomalies =
        VisitAnomalyMapper.map(localVisitType, Collections.singletonList(expectedCode), peiState);

    assertEquals(Collections.singletonList(expectedCode), anomalies.getAnomaliesControlees());
    assertEquals(Collections.singletonList(expectedCode), anomalies.getAnomaliesConstatees());
  }
}
