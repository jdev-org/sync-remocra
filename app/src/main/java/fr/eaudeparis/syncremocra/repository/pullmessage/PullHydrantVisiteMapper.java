package fr.eaudeparis.syncremocra.repository.pullmessage;

import fr.eaudeparis.syncremocra.repository.message.VisitTypeMapper;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

/** Mappe les payloads de visites REMOcRA v3 vers le format local historique. */
public final class PullHydrantVisiteMapper {

  private PullHydrantVisiteMapper() {}

  /**
   * @param visite payload de liste de visites
   * @return identifiant de visite, ou {@code null} si absent
   */
  public static Object getVisiteId(Map<String, Object> visite) {
    return visite.get("visiteId");
  }

  /**
   * Convertit le type de visite REMOcRA vers le type local historique.
   *
   * @param dataVisiteSpecifique payload détaillé de la visite
   * @return type local
   */
  public static String getLocalVisitType(Map<String, Object> dataVisiteSpecifique) {
    return VisitTypeMapper.toLocalType(JSONUtil.getString(dataVisiteSpecifique, "typeVisite"));
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
   * @param dataVisiteSpecifique payload détaillé de la visite
   * @return date locale de la visite, ou {@code null} si absente
   */
  public static LocalDateTime getVisitDate(Map<String, Object> dataVisiteSpecifique) {
    String moment = JSONUtil.getString(dataVisiteSpecifique, "moment");
    if (moment == null) {
      return null;
    }
    return OffsetDateTime.parse(moment).toLocalDateTime();
  }
}
