/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VuePeiEdpRemocra implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       gidEdp;
    private String        reference;
    private Point         geometry1;
    private String        typeObjet;
    private Integer       prive;
    private String        precisionPeiPrive;
    private String        adresse;
    private String        notesLocalisation;
    private Integer       diametre;
    private String        modele;
    private String        diametreCanalisation;
    private String        materiauCanalisation;
    private LocalDateTime dateEssai;
    private BigDecimal    essaiPressionStatique;
    private BigDecimal    essaiPressionDynamique;
    private BigDecimal    essaiDebit;
    private String        etat;
    private LocalDateTime dateDerniereVisite;
    private String        typeDerniereVisite;
    private LocalDateTime dateMajP;
    private LocalDateTime dateMajE;
    private LocalDateTime dateMajQp;
    private LocalDateTime dateMajCf;
    private LocalDateTime dateMajNp;
    private LocalDateTime dateMajMj;

    public VuePeiEdpRemocra() {}

    public VuePeiEdpRemocra(VuePeiEdpRemocra value) {
        this.gidEdp = value.gidEdp;
        this.reference = value.reference;
        this.geometry1 = value.geometry1;
        this.typeObjet = value.typeObjet;
        this.prive = value.prive;
        this.precisionPeiPrive = value.precisionPeiPrive;
        this.adresse = value.adresse;
        this.notesLocalisation = value.notesLocalisation;
        this.diametre = value.diametre;
        this.modele = value.modele;
        this.diametreCanalisation = value.diametreCanalisation;
        this.materiauCanalisation = value.materiauCanalisation;
        this.dateEssai = value.dateEssai;
        this.essaiPressionStatique = value.essaiPressionStatique;
        this.essaiPressionDynamique = value.essaiPressionDynamique;
        this.essaiDebit = value.essaiDebit;
        this.etat = value.etat;
        this.dateDerniereVisite = value.dateDerniereVisite;
        this.typeDerniereVisite = value.typeDerniereVisite;
        this.dateMajP = value.dateMajP;
        this.dateMajE = value.dateMajE;
        this.dateMajQp = value.dateMajQp;
        this.dateMajCf = value.dateMajCf;
        this.dateMajNp = value.dateMajNp;
        this.dateMajMj = value.dateMajMj;
    }

    public VuePeiEdpRemocra(
        Integer       gidEdp,
        String        reference,
        Point         geometry1,
        String        typeObjet,
        Integer       prive,
        String        precisionPeiPrive,
        String        adresse,
        String        notesLocalisation,
        Integer       diametre,
        String        modele,
        String        diametreCanalisation,
        String        materiauCanalisation,
        LocalDateTime dateEssai,
        BigDecimal    essaiPressionStatique,
        BigDecimal    essaiPressionDynamique,
        BigDecimal    essaiDebit,
        String        etat,
        LocalDateTime dateDerniereVisite,
        String        typeDerniereVisite,
        LocalDateTime dateMajP,
        LocalDateTime dateMajE,
        LocalDateTime dateMajQp,
        LocalDateTime dateMajCf,
        LocalDateTime dateMajNp,
        LocalDateTime dateMajMj
    ) {
        this.gidEdp = gidEdp;
        this.reference = reference;
        this.geometry1 = geometry1;
        this.typeObjet = typeObjet;
        this.prive = prive;
        this.precisionPeiPrive = precisionPeiPrive;
        this.adresse = adresse;
        this.notesLocalisation = notesLocalisation;
        this.diametre = diametre;
        this.modele = modele;
        this.diametreCanalisation = diametreCanalisation;
        this.materiauCanalisation = materiauCanalisation;
        this.dateEssai = dateEssai;
        this.essaiPressionStatique = essaiPressionStatique;
        this.essaiPressionDynamique = essaiPressionDynamique;
        this.essaiDebit = essaiDebit;
        this.etat = etat;
        this.dateDerniereVisite = dateDerniereVisite;
        this.typeDerniereVisite = typeDerniereVisite;
        this.dateMajP = dateMajP;
        this.dateMajE = dateMajE;
        this.dateMajQp = dateMajQp;
        this.dateMajCf = dateMajCf;
        this.dateMajNp = dateMajNp;
        this.dateMajMj = dateMajMj;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.gid_edp</code>.
     */
    public Integer getGidEdp() {
        return this.gidEdp;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.gid_edp</code>.
     */
    public VuePeiEdpRemocra setGidEdp(Integer gidEdp) {
        this.gidEdp = gidEdp;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.reference</code>.
     */
    public String getReference() {
        return this.reference;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.reference</code>.
     */
    public VuePeiEdpRemocra setReference(String reference) {
        this.reference = reference;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.geometry1</code>.
     */
    public Point getGeometry1() {
        return this.geometry1;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.geometry1</code>.
     */
    public VuePeiEdpRemocra setGeometry1(Point geometry1) {
        this.geometry1 = geometry1;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.type_objet</code>.
     */
    public String getTypeObjet() {
        return this.typeObjet;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.type_objet</code>.
     */
    public VuePeiEdpRemocra setTypeObjet(String typeObjet) {
        this.typeObjet = typeObjet;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.prive</code>.
     */
    public Integer getPrive() {
        return this.prive;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.prive</code>.
     */
    public VuePeiEdpRemocra setPrive(Integer prive) {
        this.prive = prive;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.precision_pei_prive</code>.
     */
    public String getPrecisionPeiPrive() {
        return this.precisionPeiPrive;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.precision_pei_prive</code>.
     */
    public VuePeiEdpRemocra setPrecisionPeiPrive(String precisionPeiPrive) {
        this.precisionPeiPrive = precisionPeiPrive;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.adresse</code>.
     */
    public String getAdresse() {
        return this.adresse;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.adresse</code>.
     */
    public VuePeiEdpRemocra setAdresse(String adresse) {
        this.adresse = adresse;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.notes_localisation</code>.
     */
    public String getNotesLocalisation() {
        return this.notesLocalisation;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.notes_localisation</code>.
     */
    public VuePeiEdpRemocra setNotesLocalisation(String notesLocalisation) {
        this.notesLocalisation = notesLocalisation;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.diametre</code>.
     */
    public Integer getDiametre() {
        return this.diametre;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.diametre</code>.
     */
    public VuePeiEdpRemocra setDiametre(Integer diametre) {
        this.diametre = diametre;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.modele</code>.
     */
    public String getModele() {
        return this.modele;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.modele</code>.
     */
    public VuePeiEdpRemocra setModele(String modele) {
        this.modele = modele;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.diametre_canalisation</code>.
     */
    public String getDiametreCanalisation() {
        return this.diametreCanalisation;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.diametre_canalisation</code>.
     */
    public VuePeiEdpRemocra setDiametreCanalisation(String diametreCanalisation) {
        this.diametreCanalisation = diametreCanalisation;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.materiau_canalisation</code>.
     */
    public String getMateriauCanalisation() {
        return this.materiauCanalisation;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.materiau_canalisation</code>.
     */
    public VuePeiEdpRemocra setMateriauCanalisation(String materiauCanalisation) {
        this.materiauCanalisation = materiauCanalisation;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_essai</code>.
     */
    public LocalDateTime getDateEssai() {
        return this.dateEssai;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_essai</code>.
     */
    public VuePeiEdpRemocra setDateEssai(LocalDateTime dateEssai) {
        this.dateEssai = dateEssai;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.essai_pression_statique</code>.
     */
    public BigDecimal getEssaiPressionStatique() {
        return this.essaiPressionStatique;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.essai_pression_statique</code>.
     */
    public VuePeiEdpRemocra setEssaiPressionStatique(BigDecimal essaiPressionStatique) {
        this.essaiPressionStatique = essaiPressionStatique;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.essai_pression_dynamique</code>.
     */
    public BigDecimal getEssaiPressionDynamique() {
        return this.essaiPressionDynamique;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.essai_pression_dynamique</code>.
     */
    public VuePeiEdpRemocra setEssaiPressionDynamique(BigDecimal essaiPressionDynamique) {
        this.essaiPressionDynamique = essaiPressionDynamique;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.essai_debit</code>.
     */
    public BigDecimal getEssaiDebit() {
        return this.essaiDebit;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.essai_debit</code>.
     */
    public VuePeiEdpRemocra setEssaiDebit(BigDecimal essaiDebit) {
        this.essaiDebit = essaiDebit;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.etat</code>.
     */
    public String getEtat() {
        return this.etat;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.etat</code>.
     */
    public VuePeiEdpRemocra setEtat(String etat) {
        this.etat = etat;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_derniere_visite</code>.
     */
    public LocalDateTime getDateDerniereVisite() {
        return this.dateDerniereVisite;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_derniere_visite</code>.
     */
    public VuePeiEdpRemocra setDateDerniereVisite(LocalDateTime dateDerniereVisite) {
        this.dateDerniereVisite = dateDerniereVisite;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.type_derniere_visite</code>.
     */
    public String getTypeDerniereVisite() {
        return this.typeDerniereVisite;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.type_derniere_visite</code>.
     */
    public VuePeiEdpRemocra setTypeDerniereVisite(String typeDerniereVisite) {
        this.typeDerniereVisite = typeDerniereVisite;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_p</code>.
     */
    public LocalDateTime getDateMajP() {
        return this.dateMajP;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_p</code>.
     */
    public VuePeiEdpRemocra setDateMajP(LocalDateTime dateMajP) {
        this.dateMajP = dateMajP;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_e</code>.
     */
    public LocalDateTime getDateMajE() {
        return this.dateMajE;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_e</code>.
     */
    public VuePeiEdpRemocra setDateMajE(LocalDateTime dateMajE) {
        this.dateMajE = dateMajE;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_qp</code>.
     */
    public LocalDateTime getDateMajQp() {
        return this.dateMajQp;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_qp</code>.
     */
    public VuePeiEdpRemocra setDateMajQp(LocalDateTime dateMajQp) {
        this.dateMajQp = dateMajQp;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_cf</code>.
     */
    public LocalDateTime getDateMajCf() {
        return this.dateMajCf;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_cf</code>.
     */
    public VuePeiEdpRemocra setDateMajCf(LocalDateTime dateMajCf) {
        this.dateMajCf = dateMajCf;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_np</code>.
     */
    public LocalDateTime getDateMajNp() {
        return this.dateMajNp;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_np</code>.
     */
    public VuePeiEdpRemocra setDateMajNp(LocalDateTime dateMajNp) {
        this.dateMajNp = dateMajNp;
        return this;
    }

    /**
     * Getter for <code>edp.vue_pei_edp_remocra.date_maj_mj</code>.
     */
    public LocalDateTime getDateMajMj() {
        return this.dateMajMj;
    }

    /**
     * Setter for <code>edp.vue_pei_edp_remocra.date_maj_mj</code>.
     */
    public VuePeiEdpRemocra setDateMajMj(LocalDateTime dateMajMj) {
        this.dateMajMj = dateMajMj;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VuePeiEdpRemocra (");

        sb.append(gidEdp);
        sb.append(", ").append(reference);
        sb.append(", ").append(geometry1);
        sb.append(", ").append(typeObjet);
        sb.append(", ").append(prive);
        sb.append(", ").append(precisionPeiPrive);
        sb.append(", ").append(adresse);
        sb.append(", ").append(notesLocalisation);
        sb.append(", ").append(diametre);
        sb.append(", ").append(modele);
        sb.append(", ").append(diametreCanalisation);
        sb.append(", ").append(materiauCanalisation);
        sb.append(", ").append(dateEssai);
        sb.append(", ").append(essaiPressionStatique);
        sb.append(", ").append(essaiPressionDynamique);
        sb.append(", ").append(essaiDebit);
        sb.append(", ").append(etat);
        sb.append(", ").append(dateDerniereVisite);
        sb.append(", ").append(typeDerniereVisite);
        sb.append(", ").append(dateMajP);
        sb.append(", ").append(dateMajE);
        sb.append(", ").append(dateMajQp);
        sb.append(", ").append(dateMajCf);
        sb.append(", ").append(dateMajNp);
        sb.append(", ").append(dateMajMj);

        sb.append(")");
        return sb.toString();
    }
}
