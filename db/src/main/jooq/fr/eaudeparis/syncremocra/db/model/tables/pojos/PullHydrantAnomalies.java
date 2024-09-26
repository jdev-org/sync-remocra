/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PullHydrantAnomalies implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long    visite;
    private String  code;

    public PullHydrantAnomalies() {}

    public PullHydrantAnomalies(PullHydrantAnomalies value) {
        this.id = value.id;
        this.visite = value.visite;
        this.code = value.code;
    }

    public PullHydrantAnomalies(
        Integer id,
        Long    visite,
        String  code
    ) {
        this.id = id;
        this.visite = visite;
        this.code = code;
    }

    /**
     * Getter for <code>edp.pull_hydrant_anomalies.id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>edp.pull_hydrant_anomalies.id</code>.
     */
    public PullHydrantAnomalies setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>edp.pull_hydrant_anomalies.visite</code>.
     */
    public Long getVisite() {
        return this.visite;
    }

    /**
     * Setter for <code>edp.pull_hydrant_anomalies.visite</code>.
     */
    public PullHydrantAnomalies setVisite(Long visite) {
        this.visite = visite;
        return this;
    }

    /**
     * Getter for <code>edp.pull_hydrant_anomalies.code</code>.
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Setter for <code>edp.pull_hydrant_anomalies.code</code>.
     */
    public PullHydrantAnomalies setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PullHydrantAnomalies (");

        sb.append(id);
        sb.append(", ").append(visite);
        sb.append(", ").append(code);

        sb.append(")");
        return sb.toString();
    }
}
