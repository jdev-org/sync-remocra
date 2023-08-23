package fr.eaudeparis.syncremocra.util;

public class InternalException extends Exception {

  private String codeErreur;

  private String message;

  public InternalException(String codeErreur, String message) {
    this.codeErreur = codeErreur;
    this.message = message;
  }

  public String getCodeErreur() {
    return codeErreur;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
