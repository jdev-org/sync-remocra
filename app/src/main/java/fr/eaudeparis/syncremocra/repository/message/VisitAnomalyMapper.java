package fr.eaudeparis.syncremocra.repository.message;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Translates the known local visit actions to REMOcRA v3 anomaly payloads. */
final class VisitAnomalyMapper {

  private VisitAnomalyMapper() {}

  /**
   * Builds the anomalies to control and observe for a local visit action.
   *
   * <p>Only mappings validated in the transfer reference are implemented here. Unknown actions
   * deliberately produce empty lists until their REMOcRA code is confirmed.
   *
   * @param localVisitType local visit type and closure action
   * @param accessibleAnomalies anomalies enabled by REMOcRA for the PEI and visit type
   * @param peiState source PEI state, used by conditional mappings
   * @return anomaly lists for the REMOcRA visit payload
   */
  static VisitAnomalies map(
      String localVisitType, List<String> accessibleAnomalies, String peiState) {
    List<String> anomaliesControlees = new ArrayList<>();
    List<String> anomaliesConstatees = new ArrayList<>();

    if (localVisitType == null) {
      return new VisitAnomalies(anomaliesControlees, anomaliesConstatees);
    }

    String normalizedVisitType = normalize(localVisitType);
    if (shouldClearAllAnomalies(normalizedVisitType)) {
      anomaliesControlees.addAll(accessibleAnomalies);
      return new VisitAnomalies(anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("TRAVAUX") || normalizedVisitType.contains("RENOUVELER")) {
      addToBoth("BSPP_APDP", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("REPARER APPAREIL")) {
      addToBoth("BSPP_APDP", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("SANS EAU")) {
      addToBoth("BSPP_APSE", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("APPAREIL DETERIORE")) {
      boolean ctpVisit = isCtpVisit(normalizedVisitType);
      boolean availablePei = "DISPONIBLE".equals(normalize(peiState));
      addToBoth(
          ctpVisit && availablePei ? "BSPP_SINC" : "BSPP_APDP",
          anomaliesControlees,
          anomaliesConstatees);
    }

    if (normalizedVisitType.contains("HORS SERVICE")) {
      addToBoth("BSPP_APSE", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("DIFFICILE A MANOEUVRER")) {
      addToBoth("BSPP_DIFF", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("EN ECOULEMENT")) {
      addToBoth("BSPP_AREE", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("DEGORGER COFFRE")) {
      addToBoth("BSPP_PODC", anomaliesControlees, anomaliesConstatees);
      addToBoth("BSPP_PDCL", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("PEI INTROUVABLE")) {
      addToBoth("BSPP_INTR", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("ENCOMBRE") || normalizedVisitType.contains("INACCESSIBLE")) {
      addToBoth("VEHICULE_EN_STATIONNEMENT_SUR_BI", anomaliesControlees, anomaliesConstatees);
      addToBoth("BSPP_ACPV", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("PB DEBIT")) {
      addToBoth("DEBIT_NON_CONFORME", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("COUVERCLE MANQU")) {
      addToBoth("BSPP_CPMD", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("PB SIGNALETIQUE")) {
      addAccessibleToBoth(
          "BSPP_PISD", accessibleAnomalies, anomaliesControlees, anomaliesConstatees);
      addAccessibleToBoth(
          "BSPP_BISD", accessibleAnomalies, anomaliesControlees, anomaliesConstatees);
    }

    return new VisitAnomalies(anomaliesControlees, anomaliesConstatees);
  }

  static VisitAnomalies map(String localVisitType, List<String> accessibleAnomalies) {
    return map(localVisitType, accessibleAnomalies, null);
  }

  /**
   * Determines whether a local visit action clears every anomaly accessible in REMOcRA.
   *
   * @param localVisitType local visit type and closure action, normalized or not
   * @return {@code true} for {@code CONTROLE REALISE} and {@code EN SERVICE}
   */
  static boolean shouldClearAllAnomalies(String localVisitType) {
    if (localVisitType == null) {
      return false;
    }
    String normalizedVisitType = normalize(localVisitType);
    return normalizedVisitType.contains("EN SERVICE")
        || normalizedVisitType.contains("CONTROLE REALISE");
  }

  private static String normalize(String value) {
    if (value == null) {
      return "";
    }
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "")
        .replace("\u0152", "OE")
        .replace("\u0153", "oe")
        .toUpperCase();
  }

  private static boolean isCtpVisit(String normalizedVisitType) {
    return normalizedVisitType.startsWith("CTP")
        || normalizedVisitType.startsWith("PICF")
        || normalizedVisitType.startsWith("PIQP")
        || normalizedVisitType.startsWith("CTRL");
  }

  private static void addAccessibleToBoth(
      String anomalyCode,
      List<String> accessibleAnomalies,
      List<String> anomaliesControlees,
      List<String> anomaliesConstatees) {
    if (accessibleAnomalies.contains(anomalyCode)) {
      addToBoth(anomalyCode, anomaliesControlees, anomaliesConstatees);
    }
  }

  private static void addToBoth(
      String anomalyCode, List<String> anomaliesControlees, List<String> anomaliesConstatees) {
    if (!anomaliesControlees.contains(anomalyCode)) {
      anomaliesControlees.add(anomalyCode);
    }
    if (!anomaliesConstatees.contains(anomalyCode)) {
      anomaliesConstatees.add(anomalyCode);
    }
  }

  /** Immutable lists to include in a REMOcRA visit payload. */
  static final class VisitAnomalies {

    private final List<String> anomaliesControlees;
    private final List<String> anomaliesConstatees;

    private VisitAnomalies(List<String> anomaliesControlees, List<String> anomaliesConstatees) {
      this.anomaliesControlees = Collections.unmodifiableList(new ArrayList<>(anomaliesControlees));
      this.anomaliesConstatees = Collections.unmodifiableList(new ArrayList<>(anomaliesConstatees));
    }

    List<String> getAnomaliesControlees() {
      return anomaliesControlees;
    }

    List<String> getAnomaliesConstatees() {
      return anomaliesConstatees;
    }
  }
}
