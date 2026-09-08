package fr.eaudeparis.syncremocra.repository.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.eaudeparis.syncremocra.api.ApiEndpoints;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TracabilitePei;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.util.Map;

/** Fabrique le payload v3 des caractéristiques PEI en fonction du type PIBI/PENA. */
public final class PeiCaracteristiquesUpdateFactory {

  private PeiCaracteristiquesUpdateFactory() {}

  /**
   * Construit la mise à jour des caractéristiques à transmettre à REMOcRA.
   *
   * @param mapper sérialiseur JSON
   * @param apiEndpoints endpoints REMOcRA
   * @param reference numéro complet du PEI
   * @param traca données locales EDP
   * @param dataRemocra caractéristiques actuellement connues côté REMOcRA
   * @param codeDiametre code diamètre REMOcRA
   * @param codeMarque code marque REMOcRA
   * @param codeModele code modèle REMOcRA
   * @return endpoint cible et payload correspondant
   */
  public static PeiCaracteristiquesUpdate create(
      ObjectMapper mapper,
      ApiEndpoints apiEndpoints,
      PeiType peiType,
      String reference,
      TracabilitePei traca,
      Map<String, Object> dataRemocra,
      String codeDiametre,
      String codeMarque,
      String codeModele) {
    return PeiType.PENA.equals(peiType)
        ? createPenaUpdate(mapper, apiEndpoints, reference, dataRemocra)
        : createPibiUpdate(
            mapper,
            apiEndpoints,
            reference,
            traca,
            dataRemocra,
            codeDiametre,
            codeMarque,
            codeModele);
  }

  private static PeiCaracteristiquesUpdate createPibiUpdate(
      ObjectMapper mapper,
      ApiEndpoints apiEndpoints,
      String reference,
      TracabilitePei traca,
      Map<String, Object> dataRemocra,
      String codeDiametre,
      String codeMarque,
      String codeModele) {
    ObjectNode payload = mapper.createObjectNode();
    payload.put("codeDiametre", codeDiametre);
    payload.put(
        "diametreCanalisation",
        traca.getDiametreCanalisation() != null
            ? Integer.valueOf(traca.getDiametreCanalisation())
            : null);
    payload.put("peiJumele", getString(dataRemocra, "jumelage", "pibiJumele"));
    payload.put(
        "inviolabilite", getBoolean(dataRemocra, "inviolabilite", "dispositifInviolabilite"));
    payload.put("renversable", getBoolean(dataRemocra, "renversable"));
    payload.put("codeMarque", codeMarque);
    payload.put("codeModele", codeModele);
    payload.put("anneeFabrication", getInteger(dataRemocra, "anneeFabrication"));
    payload.put("codeTypeReseau", getString(dataRemocra, "natureReseau", "typeReseauCode"));
    payload.put(
        "codeTypeCanalisation",
        getString(dataRemocra, "natureCanalisation", "typeCanalisationCode"));
    payload.put("reseauSurpresse", getBoolean(dataRemocra, "reseauSurpresse", "surpresse"));
    payload.put("reseauAdditive", getBoolean(dataRemocra, "reseauAdditive", "additive"));
    return new PeiCaracteristiquesUpdate(apiEndpoints.peiPibiCaracteristiques(reference), payload);
  }

  private static PeiCaracteristiquesUpdate createPenaUpdate(
      ObjectMapper mapper, ApiEndpoints apiEndpoints, String reference, Map<String, Object> data) {
    ObjectNode payload = mapper.createObjectNode();
    payload.put("capaciteIllimitee", getBoolean(data, "illimite", "capaciteIllimitee"));
    payload.put("capaciteIncertaine", getBoolean(data, "incertaine", "capaciteIncertaine"));
    payload.put("capacite", getInteger(data, "capacite"));
    payload.put("quantiteAppoint", getDouble(data, "debitAppoint", "quantiteAppoint"));
    payload.put("codeMateriau", getString(data, "codeMateriau", "materiauCode"));
    payload.put("equipeHBE", getBoolean(data, "equipeHBE"));
    return new PeiCaracteristiquesUpdate(apiEndpoints.peiPenaCaracteristiques(reference), payload);
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

  private static Boolean getBoolean(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Boolean value = JSONUtil.getBoolean(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Integer getInteger(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Integer value = JSONUtil.getInteger(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  private static Double getDouble(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Double value = JSONUtil.getDouble(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }
}
