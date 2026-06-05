package fr.eaudeparis.syncremocra.repository.pullmessage;

import fr.eaudeparis.syncremocra.db.model.tables.pojos.PullHydrant;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.util.Map;

/**
 * Mappe les réponses REMOcRA v2/v3 vers le schéma applicatif local des hydrants en attente.
 *
 * <p>La base locale conserve encore le modèle historique. Tant que le schéma n'évolue pas, les
 * identifiants v3 de type UUID ou code sont donc stockés tels quels dans les colonnes texte
 * existantes.
 */
public final class PullHydrantMapper {

  private PullHydrantMapper() {}

  /**
   * Construit une vue hydrant compatible avec le schéma local à partir des réponses PEI et
   * caractéristiques.
   *
   * @param dataPei réponse du endpoint `/deci/pei/{numeroComplet}`
   * @param dataPeiCarac réponse du endpoint `/deci/pei/{numeroComplet}/caracteristiques`
   * @return données hydrant prêtes à être persistées
   */
  public static PullHydrant map(Map<String, Object> dataPei, Map<String, Object> dataPeiCarac) {
    PullHydrant hydrant = new PullHydrant();
    hydrant.setDiametre(getString(dataPeiCarac, "diametre", "pibiDiametreId"));
    hydrant.setMarque(getString(dataPeiCarac, "marque", "pibiMarqueId"));
    hydrant.setModele(getString(dataPeiCarac, "modele", "pibiModeleId"));
    hydrant.setDiametreCanalisation(
        getInteger(dataPeiCarac, "diametreCanalisation", "pibiDiametreCanalisation"));
    hydrant.setAnneeFabrication(getString(dataPeiCarac, "anneeFabrication", "peiAnneeFabrication"));
    if (hydrant.getAnneeFabrication() == null) {
      hydrant.setAnneeFabrication(getString(dataPei, "anneeFabrication", "peiAnneeFabrication"));
    }
    hydrant.setComplement(getString(dataPei, "complement", "peiComplementAdresse"));
    hydrant.setDispoTerrestre(getString(dataPei, "dispoTerrestre", "peiDisponibiliteTerrestre"));
    hydrant.setDispoHbe(getString(dataPei, "dispoAerienne", "penaDisponibiliteHbe"));
    hydrant.setNumeroVoie(getInteger(dataPei, "numeroVoie", "peiNumeroVoie"));
    hydrant.setSuffixeVoie(getString(dataPei, "suffixeVoie", "peiSuffixeVoie"));
    hydrant.setNiveau(getString(dataPei, "niveau", "peiNiveauId"));
    hydrant.setVoie(getString(dataPei, "voie", "peiVoieTexte"));
    hydrant.setVoie2(getString(dataPei, "carrefour", "peiCroisementId"));
    hydrant.setEnFace(getBoolean(dataPei, "enFace", "peiEnFace"));
    hydrant.setDomaine(getString(dataPei, "domaine", "peiDomaineId"));
    hydrant.setCommune(getString(dataPei, "commune", "peiCommuneId"));
    hydrant.setNature(getString(dataPei, "nature", "peiNatureId"));
    hydrant.setNatureDeci(getString(dataPei, "natureDeci", "peiNatureDeciId"));
    hydrant.setIndispoTemporaire(getBoolean(dataPei, "indispoTemporaire", "peiIndispoTemporaire"));
    return hydrant;
  }

  private static String getString(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      String value = JSONUtil.getString(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Integer getInteger(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Integer value = parseInteger(JSONUtil.getString(data, key));
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Boolean getBoolean(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Boolean value = JSONUtil.getBoolean(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Integer parseInteger(String value) {
    if (value == null || value.isEmpty() || !value.matches("-?\\d+")) {
      return null;
    }
    return Integer.valueOf(value);
  }
}
