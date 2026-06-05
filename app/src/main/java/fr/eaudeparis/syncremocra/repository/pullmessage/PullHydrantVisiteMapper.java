package fr.eaudeparis.syncremocra.repository.pullmessage;

import fr.eaudeparis.syncremocra.repository.message.VisitTypeMapper;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

/** Mappe les payloads de visites v2/v3 REMOcRA vers le format local historique. */
public final class PullHydrantVisiteMapper {

  private PullHydrantVisiteMapper() {}

  /**
   * Résout l'identifiant de visite en acceptant les contrats v2 et v3.
   *
   * @param visite payload de liste de visites
   * @return identifiant de visite, ou {@code null} si absent
   */
  public static Object getVisiteId(Map<String, Object> visite) {
    return (visite.get("visiteId") != null) ? visite.get("visiteId") : visite.get("identifiant");
  }

  /**
   * Convertit le type de visite REMOcRA vers le type local historique.
   *
   * @param dataVisiteSpecifique payload détaillé de la visite
   * @return type local
   */
  public static String getLocalVisitType(Map<String, Object> dataVisiteSpecifique) {
    String typeVisite = JSONUtil.getString(dataVisiteSpecifique, "typeVisite");
    return VisitTypeMapper.toLocalType(
        (typeVisite != null) ? typeVisite : JSONUtil.getString(dataVisiteSpecifique, "contexte"));
  }

  /**
   * Résout un double parmi plusieurs noms de champs compatibles.
   *
   * @param data payload source
   * @param keys noms de champs à tester dans l'ordre
   * @return première valeur trouvée
   */
  public static Double getDouble(Map<String, Object> data, String... keys) {
    for (String key : keys) {
      Double value = JSONUtil.getDouble(data, key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  /**
   * Résout la date de visite en acceptant les formats v2 et v3.
   *
   * @param dataVisiteSpecifique payload détaillé de la visite
   * @return date locale de la visite, ou {@code null} si absente
   */
  public static LocalDateTime getVisitDate(Map<String, Object> dataVisiteSpecifique) {
    LocalDateTime legacyDate =
        JSONUtil.getLocalDateTime(dataVisiteSpecifique, "date", "yyyy-MM-dd HH:mm");
    if (legacyDate != null) {
      return legacyDate;
    }
    String moment = JSONUtil.getString(dataVisiteSpecifique, "moment");
    if (moment == null) {
      return null;
    }
    return OffsetDateTime.parse(moment).toLocalDateTime();
  }
}
