package fr.eaudeparis.syncremocra.util;

public class RequestException extends Exception {

  private int code;

  private String codeErreur;

  private String message;

  /**
   * Exception levée en cas de retour d'une erreur de l'API
   *
   * @param code code HTTP du retour de la requête
   * @param codeErreur Code d'erreur interne
   * @param message Message d'erreur
   */
  public RequestException(int code, String codeErreur, String message) {
    this.code = code;
    this.codeErreur = codeErreur;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getCodeErreur() {
    return codeErreur;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
