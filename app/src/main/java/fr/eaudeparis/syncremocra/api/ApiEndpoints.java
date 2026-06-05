package fr.eaudeparis.syncremocra.api;

/**
 * Centralise les chemins des endpoints REMOcRA utilisés par l'application.
 *
 * <p>Cette classe ne gère que les chemins relatifs. La construction de l'URL complète ({@code host
 * + basePath + path}) reste du ressort de {@link fr.eaudeparis.syncremocra.util.RequestManager}.
 */
public class ApiEndpoints {

  private static final String DECI_ROOT = "/deci";
  private static final String PEI_ROOT = DECI_ROOT + "/pei";
  private static final String REFERENTIEL_ROOT = DECI_ROOT + "/referentiel";
  private static final String INDISPO_TEMPORAIRE_ROOT = DECI_ROOT + "/indispoTemporaire";
  private static final String VISITES_SEGMENT = "/visites";
  private static final String CARACTERISTIQUES_SEGMENT = "/caracteristiques";

  /** @return Endpoint de récupération des diffs PEI */
  public String peiDiff() {
    return PEI_ROOT + "/diff";
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @return Endpoint du PEI ciblé
   */
  public String pei(String reference) {
    return PEI_ROOT + "/" + reference;
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @return Endpoint des caractéristiques du PEI ciblé
   */
  public String peiCaracteristiques(String reference) {
    return pei(reference) + CARACTERISTIQUES_SEGMENT;
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @return Endpoint de mise à jour des caractéristiques PIBI
   */
  public String peiPibiCaracteristiques(String reference) {
    return pei(reference) + "/pibi-caracteristiques";
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @return Endpoint de mise à jour des caractéristiques PENA
   */
  public String peiPenaCaracteristiques(String reference) {
    return pei(reference) + "/pena-caracteristiques";
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @return Endpoint de la collection de visites du PEI ciblé
   */
  public String peiVisites(String reference) {
    return pei(reference) + VISITES_SEGMENT;
  }

  /**
   * @param reference Référence ou numéro du PEI
   * @param identifiantVisite Identifiant de la visite
   * @return Endpoint d'une visite spécifique d'un PEI
   */
  public String peiVisite(String reference, Object identifiantVisite) {
    return peiVisites(reference) + "/" + identifiantVisite;
  }

  /** @return Endpoint de la collection des indisponibilités temporaires */
  public String indispoTemporaire() {
    return INDISPO_TEMPORAIRE_ROOT;
  }

  /**
   * @param identifiant Identifiant de l'indisponibilité temporaire
   * @return Endpoint d'une indisponibilité temporaire spécifique
   */
  public String indispoTemporaire(Object identifiant) {
    return INDISPO_TEMPORAIRE_ROOT + "/" + identifiant;
  }

  /**
   * @param type Type de PEI en minuscule, par exemple {@code pibi} ou {@code pena}
   * @param nature Nature du PEI
   * @return Endpoint du référentiel des anomalies pour le type et la nature fournis
   */
  public String referentielNaturesAnomalies(String type, String nature) {
    return REFERENTIEL_ROOT + "/" + type + "/" + nature + "/naturesAnomalies";
  }

  /**
   * @param type Type de PEI en minuscule, par exemple {@code pibi} ou {@code pena}
   * @return Endpoint du référentiel des natures PEI pour le type fourni
   */
  public String referentielNaturesPei(String type) {
    return REFERENTIEL_ROOT + "/" + type + "/naturesPEI";
  }
}
