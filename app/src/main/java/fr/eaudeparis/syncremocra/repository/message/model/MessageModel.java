package fr.eaudeparis.syncremocra.repository.message.model;

import java.util.Date;

public class MessageModel {
  Integer id;

  Integer id_traca_pei;

  Date date;

  String type;

  String statut;

  Boolean synchroniser;

  Integer synchronisations;

  String erreur;

  String json;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId_traca_pei() {
    return id_traca_pei;
  }

  public void setId_traca_pei(Integer id_traca_pei) {
    this.id_traca_pei = id_traca_pei;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatut() {
    return statut;
  }

  public void setStatut(String statut) {
    this.statut = statut;
  }

  public Boolean getSynchroniser() {
    return synchroniser;
  }

  public void setSynchroniser(Boolean synchroniser) {
    this.synchroniser = synchroniser;
  }

  public Integer getSynchronisations() {
    return synchronisations;
  }

  public void setSynchronisations(Integer synchronisations) {
    this.synchronisations = synchronisations;
  }

  public String getErreur() {
    return erreur;
  }

  public void setErreur(String erreur) {
    this.erreur = erreur;
  }

  public String getJson() {
    return json;
  }

  public void setJson(String json) {
    this.json = json;
  }
}
