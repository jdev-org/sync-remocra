package fr.eaudeparis.syncremocra.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApiEndpointsTest {

  private final ApiEndpoints apiEndpoints = new ApiEndpoints();

  @Test
  public void shouldBuildPeiEndpoints() {
    assertEquals("/deci/pei/diff", apiEndpoints.peiDiff());
    assertEquals("/deci/pei/PEI-001", apiEndpoints.pei("PEI-001"));
    assertEquals("/deci/pei/PEI-001/caracteristiques", apiEndpoints.peiCaracteristiques("PEI-001"));
    assertEquals("/deci/pei/PEI-001/visites", apiEndpoints.peiVisites("PEI-001"));
    assertEquals("/deci/pei/PEI-001/visites/42", apiEndpoints.peiVisite("PEI-001", 42));
  }

  @Test
  public void shouldBuildReferentielAndIndispoEndpoints() {
    assertEquals("/deci/indispoTemporaire", apiEndpoints.indispoTemporaire());
    assertEquals("/deci/indispoTemporaire/99", apiEndpoints.indispoTemporaire(99));
    assertEquals(
        "/deci/referentiel/pibi/NAT-01/naturesAnomalies",
        apiEndpoints.referentielNaturesAnomalies("pibi", "NAT-01"));
    assertEquals("/authentication/jwt", apiEndpoints.authenticationJwt());
  }
}
