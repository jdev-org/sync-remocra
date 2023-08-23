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
public class VueVisite implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       gidEdp;
    private String        reference;
    private LocalDateTime dateVisite;
    private String        typeVisite;
    private Integer       essaiePressionStatic;
    private Integer       essaiePressionDynamique;
    private Integer       essaieDebit;
    private LocalDateTime dateMajE;

    public VueVisite() {}

    public VueVisite(VueVisite value) {
        this.gidEdp = value.gidEdp;
        this.reference = value.reference;
        this.dateVisite = value.dateVisite;
        this.typeVisite = value.typeVisite;
        this.essaiePressionStatic = value.essaiePressionStatic;
        this.essaiePressionDynamique = value.essaiePressionDynamique;
        this.essaieDebit = value.essaieDebit;
        this.dateMajE = value.dateMajE;
    }

    public VueVisite(
        Integer       gidEdp,
        String        reference,
        LocalDateTime dateVisite,
        String        typeVisite,
        Integer       essaiePressionStatic,
        Integer       essaiePressionDynamique,
        Integer       essaieDebit,
        LocalDateTime dateMajE
    ) {
        this.gidEdp = gidEdp;
        this.reference = reference;
        this.dateVisite = dateVisite;
        this.typeVisite = typeVisite;
        this.essaiePressionStatic = essaiePressionStatic;
        this.essaiePressionDynamique = essaiePressionDynamique;
        this.essaieDebit = essaieDebit;
        this.dateMajE = dateMajE;
    }

    /**
     * Getter for <code>edp.vue_visite.gid_edp</code>.
     */
    public Integer getGidEdp() {
        return this.gidEdp;
    }

    /**
     * Setter for <code>edp.vue_visite.gid_edp</code>.
     */
    public VueVisite setGidEdp(Integer gidEdp) {
        this.gidEdp = gidEdp;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.reference</code>.
     */
    public String getReference() {
        return this.reference;
    }

    /**
     * Setter for <code>edp.vue_visite.reference</code>.
     */
    public VueVisite setReference(String reference) {
        this.reference = reference;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.date_visite</code>.
     */
    public LocalDateTime getDateVisite() {
        return this.dateVisite;
    }

    /**
     * Setter for <code>edp.vue_visite.date_visite</code>.
     */
    public VueVisite setDateVisite(LocalDateTime dateVisite) {
        this.dateVisite = dateVisite;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.type_visite</code>.
     */
    public String getTypeVisite() {
        return this.typeVisite;
    }

    /**
     * Setter for <code>edp.vue_visite.type_visite</code>.
     */
    public VueVisite setTypeVisite(String typeVisite) {
        this.typeVisite = typeVisite;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.essaie_pression_static</code>.
     */
    public Integer getEssaiePressionStatic() {
        return this.essaiePressionStatic;
    }

    /**
     * Setter for <code>edp.vue_visite.essaie_pression_static</code>.
     */
    public VueVisite setEssaiePressionStatic(Integer essaiePressionStatic) {
        this.essaiePressionStatic = essaiePressionStatic;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.essaie_pression_dynamique</code>.
     */
    public Integer getEssaiePressionDynamique() {
        return this.essaiePressionDynamique;
    }

    /**
     * Setter for <code>edp.vue_visite.essaie_pression_dynamique</code>.
     */
    public VueVisite setEssaiePressionDynamique(Integer essaiePressionDynamique) {
        this.essaiePressionDynamique = essaiePressionDynamique;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.essaie_debit</code>.
     */
    public Integer getEssaieDebit() {
        return this.essaieDebit;
    }

    /**
     * Setter for <code>edp.vue_visite.essaie_debit</code>.
     */
    public VueVisite setEssaieDebit(Integer essaieDebit) {
        this.essaieDebit = essaieDebit;
        return this;
    }

    /**
     * Getter for <code>edp.vue_visite.date_maj_e</code>.
     */
    public LocalDateTime getDateMajE() {
        return this.dateMajE;
    }

    /**
     * Setter for <code>edp.vue_visite.date_maj_e</code>.
     */
    public VueVisite setDateMajE(LocalDateTime dateMajE) {
        this.dateMajE = dateMajE;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VueVisite (");

        sb.append(gidEdp);
        sb.append(", ").append(reference);
        sb.append(", ").append(dateVisite);
        sb.append(", ").append(typeVisite);
        sb.append(", ").append(essaiePressionStatic);
        sb.append(", ").append(essaiePressionDynamique);
        sb.append(", ").append(essaieDebit);
        sb.append(", ").append(dateMajE);

        sb.append(")");
        return sb.toString();
    }
}
