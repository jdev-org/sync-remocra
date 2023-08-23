package fr.eaudeparis.syncremocra.repository.typeErreur.model;

public class TypeErreurModel {

  Long id;

  String code;

  Integer iterations;

  String contexte;

  String messageAction;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getIterations() {
    return iterations;
  }

  public void setIterations(Integer iterations) {
    this.iterations = iterations;
  }

  public String getContexte() {
    return contexte;
  }

  public void setContexte(String contexte) {
    this.contexte = contexte;
  }

  public String getMessageAction() {
    return messageAction;
  }

  public void setMessageAction(String messageAction) {
    this.messageAction = messageAction;
  }
}
