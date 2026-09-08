package fr.eaudeparis.syncremocra.repository.pei;

import fr.eaudeparis.syncremocra.repository.message.PeiType;
import java.util.List;
import java.util.Map;

/** Résout le type et la nature d'un PEI à partir du contrat REMOcRA v3. */
public final class PeiNatureReferenceResolver {

  private PeiNatureReferenceResolver() {}

  /**
   * Détermine le type de PEI et le code nature à utiliser pour interroger les référentiels
   * d'anomalies.
   *
   * @param pei détail du PEI
   * @param pibiNatures référentiel des natures PIBI
   * @param penaNatures référentiel des natures PENA
   * @return la référence de nature, ou {@code null} si elle ne peut pas être résolue
   */
  public static PeiNatureReference resolve(
      Map<String, Object> pei,
      List<Map<String, Object>> pibiNatures,
      List<Map<String, Object>> penaNatures) {
    String natureId = valueAsString(pei.get("peiNatureId"));
    if (natureId == null) {
      return null;
    }

    String pibiNatureCode = findNatureCode(pibiNatures, natureId);
    if (pibiNatureCode != null) {
      return new PeiNatureReference(PeiType.PIBI, pibiNatureCode);
    }

    String penaNatureCode = findNatureCode(penaNatures, natureId);
    if (penaNatureCode != null) {
      return new PeiNatureReference(PeiType.PENA, penaNatureCode);
    }

    return null;
  }

  private static String findNatureCode(List<Map<String, Object>> natures, String natureId) {
    for (Map<String, Object> nature : natures) {
      if (natureId.equals(valueAsString(nature.get("natureId")))) {
        return valueAsString(nature.get("natureCode"));
      }
    }
    return null;
  }

  private static String valueAsString(Object value) {
    return value != null ? value.toString() : null;
  }
}
