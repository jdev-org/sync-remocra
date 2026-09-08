package fr.eaudeparis.syncremocra.repository.message;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Adapte le payload visite stocké localement pour conserver le suivi historique de SyncRemocra.
 *
 * <p>Le contrat REMOcRA v3 attend {@code typeVisite}, tandis que les vues SQL et notifications
 * existantes lisent encore le champ legacy {@code contexte}. Le payload envoyé à l'API reste en v3,
 * mais la version stockée localement conserve ce champ historique.
 */
public final class VisitPayloadTracker {

  private VisitPayloadTracker() {}

  /**
   * Produit une copie du payload de visite enrichie avec le champ local legacy {@code contexte}.
   *
   * @param apiPayload payload destiné à l'API REMOcRA
   * @return copie enrichie pour le suivi local
   */
  public static ObjectNode toTrackedPayload(ObjectNode apiPayload) {
    ObjectNode trackedPayload = apiPayload.deepCopy();
    if (trackedPayload.hasNonNull("typeVisite") && !trackedPayload.has("contexte")) {
      trackedPayload.put(
          "contexte", VisitTypeMapper.toLocalType(trackedPayload.get("typeVisite").asText()));
    }
    return trackedPayload;
  }
}
