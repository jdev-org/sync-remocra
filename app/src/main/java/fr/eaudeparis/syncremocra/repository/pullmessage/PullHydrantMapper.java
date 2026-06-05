package fr.eaudeparis.syncremocra.repository.pullmessage;

import fr.eaudeparis.syncremocra.db.model.tables.pojos.PullHydrant;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.util.Map;

/**
 * Mappe les réponses REMOcRA v3 vers le schéma applicatif local des hydrants en attente.
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
    hydrant.setDiametre(getString(dataPeiCarac, "pibiDiametreId"));
    hydrant.setMarque(getString(dataPeiCarac, "pibiMarqueId"));
    hydrant.setModele(getString(dataPeiCarac, "pibiModeleId"));
    hydrant.setDiametreCanalisation(getInteger(dataPeiCarac, "pibiDiametreCanalisation"));
    hydrant.setAnneeFabrication(getString(dataPeiCarac, "peiAnneeFabrication"));
    if (hydrant.getAnneeFabrication() == null) {
      hydrant.setAnneeFabrication(getString(dataPei, "peiAnneeFabrication"));
    }
    hydrant.setComplement(getString(dataPei, "peiComplementAdresse"));
    hydrant.setDispoTerrestre(getString(dataPei, "peiDisponibiliteTerrestre"));
    hydrant.setDispoHbe(getString(dataPei, "penaDisponibiliteHbe"));
    hydrant.setNumeroVoie(getInteger(dataPei, "peiNumeroVoie"));
    hydrant.setSuffixeVoie(getString(dataPei, "peiSuffixeVoie"));
    hydrant.setNiveau(getString(dataPei, "peiNiveauId"));
    hydrant.setVoie(getString(dataPei, "peiVoieTexte"));
    hydrant.setVoie2(getString(dataPei, "peiCroisementId"));
    hydrant.setEnFace(getBoolean(dataPei, "peiEnFace"));
    hydrant.setDomaine(getString(dataPei, "peiDomaineId"));
    hydrant.setCommune(getString(dataPei, "peiCommuneId"));
    hydrant.setNature(getString(dataPei, "peiNatureId"));
    hydrant.setNatureDeci(getString(dataPei, "peiNatureDeciId"));
    hydrant.setIndispoTemporaire(getBoolean(dataPei, "peiIndispoTemporaire"));
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
