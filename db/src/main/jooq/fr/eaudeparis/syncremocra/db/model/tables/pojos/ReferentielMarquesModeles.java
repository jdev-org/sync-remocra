/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ReferentielMarquesModeles implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeModeleEdp;
    private String codeMarqueRemocra;
    private String codeModeleRemocra;

    public ReferentielMarquesModeles() {}

    public ReferentielMarquesModeles(ReferentielMarquesModeles value) {
        this.codeModeleEdp = value.codeModeleEdp;
        this.codeMarqueRemocra = value.codeMarqueRemocra;
        this.codeModeleRemocra = value.codeModeleRemocra;
    }

    public ReferentielMarquesModeles(
        String codeModeleEdp,
        String codeMarqueRemocra,
        String codeModeleRemocra
    ) {
        this.codeModeleEdp = codeModeleEdp;
        this.codeMarqueRemocra = codeMarqueRemocra;
        this.codeModeleRemocra = codeModeleRemocra;
    }

    /**
     * Getter for <code>edp.referentiel_marques_modeles.code_modele_edp</code>.
     */
    public String getCodeModeleEdp() {
        return this.codeModeleEdp;
    }

    /**
     * Setter for <code>edp.referentiel_marques_modeles.code_modele_edp</code>.
     */
    public ReferentielMarquesModeles setCodeModeleEdp(String codeModeleEdp) {
        this.codeModeleEdp = codeModeleEdp;
        return this;
    }

    /**
     * Getter for <code>edp.referentiel_marques_modeles.code_marque_remocra</code>.
     */
    public String getCodeMarqueRemocra() {
        return this.codeMarqueRemocra;
    }

    /**
     * Setter for <code>edp.referentiel_marques_modeles.code_marque_remocra</code>.
     */
    public ReferentielMarquesModeles setCodeMarqueRemocra(String codeMarqueRemocra) {
        this.codeMarqueRemocra = codeMarqueRemocra;
        return this;
    }

    /**
     * Getter for <code>edp.referentiel_marques_modeles.code_modele_remocra</code>.
     */
    public String getCodeModeleRemocra() {
        return this.codeModeleRemocra;
    }

    /**
     * Setter for <code>edp.referentiel_marques_modeles.code_modele_remocra</code>.
     */
    public ReferentielMarquesModeles setCodeModeleRemocra(String codeModeleRemocra) {
        this.codeModeleRemocra = codeModeleRemocra;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ReferentielMarquesModeles (");

        sb.append(codeModeleEdp);
        sb.append(", ").append(codeMarqueRemocra);
        sb.append(", ").append(codeModeleRemocra);

        sb.append(")");
        return sb.toString();
    }
}
