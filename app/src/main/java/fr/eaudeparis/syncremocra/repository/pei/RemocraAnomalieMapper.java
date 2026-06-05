package fr.eaudeparis.syncremocra.repository.pei;

import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.util.List;
import java.util.Map;

/** Mappe les anomalies des référentiels REMOcRA v3 vers le format historique SyncRemocra. */
public final class RemocraAnomalieMapper {

  private static final int BLOQUANTE_WEIGHT = 5;

  private RemocraAnomalieMapper() {}

  /**
   * @param anomalie anomalie issue du référentiel REMOcRA
   * @return code de l'anomalie
   */
  public static String getCode(Map<String, Object> anomalie) {
    return JSONUtil.getString(anomalie, "anomalieCode");
  }

  /**
   * @param anomalie anomalie issue du référentiel REMOcRA
   * @return {@code true} si l'anomalie est bloquante
   */
  public static boolean isBloquante(Map<String, Object> anomalie) {
    Integer v3Weight = JSONUtil.getInteger(anomalie, "poidsAnomalieValIndispoTerrestre");
    return Integer.valueOf(BLOQUANTE_WEIGHT).equals(v3Weight);
  }

  /**
   * Détermine si l'anomalie est accessible pour un type de visite donné.
   *
   * @param anomalie anomalie issue du référentiel REMOcRA
   * @param typeVisite type de visite REMOcRA
   * @return {@code true} si l'anomalie est compatible avec ce type de visite
   */
  public static boolean supportsTypeVisite(Map<String, Object> anomalie, String typeVisite) {
    Object listTypeVisite = anomalie.get("listTypeVisite");
    if (!(listTypeVisite instanceof List<?>)) {
      return true;
    }
    for (Object supportedType : (List<?>) listTypeVisite) {
      if (typeVisite.equalsIgnoreCase(String.valueOf(supportedType))) {
        return true;
      }
    }
    return false;
  }
}
