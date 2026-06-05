package fr.eaudeparis.syncremocra.repository.message;

/** Type métier de PEI utilisé pour router les mises à jour de caractéristiques. */
public enum PeiType {
  PIBI("pibi"),
  PENA("pena");

  private final String apiValue;

  PeiType(String apiValue) {
    this.apiValue = apiValue;
  }

  /** @return valeur attendue dans les chemins d'API REMOcRA */
  public String getApiValue() {
    return apiValue;
  }
}
