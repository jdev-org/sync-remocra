/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.pojos;


import java.io.Serializable;


/**
 * Typologie des erreurs pouvant être rencontrées lors du processus global 
 * de synchronisation entre REMOCRA et EDP
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TypeErreur implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String  code;
    private Integer iterations;
    private String  contexte;
    private String  messageAction;
    private String  messageErreur;

    public TypeErreur() {}

    public TypeErreur(TypeErreur value) {
        this.id = value.id;
        this.code = value.code;
        this.iterations = value.iterations;
        this.contexte = value.contexte;
        this.messageAction = value.messageAction;
        this.messageErreur = value.messageErreur;
    }

    public TypeErreur(
        Integer id,
        String  code,
        Integer iterations,
        String  contexte,
        String  messageAction,
        String  messageErreur
    ) {
        this.id = id;
        this.code = code;
        this.iterations = iterations;
        this.contexte = contexte;
        this.messageAction = messageAction;
        this.messageErreur = messageErreur;
    }

    /**
     * Getter for <code>edp.type_erreur.id</code>. Identifiant interne
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>edp.type_erreur.id</code>. Identifiant interne
     */
    public TypeErreur setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>edp.type_erreur.code</code>. Code du type d'erreur. Code identique à celui retourné par l'API REMOCRA dans le cas d'une erreur lié à un appel API
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Setter for <code>edp.type_erreur.code</code>. Code du type d'erreur. Code identique à celui retourné par l'API REMOCRA dans le cas d'une erreur lié à un appel API
     */
    public TypeErreur setCode(String code) {
        this.code = code;
        return this;
    }

    /**
     * Getter for <code>edp.type_erreur.iterations</code>. Nombre d'itérations autorisé pour ce type d'erreur. Valable dans le cas des erreurs associés à des messages à traiter ou dans le cas des erreurs système
     */
    public Integer getIterations() {
        return this.iterations;
    }

    /**
     * Setter for <code>edp.type_erreur.iterations</code>. Nombre d'itérations autorisé pour ce type d'erreur. Valable dans le cas des erreurs associés à des messages à traiter ou dans le cas des erreurs système
     */
    public TypeErreur setIterations(Integer iterations) {
        this.iterations = iterations;
        return this;
    }

    /**
     * Getter for <code>edp.type_erreur.contexte</code>. Contexte permettant d'adresser les erreurs lors de la phase de notification
     */
    public String getContexte() {
        return this.contexte;
    }

    /**
     * Setter for <code>edp.type_erreur.contexte</code>. Contexte permettant d'adresser les erreurs lors de la phase de notification
     */
    public TypeErreur setContexte(String contexte) {
        this.contexte = contexte;
        return this;
    }

    /**
     * Getter for <code>edp.type_erreur.message_action</code>. Message type permettant d'indiquer au destinataire des erreurs le type d'action à engager
     */
    public String getMessageAction() {
        return this.messageAction;
    }

    /**
     * Setter for <code>edp.type_erreur.message_action</code>. Message type permettant d'indiquer au destinataire des erreurs le type d'action à engager
     */
    public TypeErreur setMessageAction(String messageAction) {
        this.messageAction = messageAction;
        return this;
    }

    /**
     * Getter for <code>edp.type_erreur.message_erreur</code>.
     */
    public String getMessageErreur() {
        return this.messageErreur;
    }

    /**
     * Setter for <code>edp.type_erreur.message_erreur</code>.
     */
    public TypeErreur setMessageErreur(String messageErreur) {
        this.messageErreur = messageErreur;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TypeErreur (");

        sb.append(id);
        sb.append(", ").append(code);
        sb.append(", ").append(iterations);
        sb.append(", ").append(contexte);
        sb.append(", ").append(messageAction);
        sb.append(", ").append(messageErreur);

        sb.append(")");
        return sb.toString();
    }
}
