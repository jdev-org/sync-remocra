package fr.eaudeparis.syncremocra.repository.pullmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.eaudeparis.syncremocra.db.model.tables.pojos.PullHydrant;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PullHydrantMapperTest {

  @Test
  public void shouldMapV3HydrantFields() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("peiComplementAdresse", "Cour interieure");
    pei.put("peiDisponibiliteTerrestre", "AVAILABLE");
    pei.put("penaDisponibiliteHbe", "LIMITED");
    pei.put("peiNumeroVoie", "24");
    pei.put("peiSuffixeVoie", "TER");
    pei.put("peiNiveauId", "7f97c8a3-1111-2222-3333-444444444444");
    pei.put("peiVoieTexte", "Rue du Contrat");
    pei.put("peiCroisementId", "88888888-9999-aaaa-bbbb-cccccccccccc");
    pei.put("peiEnFace", false);
    pei.put("peiDomaineId", "domaine-uuid");
    pei.put("peiCommuneId", "commune-uuid");
    pei.put("peiNatureId", "nature-uuid");
    pei.put("peiNatureDeciId", "nature-deci-uuid");
    pei.put("peiIndispoTemporaire", true);
    pei.put("peiAnneeFabrication", "2004");

    Map<String, Object> carac = new HashMap<>();
    carac.put("pibiDiametreId", "diametre-uuid");
    carac.put("pibiMarqueId", "marque-uuid");
    carac.put("pibiModeleId", "modele-uuid");
    carac.put("pibiDiametreCanalisation", "200");

    PullHydrant hydrant = PullHydrantMapper.map(pei, carac);

    assertEquals("diametre-uuid", hydrant.getDiametre());
    assertEquals("marque-uuid", hydrant.getMarque());
    assertEquals("modele-uuid", hydrant.getModele());
    assertEquals(Integer.valueOf(200), hydrant.getDiametreCanalisation());
    assertEquals("2004", hydrant.getAnneeFabrication());
    assertEquals("Cour interieure", hydrant.getComplement());
    assertEquals("AVAILABLE", hydrant.getDispoTerrestre());
    assertEquals("LIMITED", hydrant.getDispoHbe());
    assertEquals(Integer.valueOf(24), hydrant.getNumeroVoie());
    assertEquals("TER", hydrant.getSuffixeVoie());
    assertEquals("7f97c8a3-1111-2222-3333-444444444444", hydrant.getNiveau());
    assertEquals("Rue du Contrat", hydrant.getVoie());
    assertEquals("88888888-9999-aaaa-bbbb-cccccccccccc", hydrant.getVoie2());
    assertEquals(Boolean.FALSE, hydrant.getEnFace());
    assertEquals("domaine-uuid", hydrant.getDomaine());
    assertEquals("commune-uuid", hydrant.getCommune());
    assertEquals("nature-uuid", hydrant.getNature());
    assertEquals("nature-deci-uuid", hydrant.getNatureDeci());
    assertEquals(Boolean.TRUE, hydrant.getIndispoTemporaire());
  }

  @Test
  public void shouldIgnoreNonNumericStreetNumbers() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("peiNumeroVoie", "12B");

    PullHydrant hydrant = PullHydrantMapper.map(pei, new HashMap<>());

    assertNull(hydrant.getNumeroVoie());
  }
}
