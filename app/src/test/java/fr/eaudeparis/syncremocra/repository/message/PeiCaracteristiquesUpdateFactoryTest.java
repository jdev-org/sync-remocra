package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TracabilitePei;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PeiCaracteristiquesUpdateFactoryTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final ApiEndpoints apiEndpoints = new ApiEndpoints();

  @Test
  public void shouldBuildPibiUpdateFromV3Fields() {
    TracabilitePei traca = new TracabilitePei();
    traca.setTypeObjet("PIBI");
    traca.setDiametreCanalisation("200");

    Map<String, Object> data = new HashMap<>();
    data.put("pibiJumele", "PEI-002");
    data.put("dispositifInviolabilite", true);
    data.put("renversable", false);
    data.put("anneeFabrication", 2010);
    data.put("typeReseauCode", "AEP");
    data.put("typeCanalisationCode", "FONTE");
    data.put("surpresse", true);
    data.put("additive", false);

    PeiCaracteristiquesUpdate update =
        PeiCaracteristiquesUpdateFactory.create(
            mapper, apiEndpoints, PeiType.PIBI, "PEI-001", traca, data, "DN100", "BAYARD", "M1");

    assertEquals("/deci/pei/PEI-001/pibi-caracteristiques", update.getPath());
    assertEquals("DN100", update.getPayload().get("codeDiametre").asText());
    assertEquals(200, update.getPayload().get("diametreCanalisation").asInt());
    assertEquals("PEI-002", update.getPayload().get("peiJumele").asText());
    assertEquals(true, update.getPayload().get("inviolabilite").asBoolean());
    assertEquals(false, update.getPayload().get("renversable").asBoolean());
    assertEquals("BAYARD", update.getPayload().get("codeMarque").asText());
    assertEquals("M1", update.getPayload().get("codeModele").asText());
    assertEquals(2010, update.getPayload().get("anneeFabrication").asInt());
    assertEquals("AEP", update.getPayload().get("codeTypeReseau").asText());
    assertEquals("FONTE", update.getPayload().get("codeTypeCanalisation").asText());
    assertEquals(true, update.getPayload().get("reseauSurpresse").asBoolean());
    assertEquals(false, update.getPayload().get("reseauAdditive").asBoolean());
  }

  @Test
  public void shouldBuildPenaUpdateFromV3Fields() {
    TracabilitePei traca = new TracabilitePei();
    traca.setTypeObjet("PENA");

    Map<String, Object> data = new HashMap<>();
    data.put("capaciteIllimitee", true);
    data.put("capaciteIncertaine", false);
    data.put("capacite", 120);
    data.put("quantiteAppoint", 3.5d);
    data.put("materiauCode", "BETON");
    data.put("equipeHBE", true);

    PeiCaracteristiquesUpdate update =
        PeiCaracteristiquesUpdateFactory.create(
            mapper, apiEndpoints, PeiType.PENA, "PEI-003", traca, data, null, null, null);

    assertEquals("/deci/pei/PEI-003/pena-caracteristiques", update.getPath());
    assertEquals(true, update.getPayload().get("capaciteIllimitee").asBoolean());
    assertEquals(false, update.getPayload().get("capaciteIncertaine").asBoolean());
    assertEquals(120, update.getPayload().get("capacite").asInt());
    assertEquals(3.5d, update.getPayload().get("quantiteAppoint").asDouble(), 0.001d);
    assertEquals("BETON", update.getPayload().get("codeMateriau").asText());
    assertEquals(true, update.getPayload().get("equipeHBE").asBoolean());
  }

  @Test
  public void shouldFallbackToLegacyFields() {
    TracabilitePei traca = new TracabilitePei();
    traca.setTypeObjet("PIBI");
    traca.setDiametreCanalisation("150");

    Map<String, Object> data = new HashMap<>();
    data.put("jumelage", "PEI-LEGACY");
    data.put("inviolabilite", false);
    data.put("renversable", true);
    data.put("natureReseau", "AEP-LEGACY");
    data.put("natureCanalisation", "PVC");
    data.put("reseauSurpresse", false);
    data.put("reseauAdditive", true);

    PeiCaracteristiquesUpdate update =
        PeiCaracteristiquesUpdateFactory.create(
            mapper, apiEndpoints, PeiType.PIBI, "PEI-004", traca, data, "DN80", "M", "X");

    assertEquals("/deci/pei/PEI-004/pibi-caracteristiques", update.getPath());
    assertEquals("PEI-LEGACY", update.getPayload().get("peiJumele").asText());
    assertEquals(false, update.getPayload().get("inviolabilite").asBoolean());
    assertEquals(true, update.getPayload().get("renversable").asBoolean());
    assertEquals("AEP-LEGACY", update.getPayload().get("codeTypeReseau").asText());
    assertEquals("PVC", update.getPayload().get("codeTypeCanalisation").asText());
    assertEquals(false, update.getPayload().get("reseauSurpresse").asBoolean());
    assertEquals(true, update.getPayload().get("reseauAdditive").asBoolean());
  }
}
