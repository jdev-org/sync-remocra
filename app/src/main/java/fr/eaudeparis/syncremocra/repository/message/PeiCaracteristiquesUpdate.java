package fr.eaudeparis.syncremocra.repository.message;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** Contient le chemin cible et le payload d'écriture des caractéristiques d'un PEI. */
public final class PeiCaracteristiquesUpdate {

  private final String path;
  private final ObjectNode payload;

  /**
   * @param path endpoint cible REMOcRA
   * @param payload charge utile à transmettre
   */
  public PeiCaracteristiquesUpdate(String path, ObjectNode payload) {
    this.path = path;
    this.payload = payload;
  }

  /** @return endpoint cible REMOcRA */
  public String getPath() {
    return path;
  }

  /** @return charge utile à transmettre */
  public ObjectNode getPayload() {
    return payload;
  }
}
