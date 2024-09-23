/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Erreur rencontrée lors du processus global de synchronisation entre REMOCRA et EDP et pouvant
 * donner lieu à notification
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Erreur implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer id;
  private LocalDateTime date;
  private String description;
  private Boolean notifie;
  private Long typeErreur;
  private Long message;

  public Erreur() {}

  public Erreur(Erreur value) {
    this.id = value.id;
    this.date = value.date;
    this.description = value.description;
    this.notifie = value.notifie;
    this.typeErreur = value.typeErreur;
    this.message = value.message;
  }

  public Erreur(
      Integer id,
      LocalDateTime date,
      String description,
      Boolean notifie,
      Long typeErreur,
      Long message) {
    this.id = id;
    this.date = date;
    this.description = description;
    this.notifie = notifie;
    this.typeErreur = typeErreur;
    this.message = message;
  }

  /** Getter for <code>edp.erreur.id</code>. Identifiant interne */
  public Integer getId() {
    return this.id;
  }

  /** Setter for <code>edp.erreur.id</code>. Identifiant interne */
  public Erreur setId(Integer id) {
    this.id = id;
    return this;
  }

  /** Getter for <code>edp.erreur.date</code>. Date et heure de l'erreur */
  public LocalDateTime getDate() {
    return this.date;
  }

  /** Setter for <code>edp.erreur.date</code>. Date et heure de l'erreur */
  public Erreur setDate(LocalDateTime date) {
    this.date = date;
    return this;
  }

  /**
   * Getter for <code>edp.erreur.description</code>. Description ou message d'erreur retourné par le
   * processus ou l'API
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Setter for <code>edp.erreur.description</code>. Description ou message d'erreur retourné par le
   * processus ou l'API
   */
  public Erreur setDescription(String description) {
    this.description = description;
    return this;
  }

  /** Getter for <code>edp.erreur.notifie</code>. */
  public Boolean getNotifie() {
    return this.notifie;
  }

  /** Setter for <code>edp.erreur.notifie</code>. */
  public Erreur setNotifie(Boolean notifie) {
    this.notifie = notifie;
    return this;
  }

  /** Getter for <code>edp.erreur.type_erreur</code>. Référence au type d'erreur */
  public Long getTypeErreur() {
    return this.typeErreur;
  }

  /** Setter for <code>edp.erreur.type_erreur</code>. Référence au type d'erreur */
  public Erreur setTypeErreur(Long typeErreur) {
    this.typeErreur = typeErreur;
    return this;
  }

  /**
   * Getter for <code>edp.erreur.message</code>. Référence au message impliqué dans le processus de
   * PUSH vers Remocra. Permet notament de retrouver le PEI impacté et les informations à
   * synchroniser
   */
  public Long getMessage() {
    return this.message;
  }

  /**
   * Setter for <code>edp.erreur.message</code>. Référence au message impliqué dans le processus de
   * PUSH vers Remocra. Permet notament de retrouver le PEI impacté et les informations à
   * synchroniser
   */
  public Erreur setMessage(Long message) {
    this.message = message;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Erreur (");

    sb.append(id);
    sb.append(", ").append(date);
    sb.append(", ").append(description);
    sb.append(", ").append(notifie);
    sb.append(", ").append(typeErreur);
    sb.append(", ").append(message);

    sb.append(")");
    return sb.toString();
  }
}
