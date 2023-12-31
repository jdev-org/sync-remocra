/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MotifIndispoActif implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reference;
    private String motif;

    public MotifIndispoActif() {}

    public MotifIndispoActif(MotifIndispoActif value) {
        this.reference = value.reference;
        this.motif = value.motif;
    }

    public MotifIndispoActif(
        String reference,
        String motif
    ) {
        this.reference = reference;
        this.motif = motif;
    }

    /**
     * Getter for <code>edp.motif_indispo_actif.reference</code>.
     */
    public String getReference() {
        return this.reference;
    }

    /**
     * Setter for <code>edp.motif_indispo_actif.reference</code>.
     */
    public MotifIndispoActif setReference(String reference) {
        this.reference = reference;
        return this;
    }

    /**
     * Getter for <code>edp.motif_indispo_actif.motif</code>.
     */
    public String getMotif() {
        return this.motif;
    }

    /**
     * Setter for <code>edp.motif_indispo_actif.motif</code>.
     */
    public MotifIndispoActif setMotif(String motif) {
        this.motif = motif;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MotifIndispoActif (");

        sb.append(reference);
        sb.append(", ").append(motif);

        sb.append(")");
        return sb.toString();
    }
}
