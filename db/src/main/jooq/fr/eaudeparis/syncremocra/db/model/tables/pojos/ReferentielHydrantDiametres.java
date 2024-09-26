/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ReferentielHydrantDiametres implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeBspp;
    private String codeEdp;

    public ReferentielHydrantDiametres() {}

    public ReferentielHydrantDiametres(ReferentielHydrantDiametres value) {
        this.codeBspp = value.codeBspp;
        this.codeEdp = value.codeEdp;
    }

    public ReferentielHydrantDiametres(
        String codeBspp,
        String codeEdp
    ) {
        this.codeBspp = codeBspp;
        this.codeEdp = codeEdp;
    }

    /**
     * Getter for <code>edp.referentiel_hydrant_diametres.code_bspp</code>.
     */
    public String getCodeBspp() {
        return this.codeBspp;
    }

    /**
     * Setter for <code>edp.referentiel_hydrant_diametres.code_bspp</code>.
     */
    public ReferentielHydrantDiametres setCodeBspp(String codeBspp) {
        this.codeBspp = codeBspp;
        return this;
    }

    /**
     * Getter for <code>edp.referentiel_hydrant_diametres.code_edp</code>.
     */
    public String getCodeEdp() {
        return this.codeEdp;
    }

    /**
     * Setter for <code>edp.referentiel_hydrant_diametres.code_edp</code>.
     */
    public ReferentielHydrantDiametres setCodeEdp(String codeEdp) {
        this.codeEdp = codeEdp;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ReferentielHydrantDiametres (");

        sb.append(codeBspp);
        sb.append(", ").append(codeEdp);

        sb.append(")");
        return sb.toString();
    }
}
