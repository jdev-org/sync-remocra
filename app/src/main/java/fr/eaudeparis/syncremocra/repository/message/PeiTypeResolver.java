package fr.eaudeparis.syncremocra.repository.message;

import fr.eaudeparis.syncremocra.util.JSONUtil;
import java.util.List;
import java.util.Map;

/** Résout le type PIBI/PENA d'un PEI à partir de sa nature et des référentiels REMOcRA. */
public final class PeiTypeResolver {

  private PeiTypeResolver() {}

  /**
   * Détermine le type de PEI en croisant la nature du PEI avec les référentiels PIBI et PENA.
   *
   * @param dataPei détail du PEI retourné par REMOcRA
   * @param pibiNatures natures PEI de type PIBI
   * @param penaNatures natures PEI de type PENA
   * @return type résolu, ou {@code null} si la nature est absente ou non retrouvée
   */
  public static PeiType resolve(
      Map<String, Object> dataPei,
      List<Map<String, Object>> pibiNatures,
      List<Map<String, Object>> penaNatures) {
    String natureIdentifier = getNatureIdentifier(dataPei);
    if (natureIdentifier == null) {
      return null;
    }
    if (containsNature(pibiNatures, natureIdentifier)) {
      return PeiType.PIBI;
    }
    if (containsNature(penaNatures, natureIdentifier)) {
      return PeiType.PENA;
    }
    return null;
  }

  private static String getNatureIdentifier(Map<String, Object> dataPei) {
    String natureId = JSONUtil.getString(dataPei, "peiNatureId");
    if (natureId != null) {
      return natureId;
    }
    return JSONUtil.getString(dataPei, "nature");
  }

  private static boolean containsNature(
      List<Map<String, Object>> natures, String natureIdentifier) {
    for (Map<String, Object> nature : natures) {
      String natureId = JSONUtil.getString(nature, "natureId");
      String natureCode = JSONUtil.getString(nature, "natureCode");
      String legacyCode = JSONUtil.getString(nature, "code");
      if (natureIdentifier.equalsIgnoreCase(String.valueOf(natureId))
          || natureIdentifier.equalsIgnoreCase(String.valueOf(natureCode))
          || natureIdentifier.equalsIgnoreCase(String.valueOf(legacyCode))) {
        return true;
      }
    }
    return false;
  }
}
