package fr.eaudeparis.syncremocra.repository.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Mappe les indisponibilités temporaires REMOcRA v3 vers SyncRemocra. */
public final class IndispoTemporaireMapper {

  private static final String ORGANISME_API = "EAU_DE_PARIS";
  private static final String DEFAULT_MOTIF = "Mise en indisponibilité Eau de Paris";

  private IndispoTemporaireMapper() {}

  /**
   * Construit les paramètres de recherche des indisponibilités temporaires.
   *
   * @return paramètres de requête pour l'API REMOcRA
   */
  public static Map<String, String> buildSearchParams() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("organismeApi", ORGANISME_API);
    return params;
  }

  /**
   * Recherche l'indisponibilité temporaire active liée à un PEI.
   *
   * @param indispos liste des indisponibilités retournées par REMOcRA
   * @param reference référence du PEI
   * @return indisponibilité active correspondante, ou {@code null} si absente
   */
  public static Map<String, Object> findActiveIndispo(
      List<Map<String, Object>> indispos, String reference) {
    for (Map<String, Object> indispo : indispos) {
      if (containsReference(indispo, reference) && isActive(indispo)) {
        return indispo;
      }
    }
    return null;
  }

  /**
   * Construit le payload v3 de création d'une indisponibilité temporaire.
   *
   * @param mapper mapper Jackson
   * @param reference référence du PEI
   * @param dateDebut date de début locale
   * @return payload prêt à être sérialisé
   */
  public static ObjectNode buildCreatePayload(
      ObjectMapper mapper, String reference, LocalDateTime dateDebut) {
    ObjectNode data = mapper.createObjectNode();
    data.put("motif", DEFAULT_MOTIF);
    data.put("dateDebut", formatApiDate(dateDebut));
    data.put("mailAvantIndisponibilite", true);
    data.put("mailApresIndisponibilite", true);
    data.put("basculeAutoDisponible", true);
    data.put("basculeAutoIndisponible", true);
    data.set("listeNumeroPei", createPeiList(mapper, reference));
    return data;
  }

  /**
   * Construit le payload v3 de clôture d'une indisponibilité temporaire existante.
   *
   * @param mapper mapper Jackson
   * @param reference référence du PEI
   * @param indispoEnCours indisponibilité en cours
   * @param dateFin date de fin locale
   * @return payload prêt à être sérialisé
   */
  public static ObjectNode buildUpdatePayload(
      ObjectMapper mapper,
      String reference,
      Map<String, Object> indispoEnCours,
      LocalDateTime dateFin) {
    ObjectNode data = buildCreatePayload(mapper, reference, dateFin);
    data.put("dateDebut", getDateDebut(indispoEnCours));
    data.put("dateFin", formatApiDate(dateFin));
    return data;
  }

  /**
   * @param indispo indisponibilité retournée par REMOcRA
   * @return identifiant d'indisponibilité, ou {@code null} si absent
   */
  public static Object getIndispoId(Map<String, Object> indispo) {
    return indispo.get("indisponibiliteTemporaireId");
  }

  /**
   * @param indispo indisponibilité retournée par REMOcRA
   * @return date de début au format attendu par l'API
   */
  public static String getDateDebut(Map<String, Object> indispo) {
    return JSONUtil.getString(indispo, "indisponibiliteTemporaireDateDebut");
  }

  private static boolean containsReference(Map<String, Object> indispo, String reference) {
    Object listeNumeroPei = indispo.get("listeNumeroPei");
    if (listeNumeroPei instanceof List<?>) {
      for (Object pei : (List<?>) listeNumeroPei) {
        if (reference.equals(String.valueOf(pei))) {
          return true;
        }
      }
      return false;
    }
    if (listeNumeroPei != null) {
      String[] references = listeNumeroPei.toString().split("\\s*,\\s*");
      for (String currentReference : references) {
        if (reference.equals(currentReference)) {
          return true;
        }
      }
    }

    return false;
  }

  private static boolean isActive(Map<String, Object> indispo) {
    return JSONUtil.getString(indispo, "indisponibiliteTemporaireDateFin") == null;
  }

  private static ArrayNode createPeiList(ObjectMapper mapper, String reference) {
    ArrayNode peiList = mapper.createArrayNode();
    peiList.add(reference);
    return peiList;
  }

  private static String formatApiDate(LocalDateTime date) {
    return date.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }
}
