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
   * @return anomaly lists for the REMOcRA visit payload
   */
  static VisitAnomalies map(String localVisitType, List<String> accessibleAnomalies) {
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

    if (normalizedVisitType.contains("SANS EAU")) {
      addToBoth("BSPP_APSE", anomaliesControlees, anomaliesConstatees);
    }

    if (normalizedVisitType.contains("PB SIGNALETIQUE")) {
      addAccessibleToBoth(
          "BSPP_PISD", accessibleAnomalies, anomaliesControlees, anomaliesConstatees);
      addAccessibleToBoth(
          "BSPP_BISD", accessibleAnomalies, anomaliesControlees, anomaliesConstatees);
    }

    return new VisitAnomalies(anomaliesControlees, anomaliesConstatees);
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
    return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toUpperCase();
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
