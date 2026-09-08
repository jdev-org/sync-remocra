package fr.eaudeparis.syncremocra.repository.message;

/** Convertit les types de visite entre le modèle local SyncRemocra et REMOcRA v3. */
public final class VisitTypeMapper {

  private VisitTypeMapper() {}

  /**
   * Détermine le type de visite REMOcRA à partir du libellé de visite local.
   *
   * @param localVisitType libellé ou code historique WatGIS/SyncRemocra
   * @return type REMOcRA v3 (`CTP` ou `NP`), ou {@code null} si le type n'est pas géré
   */
  public static String toRemocraType(String localVisitType) {
    if (localVisitType == null) {
      return null;
    }
    String normalized = localVisitType.toUpperCase();
    if (normalized.startsWith("PICF")
        || normalized.startsWith("PIQP")
        || normalized.startsWith("CTRL")
        || normalized.startsWith("CTP")) {
      return "CTP";
    }
    if (normalized.startsWith("NP")) {
      return "NP";
    }
    return null;
  }

  /**
   * Convertit un type de visite REMOcRA v3 vers la valeur locale historique.
   *
   * @param remocraVisitType type REMOcRA v3
   * @return type local stocké en base
   */
  public static String toLocalType(String remocraVisitType) {
    if ("CTP".equalsIgnoreCase(remocraVisitType)) {
      return "CTRL";
    }
    return remocraVisitType;
  }
}
