/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;

import java.io.Serializable;
import java.time.LocalDateTime;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class VuePeiMotifIndispoEdpRemocra implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer gidEdp;
  private String reference;
  private String motifIndispo;
  private String statutIndispo;
  private LocalDateTime dateDebut;
  private LocalDateTime dateFinPrevu;
  private LocalDateTime dateMajE;

  public VuePeiMotifIndispoEdpRemocra() {}

  public VuePeiMotifIndispoEdpRemocra(VuePeiMotifIndispoEdpRemocra value) {
    this.gidEdp = value.gidEdp;
    this.reference = value.reference;
    this.motifIndispo = value.motifIndispo;
    this.statutIndispo = value.statutIndispo;
    this.dateDebut = value.dateDebut;
    this.dateFinPrevu = value.dateFinPrevu;
    this.dateMajE = value.dateMajE;
  }

  public VuePeiMotifIndispoEdpRemocra(
      Integer gidEdp,
      String reference,
      String motifIndispo,
      String statutIndispo,
      LocalDateTime dateDebut,
      LocalDateTime dateFinPrevu,
      LocalDateTime dateMajE) {
    this.gidEdp = gidEdp;
    this.reference = reference;
    this.motifIndispo = motifIndispo;
    this.statutIndispo = statutIndispo;
    this.dateDebut = dateDebut;
    this.dateFinPrevu = dateFinPrevu;
    this.dateMajE = dateMajE;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.gid_edp</code>. */
  public Integer getGidEdp() {
    return this.gidEdp;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.gid_edp</code>. */
  public VuePeiMotifIndispoEdpRemocra setGidEdp(Integer gidEdp) {
    this.gidEdp = gidEdp;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.reference</code>. */
  public String getReference() {
    return this.reference;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.reference</code>. */
  public VuePeiMotifIndispoEdpRemocra setReference(String reference) {
    this.reference = reference;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.motif_indispo</code>. */
  public String getMotifIndispo() {
    return this.motifIndispo;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.motif_indispo</code>. */
  public VuePeiMotifIndispoEdpRemocra setMotifIndispo(String motifIndispo) {
    this.motifIndispo = motifIndispo;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.statut_indispo</code>. */
  public String getStatutIndispo() {
    return this.statutIndispo;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.statut_indispo</code>. */
  public VuePeiMotifIndispoEdpRemocra setStatutIndispo(String statutIndispo) {
    this.statutIndispo = statutIndispo;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_debut</code>. */
  public LocalDateTime getDateDebut() {
    return this.dateDebut;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_debut</code>. */
  public VuePeiMotifIndispoEdpRemocra setDateDebut(LocalDateTime dateDebut) {
    this.dateDebut = dateDebut;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_fin_prevu</code>. */
  public LocalDateTime getDateFinPrevu() {
    return this.dateFinPrevu;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_fin_prevu</code>. */
  public VuePeiMotifIndispoEdpRemocra setDateFinPrevu(LocalDateTime dateFinPrevu) {
    this.dateFinPrevu = dateFinPrevu;
    return this;
  }

  /** Getter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_maj_e</code>. */
  public LocalDateTime getDateMajE() {
    return this.dateMajE;
  }

  /** Setter for <code>edp.vue_pei_motif_indispo_edp_remocra.date_maj_e</code>. */
  public VuePeiMotifIndispoEdpRemocra setDateMajE(LocalDateTime dateMajE) {
    this.dateMajE = dateMajE;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("VuePeiMotifIndispoEdpRemocra (");

    sb.append(gidEdp);
    sb.append(", ").append(reference);
    sb.append(", ").append(motifIndispo);
    sb.append(", ").append(statutIndispo);
    sb.append(", ").append(dateDebut);
    sb.append(", ").append(dateFinPrevu);
    sb.append(", ").append(dateMajE);

    sb.append(")");
    return sb.toString();
  }
}
