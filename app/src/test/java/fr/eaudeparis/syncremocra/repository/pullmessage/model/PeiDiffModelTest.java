package fr.eaudeparis.syncremocra.repository.pullmessage.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.Test;

public class PeiDiffModelTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void shouldDeserializeV3PeiDiffAsCaracteristiques() throws Exception {
    String json =
        "{"
            + "\"peiId\":\"11111111-1111-1111-1111-111111111111\","
            + "\"numeroComplet\":\"PEI-001\","
            + "\"momentModification\":\"2026-06-05T08:15:00Z\","
            + "\"auteurModification\":\"EAU_DE_PARIS\","
            + "\"auteur\":{\"typeSourceModification\":\"API\"},"
            + "\"typeOperation\":\"UPDATE\","
            + "\"typeObjet\":\"PEI\""
            + "}";

    PeiDiffModel model = objectMapper.readValue(json, PeiDiffModel.class);

    assertEquals("PEI-001", model.getNumero());
    assertEquals("UPDATE", model.getOperation());
    assertEquals("CARACTERISTIQUES", model.getType());
    assertEquals("API", model.getAuteurModificationFlag());
    assertEquals("EAU_DE_PARIS", model.getOrganismeModification());
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals("2026-06-05 08:15:00", dateFormat.format(model.getDateModification()));
    assertTrue(model.isModifiedByCurrentOrganisme("EAU_DE_PARIS"));
    assertFalse(model.isModifiedByCurrentOrganisme("AUTRE_ORGANISME"));
  }

  @Test
  public void shouldDeserializeV3VisiteDiffAsVisites() throws Exception {
    String json =
        "{"
            + "\"peiId\":\"22222222-2222-2222-2222-222222222222\","
            + "\"numeroComplet\":\"PEI-002\","
            + "\"momentModification\":\"2026-06-05T09:30:00Z\","
            + "\"auteurModification\":\"Jean Dupont\","
            + "\"auteur\":{\"typeSourceModification\":\"REMOCRA_WEB\"},"
            + "\"typeOperation\":\"CREATE\","
            + "\"typeObjet\":\"VISITE\""
            + "}";

    PeiDiffModel model = objectMapper.readValue(json, PeiDiffModel.class);

    assertEquals("PEI-002", model.getNumero());
    assertEquals("CREATE", model.getOperation());
    assertEquals("VISITES", model.getType());
    assertEquals("USER", model.getAuteurModificationFlag());
    assertEquals("Jean Dupont", model.getUtilisateurModification());
    assertFalse(model.isModifiedByCurrentOrganisme("EAU_DE_PARIS"));
  }
}
