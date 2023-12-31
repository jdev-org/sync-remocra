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
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer       id;
    private Long          idTracaPei;
    private LocalDateTime date;
    private String        type;
    private String        statut;
    private Boolean       synchroniser;
    private Integer       synchronisations;
    private String        erreur;
    private String        json;
    private LocalDateTime dateDebutVerif;
    private String        jsonCreationVisite;
    private String        declencheur;

    public Message() {}

    public Message(Message value) {
        this.id = value.id;
        this.idTracaPei = value.idTracaPei;
        this.date = value.date;
        this.type = value.type;
        this.statut = value.statut;
        this.synchroniser = value.synchroniser;
        this.synchronisations = value.synchronisations;
        this.erreur = value.erreur;
        this.json = value.json;
        this.dateDebutVerif = value.dateDebutVerif;
        this.jsonCreationVisite = value.jsonCreationVisite;
        this.declencheur = value.declencheur;
    }

    public Message(
        Integer       id,
        Long          idTracaPei,
        LocalDateTime date,
        String        type,
        String        statut,
        Boolean       synchroniser,
        Integer       synchronisations,
        String        erreur,
        String        json,
        LocalDateTime dateDebutVerif,
        String        jsonCreationVisite,
        String        declencheur
    ) {
        this.id = id;
        this.idTracaPei = idTracaPei;
        this.date = date;
        this.type = type;
        this.statut = statut;
        this.synchroniser = synchroniser;
        this.synchronisations = synchronisations;
        this.erreur = erreur;
        this.json = json;
        this.dateDebutVerif = dateDebutVerif;
        this.jsonCreationVisite = jsonCreationVisite;
        this.declencheur = declencheur;
    }

    /**
     * Getter for <code>edp.message.id</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>edp.message.id</code>.
     */
    public Message setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>edp.message.id_traca_pei</code>.
     */
    public Long getIdTracaPei() {
        return this.idTracaPei;
    }

    /**
     * Setter for <code>edp.message.id_traca_pei</code>.
     */
    public Message setIdTracaPei(Long idTracaPei) {
        this.idTracaPei = idTracaPei;
        return this;
    }

    /**
     * Getter for <code>edp.message.date</code>.  Date et heure du changement tel que renseigné dans la vue READ_ONLY.VUE_PEI_EDP_REMOCRA
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Setter for <code>edp.message.date</code>.  Date et heure du changement tel que renseigné dans la vue READ_ONLY.VUE_PEI_EDP_REMOCRA
     */
    public Message setDate(LocalDateTime date) {
        this.date = date;
        return this;
    }

    /**
     * Getter for <code>edp.message.type</code>. "CARACTERISTIQUES" pour les données techniques, localisation, canalisation, etc., "VISITES" pour les disponibilités et les anomalies associées
     */
    public String getType() {
        return this.type;
    }

    /**
     * Setter for <code>edp.message.type</code>. "CARACTERISTIQUES" pour les données techniques, localisation, canalisation, etc., "VISITES" pour les disponibilités et les anomalies associées
     */
    public Message setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Getter for <code>edp.message.statut</code>.  A traiter : message à jouer ou à rejouer, Terminé : le message a été joué avec succès ou avec échec le type d'erreur empêche tout rejeu
     */
    public String getStatut() {
        return this.statut;
    }

    /**
     * Setter for <code>edp.message.statut</code>.  A traiter : message à jouer ou à rejouer, Terminé : le message a été joué avec succès ou avec échec le type d'erreur empêche tout rejeu
     */
    public Message setStatut(String statut) {
        this.statut = statut;
        return this;
    }

    /**
     * Getter for <code>edp.message.synchroniser</code>. Indique si le message est à rejouer ou non
     */
    public Boolean getSynchroniser() {
        return this.synchroniser;
    }

    /**
     * Setter for <code>edp.message.synchroniser</code>. Indique si le message est à rejouer ou non
     */
    public Message setSynchroniser(Boolean synchroniser) {
        this.synchroniser = synchroniser;
        return this;
    }

    /**
     * Getter for <code>edp.message.synchronisations</code>. Nombre de fois où le message a été joué ou rejoué
     */
    public Integer getSynchronisations() {
        return this.synchronisations;
    }

    /**
     * Setter for <code>edp.message.synchronisations</code>. Nombre de fois où le message a été joué ou rejoué
     */
    public Message setSynchronisations(Integer synchronisations) {
        this.synchronisations = synchronisations;
        return this;
    }

    /**
     * Getter for <code>edp.message.erreur</code>. Message d'erreur éventuellement rencontré
     */
    public String getErreur() {
        return this.erreur;
    }

    /**
     * Setter for <code>edp.message.erreur</code>. Message d'erreur éventuellement rencontré
     */
    public Message setErreur(String erreur) {
        this.erreur = erreur;
        return this;
    }

    /**
     * Getter for <code>edp.message.json</code>.
     */
    public String getJson() {
        return this.json;
    }

    /**
     * Setter for <code>edp.message.json</code>.
     */
    public Message setJson(String json) {
        this.json = json;
        return this;
    }

    /**
     * Getter for <code>edp.message.date_debut_verif</code>.
     */
    public LocalDateTime getDateDebutVerif() {
        return this.dateDebutVerif;
    }

    /**
     * Setter for <code>edp.message.date_debut_verif</code>.
     */
    public Message setDateDebutVerif(LocalDateTime dateDebutVerif) {
        this.dateDebutVerif = dateDebutVerif;
        return this;
    }

    /**
     * Getter for <code>edp.message.json_creation_visite</code>.
     */
    public String getJsonCreationVisite() {
        return this.jsonCreationVisite;
    }

    /**
     * Setter for <code>edp.message.json_creation_visite</code>.
     */
    public Message setJsonCreationVisite(String jsonCreationVisite) {
        this.jsonCreationVisite = jsonCreationVisite;
        return this;
    }

    /**
     * Getter for <code>edp.message.declencheur</code>.
     */
    public String getDeclencheur() {
        return this.declencheur;
    }

    /**
     * Setter for <code>edp.message.declencheur</code>.
     */
    public Message setDeclencheur(String declencheur) {
        this.declencheur = declencheur;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Message (");

        sb.append(id);
        sb.append(", ").append(idTracaPei);
        sb.append(", ").append(date);
        sb.append(", ").append(type);
        sb.append(", ").append(statut);
        sb.append(", ").append(synchroniser);
        sb.append(", ").append(synchronisations);
        sb.append(", ").append(erreur);
        sb.append(", ").append(json);
        sb.append(", ").append(dateDebutVerif);
        sb.append(", ").append(jsonCreationVisite);
        sb.append(", ").append(declencheur);

        sb.append(")");
        return sb.toString();
    }
}
