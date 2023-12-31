/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.records;


import fr.eaudeparis.syncremocra.db.model.tables.TracabilitePei;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.locationtech.jts.geom.Point;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TracabilitePeiRecord extends UpdatableRecordImpl<TracabilitePeiRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>edp.tracabilite_pei.id</code>.
     */
    public TracabilitePeiRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.reference</code>.
     */
    public TracabilitePeiRecord setReference(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.reference</code>.
     */
    public String getReference() {
        return (String) get(1);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.geometry1</code>.
     */
    public TracabilitePeiRecord setGeometry1(Point value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.geometry1</code>.
     */
    public Point getGeometry1() {
        return (Point) get(2);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.type_objet</code>.
     */
    public TracabilitePeiRecord setTypeObjet(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.type_objet</code>.
     */
    public String getTypeObjet() {
        return (String) get(3);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.prive</code>.
     */
    public TracabilitePeiRecord setPrive(Integer value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.prive</code>.
     */
    public Integer getPrive() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.precision_pei_prive</code>.
     */
    public TracabilitePeiRecord setPrecisionPeiPrive(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.precision_pei_prive</code>.
     */
    public String getPrecisionPeiPrive() {
        return (String) get(5);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.adresse</code>.
     */
    public TracabilitePeiRecord setAdresse(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.adresse</code>.
     */
    public String getAdresse() {
        return (String) get(6);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.notes_localisation</code>.
     */
    public TracabilitePeiRecord setNotesLocalisation(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.notes_localisation</code>.
     */
    public String getNotesLocalisation() {
        return (String) get(7);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.diametre</code>.
     */
    public TracabilitePeiRecord setDiametre(Integer value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.diametre</code>.
     */
    public Integer getDiametre() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.modele</code>.
     */
    public TracabilitePeiRecord setModele(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.modele</code>.
     */
    public String getModele() {
        return (String) get(9);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.diametre_canalisation</code>.
     */
    public TracabilitePeiRecord setDiametreCanalisation(String value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.diametre_canalisation</code>.
     */
    public String getDiametreCanalisation() {
        return (String) get(10);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.etat</code>.
     */
    public TracabilitePeiRecord setEtat(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.etat</code>.
     */
    public String getEtat() {
        return (String) get(11);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_p</code>.
     */
    public TracabilitePeiRecord setDateMajP(LocalDateTime value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_p</code>.
     */
    public LocalDateTime getDateMajP() {
        return (LocalDateTime) get(12);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_e</code>.
     */
    public TracabilitePeiRecord setDateMajE(LocalDateTime value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_e</code>.
     */
    public LocalDateTime getDateMajE() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_traca</code>.
     */
    public TracabilitePeiRecord setDateTraca(LocalDateTime value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_traca</code>.
     */
    public LocalDateTime getDateTraca() {
        return (LocalDateTime) get(14);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_essai</code>.
     */
    public TracabilitePeiRecord setDateEssai(LocalDateTime value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_essai</code>.
     */
    public LocalDateTime getDateEssai() {
        return (LocalDateTime) get(15);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.essai_pression_statique</code>.
     */
    public TracabilitePeiRecord setEssaiPressionStatique(BigDecimal value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.essai_pression_statique</code>.
     */
    public BigDecimal getEssaiPressionStatique() {
        return (BigDecimal) get(16);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.essai_pression_dynamique</code>.
     */
    public TracabilitePeiRecord setEssaiPressionDynamique(BigDecimal value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.essai_pression_dynamique</code>.
     */
    public BigDecimal getEssaiPressionDynamique() {
        return (BigDecimal) get(17);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.essai_debit</code>.
     */
    public TracabilitePeiRecord setEssaiDebit(BigDecimal value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.essai_debit</code>.
     */
    public BigDecimal getEssaiDebit() {
        return (BigDecimal) get(18);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_qp</code>.
     */
    public TracabilitePeiRecord setDateMajQp(LocalDateTime value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_qp</code>.
     */
    public LocalDateTime getDateMajQp() {
        return (LocalDateTime) get(19);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_derniere_visite</code>.
     */
    public TracabilitePeiRecord setDateDerniereVisite(LocalDateTime value) {
        set(20, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_derniere_visite</code>.
     */
    public LocalDateTime getDateDerniereVisite() {
        return (LocalDateTime) get(20);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.type_derniere_visite</code>.
     */
    public TracabilitePeiRecord setTypeDerniereVisite(String value) {
        set(21, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.type_derniere_visite</code>.
     */
    public String getTypeDerniereVisite() {
        return (String) get(21);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_cf</code>.
     */
    public TracabilitePeiRecord setDateMajCf(LocalDateTime value) {
        set(22, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_cf</code>.
     */
    public LocalDateTime getDateMajCf() {
        return (LocalDateTime) get(22);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_np</code>.
     */
    public TracabilitePeiRecord setDateMajNp(LocalDateTime value) {
        set(23, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_np</code>.
     */
    public LocalDateTime getDateMajNp() {
        return (LocalDateTime) get(23);
    }

    /**
     * Setter for <code>edp.tracabilite_pei.date_maj_mj</code>.
     */
    public TracabilitePeiRecord setDateMajMj(LocalDateTime value) {
        set(24, value);
        return this;
    }

    /**
     * Getter for <code>edp.tracabilite_pei.date_maj_mj</code>.
     */
    public LocalDateTime getDateMajMj() {
        return (LocalDateTime) get(24);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TracabilitePeiRecord
     */
    public TracabilitePeiRecord() {
        super(TracabilitePei.TRACABILITE_PEI);
    }

    /**
     * Create a detached, initialised TracabilitePeiRecord
     */
    public TracabilitePeiRecord(Integer id, String reference, Point geometry1, String typeObjet, Integer prive, String precisionPeiPrive, String adresse, String notesLocalisation, Integer diametre, String modele, String diametreCanalisation, String etat, LocalDateTime dateMajP, LocalDateTime dateMajE, LocalDateTime dateTraca, LocalDateTime dateEssai, BigDecimal essaiPressionStatique, BigDecimal essaiPressionDynamique, BigDecimal essaiDebit, LocalDateTime dateMajQp, LocalDateTime dateDerniereVisite, String typeDerniereVisite, LocalDateTime dateMajCf, LocalDateTime dateMajNp, LocalDateTime dateMajMj) {
        super(TracabilitePei.TRACABILITE_PEI);

        setId(id);
        setReference(reference);
        setGeometry1(geometry1);
        setTypeObjet(typeObjet);
        setPrive(prive);
        setPrecisionPeiPrive(precisionPeiPrive);
        setAdresse(adresse);
        setNotesLocalisation(notesLocalisation);
        setDiametre(diametre);
        setModele(modele);
        setDiametreCanalisation(diametreCanalisation);
        setEtat(etat);
        setDateMajP(dateMajP);
        setDateMajE(dateMajE);
        setDateTraca(dateTraca);
        setDateEssai(dateEssai);
        setEssaiPressionStatique(essaiPressionStatique);
        setEssaiPressionDynamique(essaiPressionDynamique);
        setEssaiDebit(essaiDebit);
        setDateMajQp(dateMajQp);
        setDateDerniereVisite(dateDerniereVisite);
        setTypeDerniereVisite(typeDerniereVisite);
        setDateMajCf(dateMajCf);
        setDateMajNp(dateMajNp);
        setDateMajMj(dateMajMj);
    }
}
