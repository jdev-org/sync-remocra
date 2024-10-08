/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TracabiliteIndispo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private Long          idTracaPei;
    private String        reference;
    private String        motifIndispo;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFinPrevue;
    private LocalDateTime dateTraca;

    public TracabiliteIndispo() {}

    public TracabiliteIndispo(TracabiliteIndispo value) {
        this.id = value.id;
        this.idTracaPei = value.idTracaPei;
        this.reference = value.reference;
        this.motifIndispo = value.motifIndispo;
        this.dateDebut = value.dateDebut;
        this.dateFinPrevue = value.dateFinPrevue;
        this.dateTraca = value.dateTraca;
    }

    public TracabiliteIndispo(
        Integer       id,
        Long          idTracaPei,
        String        reference,
        String        motifIndispo,
        LocalDateTime dateDebut,
        LocalDateTime dateFinPrevue,
        LocalDateTime dateTraca
    ) {
        this.id = id;
        this.idTracaPei = idTracaPei;
        this.reference = reference;
        this.motifIndispo = motifIndispo;
        this.dateDebut = dateDebut;
        this.dateFinPrevue = dateFinPrevue;
        this.dateTraca = dateTraca;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.id</code>.
     */
    public TracabiliteIndispo setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.id_traca_pei</code>.
     */
    public Long getIdTracaPei() {
        return this.idTracaPei;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.id_traca_pei</code>.
     */
    public TracabiliteIndispo setIdTracaPei(Long idTracaPei) {
        this.idTracaPei = idTracaPei;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.reference</code>.
     */
    public String getReference() {
        return this.reference;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.reference</code>.
     */
    public TracabiliteIndispo setReference(String reference) {
        this.reference = reference;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.motif_indispo</code>.
     */
    public String getMotifIndispo() {
        return this.motifIndispo;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.motif_indispo</code>.
     */
    public TracabiliteIndispo setMotifIndispo(String motifIndispo) {
        this.motifIndispo = motifIndispo;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.date_debut</code>.
     */
    public LocalDateTime getDateDebut() {
        return this.dateDebut;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.date_debut</code>.
     */
    public TracabiliteIndispo setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.date_fin_prevue</code>.
     */
    public LocalDateTime getDateFinPrevue() {
        return this.dateFinPrevue;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.date_fin_prevue</code>.
     */
    public TracabiliteIndispo setDateFinPrevue(LocalDateTime dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_indispo.date_traca</code>.
     */
    public LocalDateTime getDateTraca() {
        return this.dateTraca;
    }

    /**
     * Setter for <code>edp.tracabilite_indispo.date_traca</code>.
     */
    public TracabiliteIndispo setDateTraca(LocalDateTime dateTraca) {
        this.dateTraca = dateTraca;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TracabiliteIndispo (");

        sb.append(id);
        sb.append(", ").append(idTracaPei);
        sb.append(", ").append(reference);
        sb.append(", ").append(motifIndispo);
        sb.append(", ").append(dateDebut);
        sb.append(", ").append(dateFinPrevue);
        sb.append(", ").append(dateTraca);

        sb.append(")");
        return sb.toString();
    }
}
