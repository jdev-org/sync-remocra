package fr.eaudeparis.syncremocra.repository.pei;

import fr.eaudeparis.syncremocra.repository.message.PeiType;

/** Porte la nature et le type d'un PEI sous le format attendu par les référentiels REMOcRA. */
public final class PeiNatureReference {

  private final PeiType peiType;
  private final String natureCode;

  public PeiNatureReference(PeiType peiType, String natureCode) {
    this.peiType = peiType;
    this.natureCode = natureCode;
  }

  public PeiType getPeiType() {
    return peiType;
  }

  public String getNatureCode() {
    return natureCode;
  }
}
