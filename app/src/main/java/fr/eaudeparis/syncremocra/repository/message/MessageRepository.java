package fr.eaudeparis.syncremocra.repository.message;

import static fr.eaudeparis.syncremocra.db.model.tables.Message.MESSAGE;
import static fr.eaudeparis.syncremocra.db.model.tables.MotifIndispoActif.MOTIF_INDISPO_ACTIF;
import static fr.eaudeparis.syncremocra.db.model.tables.ReferentielAnomalies.REFERENTIEL_ANOMALIES;
import static fr.eaudeparis.syncremocra.db.model.tables.ReferentielHydrantDiametres.REFERENTIEL_HYDRANT_DIAMETRES;
import static fr.eaudeparis.syncremocra.db.model.tables.ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES;
import static fr.eaudeparis.syncremocra.db.model.tables.TracabiliteIndispo.TRACABILITE_INDISPO;
import static fr.eaudeparis.syncremocra.db.model.tables.TracabilitePei.TRACABILITE_PEI;
import static fr.eaudeparis.syncremocra.db.model.tables.TypeErreur.TYPE_ERREUR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.persist.Transactional;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.ReferentielMarquesModeles;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TracabilitePei;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TypeErreur;
import fr.eaudeparis.syncremocra.job.NotificationJob;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import fr.eaudeparis.syncremocra.repository.message.model.MessageModel;
import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import fr.eaudeparis.syncremocra.repository.pei.PeiRepository;
import fr.eaudeparis.syncremocra.repository.typeErreur.model.TypeErreurModel;
import fr.eaudeparis.syncremocra.util.APIAuthentException;
import fr.eaudeparis.syncremocra.util.APIConnectionException;
import fr.eaudeparis.syncremocra.util.InternalException;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import fr.eaudeparis.syncremocra.util.RequestException;
import fr.eaudeparis.syncremocra.util.RequestManager;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageRepository {

  private final DSLContext context;

  private static Logger logger = LoggerFactory.getLogger(MessageRepository.class);

  @Inject private ErreurRepository erreurRepository;

  @Inject
  MessageRepository(DSLContext context) {
    this.context = context;
  }

  @Inject RequestManager requestManager;

  @Inject PeiRepository peiRepository;

  @Inject ParametresRepository parametresRepository;

  @Inject NotificationJob notificationJob;

  /** Retourne la liste des messages a traiter ou en erreur à rejouer */
  private List<MessageModel> getListeMessages() {
    return context
        .selectFrom(MESSAGE)
        .where(
            MESSAGE
                .STATUT
                .eq("A TRAITER")
                .or(MESSAGE.STATUT.eq("EN ERREUR").and(MESSAGE.SYNCHRONISER.eq(true))))
        .orderBy(MESSAGE.DATE.asc())
        .fetchInto(MessageModel.class);
  }

  /**
   * Traite tous les messages a traiter et en erreur n'ayant pas atteint leur nombre maximum de
   * synchronisations possibles
   */
  @Transactional
  public void traiterMessages() {
    this.parametresRepository.set("FLAG_STATUS_PUSHWORKER", "EN COURS");
    List<MessageModel> messagesATraiter = this.getListeMessages();

    logger.info("Traitement des messages : " + messagesATraiter.size() + " a traiter");

    boolean continuer = true;
    for (int i = 0; i < messagesATraiter.size() && continuer; i++) {
      MessageModel message = messagesATraiter.get(i);
      logger.info("Traitement message " + message.getId());
      ObjectMapper mapper = new ObjectMapper();

      String reference =
          context
              .select(TRACABILITE_PEI.REFERENCE)
              .from(TRACABILITE_PEI)
              .where(TRACABILITE_PEI.ID.equal(message.getId_traca_pei()))
              .fetchOneInto(String.class);

      ObjectNode data = null;
      String path = null;
      String methode = null;
      String statutFinal = "TRAITE";

      try {

        if ("INDISPO_TEMPORAIRE".equalsIgnoreCase(message.getType())) {
          logger.debug("Traitement INDISPO_TEMPORAIRE " + message.getId());
          ObjectNode dataIndispo = this.traiterMessageVisite(message, reference);
          if (dataIndispo != null) {
            data = dataIndispo.get("data").deepCopy();
            path = dataIndispo.get("path").asText();
            methode = dataIndispo.get("methode").asText();
            statutFinal = "A VERIFIER";
          }

        } else if ("CARACTERISTIQUES".equalsIgnoreCase(message.getType())) {
          logger.debug("Traitement CARACTERISTIQUES " + message.getId());
          data = this.traiterMessageCaracteristiques(message, reference);
          path = "/api/deci/pei/" + reference + "/caracteristiques";
          methode = "PUT";
        } else if ("SPECIFIQUE".equalsIgnoreCase(message.getType())) {
          logger.debug("Traitement SPECIFIQUE " + message.getId());
          data = this.traiterMessageSpecifique(message, reference);
          path = "/api/deci/pei/" + reference + "/visites";
          methode = "POST";
        } else if ("MANUELLE".equalsIgnoreCase(message.getType())) {
          logger.debug("Traitement MANUELLE " + message.getId());
          data = this.traiterMessageManuelle(message, reference);
          path = "/api/deci/pei/" + reference + "/visites";
          methode = "POST";
        }

        String json = mapper.writeValueAsString(data);

        context
            .update(MESSAGE)
            .set(MESSAGE.JSON, json)
            .where(MESSAGE.ID.eq(message.getId()))
            .execute();

        /**
         * Envoi à l'API. Si codeRetour -1 (connexion API échouée) ou -2 (authent échouée), l'erreur
         * a déjà été traitée par RequestManager (éventuellement par une ResponseException)
         */
        if (methode != null && path != null && json != null) {
          Integer codeRetour = this.requestManager.sendRequest(methode, path, json);
          // si j'ai eu une réponse de l'api, c'est qu'elle fonctionne
          if (codeRetour != null) {
            // si le message était en erreur, on notifie que finalement le message est passé
            notifRetourNormal(message);
          }
          if (codeRetour != null
              && (codeRetour == HttpURLConnection.HTTP_CREATED
                  || codeRetour == HttpURLConnection.HTTP_OK)) { // Visite créée avec succès

            /**
             * Si le message est à vérifier, on planifie cette vérification à partir de h+1 minute
             * 30 afin d'être certain que le traitement PDI côté remocra a bien pu se lancer et
             * effectuer les modifications en base
             */
            Instant dateVerif = null;
            if ("A VERIFIER".equalsIgnoreCase(statutFinal)) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(new Date());
              cal.add(Calendar.MINUTE, 5);
              cal.add(Calendar.SECOND, 30);
              dateVerif = cal.toInstant();
            }

            context
                .update(MESSAGE)
                .set(MESSAGE.STATUT, statutFinal)
                .set(MESSAGE.SYNCHRONISATIONS, MESSAGE.SYNCHRONISATIONS.add(1))
                .set(MESSAGE.SYNCHRONISER, false)
                .set(
                    MESSAGE.DATE_DEBUT_VERIF,
                    (dateVerif != null)
                        ? dateVerif.atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .where(MESSAGE.ID.eq(message.getId()))
                .execute();

            logger.info("Traitement terminé avec succès");
          }
        }

      } catch (JsonProcessingException e) {
        logger.error(
            "Message " + message.getId() + " erreur de création du contenu de la requete", e);
        e.printStackTrace();
      } catch (RequestException
          | InternalException e) { // Erreur renvoyée par l'API ou erreur interne (hors pb authent)

        logger.error("Message " + message.getId() + " erreur echange API", e);

        boolean rejouer = false;

        String codeErreur = null;
        String messageErreur = null;
        if (e instanceof InternalException) {
          codeErreur = ((InternalException) e).getCodeErreur();
          messageErreur = ((InternalException) e).getMessage();
        } else if (e instanceof RequestException) {
          codeErreur = ((RequestException) e).getCodeErreur();
          messageErreur = ((RequestException) e).getMessage();
        }

        // Selon l'erreur rencontrée, on regarde le nombre de synchro max pour
        // déterminer si le
        // message doit être rejoué
        if (codeErreur != null) {
          TypeErreurModel typeErreur =
              context
                  .selectFrom(TYPE_ERREUR)
                  .where(TYPE_ERREUR.CODE.equal(codeErreur.toString()))
                  .fetchOneInto(TypeErreurModel.class);

          if (typeErreur != null
              && message.getSynchronisations() + 1 < typeErreur.getIterations()) {
            rejouer = true;
          } else { // Si n'est pas à rejouer, on ajoute une erreur à notifier
            this.erreurRepository.addError(
                codeErreur.toString(), messageErreur, Long.valueOf(message.getId()));
          }
        }

        context
            .update(MESSAGE)
            .set(MESSAGE.STATUT, "EN ERREUR")
            .set(MESSAGE.SYNCHRONISATIONS, MESSAGE.SYNCHRONISATIONS.add(1))
            .set(MESSAGE.SYNCHRONISER, rejouer)
            .set(MESSAGE.ERREUR, messageErreur)
            .where(MESSAGE.ID.eq(message.getId()))
            .execute();

        logger.info("Traitement terminé, une erreur a été rencontrée");

      } catch (APIConnectionException
          | APIAuthentException e) { // Erreur de connexion à l'API (connexion API ou authent)
        boolean rejouer = false;
        String typeErreur = null;
        if (e instanceof APIConnectionException) {

          rejouer = true; // Si erreur réseau alors on autorise de retenter la prochaine fois
          typeErreur = "0003";

          logger.info("Erreur de connexion à l'API détectée, fin du traitement des messages");
          logger.info(
              "Une nouvelle tentative de synchronisation sera effectuée lors du prochain"
                  + " déclenchement du PushWorker");
        } else if (e instanceof APIAuthentException) {

          typeErreur = "0200";
          logger.info("Erreur d'authentification à l'API détectée, fin du traitement des messages");
        }

        // Nombre de rejeu max pour une erreur de connexion à l'API
        Integer nbSynchroMax =
            context
                .select(TYPE_ERREUR.ITERATIONS)
                .from(TYPE_ERREUR)
                .where(TYPE_ERREUR.CODE.eq(typeErreur))
                .fetchOneInto(Integer.class);

        String messageErreur =
            context
                .select(TYPE_ERREUR.MESSAGE_ERREUR)
                .from(TYPE_ERREUR)
                .where(TYPE_ERREUR.CODE.eq(typeErreur))
                .fetchOneInto(String.class);
        continuer = false;

        // Pour ce PEi et tous les PEI restants, on augmente le nombre de
        // synchronisations, et on
        // détermine si on peut les rejouer
        for (int j = i; j < messagesATraiter.size(); j++) {
          MessageModel m = messagesATraiter.get(j);

          context
              .update(MESSAGE)
              .set(MESSAGE.STATUT, "EN ERREUR")
              .set(MESSAGE.SYNCHRONISATIONS, MESSAGE.SYNCHRONISATIONS.add(1))
              .set(MESSAGE.SYNCHRONISER, rejouer)
              .set(MESSAGE.ERREUR, messageErreur)
              .where(MESSAGE.ID.eq(m.getId()))
              .execute();

          // On informe que le pei n'a pas été mis a jour pendant X essais
          // X étant le nombre d'itérations dans edp.type_erreur
          if (m.getSynchronisations() == 0 || m.getSynchronisations() == nbSynchroMax) {
            this.erreurRepository.addError("I1003", messageErreur, Long.valueOf(m.getId()));
          }
        }
        logger.info(
            "Traitement terminé, une erreur a été rencontrée; mise en erreur des messages"
                + " restants");
      }
    }
    this.parametresRepository.set("FLAG_STATUS_PUSHWORKER", "TERMINE");
  }

  /**
   * Traite les messages concernant les visites
   *
   * @param message Les informations du message
   * @param reference La référence du PEI lié au message
   * @return Les données JSON de mise à jour
   * @throws RequestException Erreur renvoyée par l'API
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  private ObjectNode traiterMessageVisite(MessageModel message, String reference)
      throws APIConnectionException, RequestException, APIAuthentException,
          JsonProcessingException {

    logger.info("Traitement visite " + message.getId_traca_pei());
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode indispoTemp = mapper.createObjectNode();

    TracabilitePei traca =
        context
            .selectFrom(TRACABILITE_PEI)
            .where(TRACABILITE_PEI.ID.eq(message.getId_traca_pei()))
            .fetchOneInto(TracabilitePei.class);

    // On récupère l'éventuelle indispo temporaire en cours
    Map<String, String> params = new HashMap<String, String>();
    params.put("organismeApi", "EAU_DE_PARIS");
    params.put("numeroHydrant", reference);
    params.put("statut", "EN_COURS");
    String indispoEnCours =
        this.requestManager.sendGetRequest("/api/deci/indispoTemporaire", params);

    // Si Indisponible, on créé une indispo temporaire s'il n'en existe pas déjà une
    // sur ce PEI
    if ("Indisponible".equalsIgnoreCase(traca.getEtat())) {
      if (indispoEnCours.length() <= 2) { // Null ou tableau vide
        logger.info("PEI " + reference + " indisponible - Création d'une indispo temporaire EDP");
        indispoTemp.put("methode", "POST");
        indispoTemp.put("path", "/api/deci/indispoTemporaire");
        ObjectNode data = mapper.createObjectNode();
        ArrayNode hydrants = mapper.createArrayNode();
        hydrants.add(reference);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
          data.put("date_debut", traca.getDateMajE().format(formatter));
        } catch (Exception e) {
          logger.error("Le champ date_maj_e présente une anomalie (traca " + traca.getId() + ")");
          throw e;
        }
        data.put("motif", "Mise en indisponibilité Eau de Paris");
        data.put("statut", "PLANIFIE");
        data.put("bascule_auto_dispo", true);
        data.put("bascule_auto_indispo", true);
        data.put("mel_avant_dispo", true);
        data.put("mel_avant_indispo", true);
        data.set("hydrants", hydrants);

        indispoTemp.set("data", data);

        this.remonteeMotifsIndispo(message, reference);
        return indispoTemp;
      } else {
        logger.info(
            "PEI "
                + reference
                + " indisponible - Indispo temporaire EDP déjà présente, reprise des nouveaux"
                + " motifs d'indispo");

        this.remonteeMotifsIndispo(message, reference);

        notifRetourNormal(message);
        context
            .update(MESSAGE)
            .set(MESSAGE.STATUT, "TRAITE")
            .where(MESSAGE.ID.eq(message.getId()))
            .execute();
      }
    } else if ("Disponible".equalsIgnoreCase(traca.getEtat())) {
      // Si disponible, on vérifie s'il y a des IT "en cours"
      if (indispoEnCours.length() <= 2) {
        // s'il n'y en a pas on vérifie les "PLANIFIE"
        params.put("organismeApi", "EAU_DE_PARIS");
        params.put("numeroHydrant", reference);
        params.put("statut", "PLANIFIE");
        String indispoPlanifie =
            this.requestManager.sendGetRequest("/api/deci/indispoTemporaire", params);

        if (indispoPlanifie.length()
            > 2) { // S'il existe une indispo planifie, on vérifie si elle aurait dû être en
          // cours"

          TypeReference<ArrayList<Map<String, Object>>> typeRef =
              new TypeReference<ArrayList<Map<String, Object>>>() {};
          ArrayList<Map<String, Object>> dataIndispoPlanifie =
              mapper.readValue(indispoPlanifie, typeRef);

          LocalDateTime dateDebut =
              JSONUtil.getLocalDateTime(
                  dataIndispoPlanifie.get(0), "date_debut", "yyyy-MM-dd HH:mm");
          if (dateDebut.isAfter(traca.getDateTraca())) {

            // si la date de début n'a pas commencé, il est normal qu'on ai cette IT
            // planifiée
            // message traité

            traiterIndispoTemporaire(message, reference);

          } else {
            // sinon on indique dans les logs qu'il faut laisser le temps au traitement
            // REMOcRA de
            // passer le PEI en indispo (IT) et on laisse le message a traiter pour la
            // prochaine
            // fois
            logger.info(
                "PEI "
                    + reference
                    + " : Une indisponibilité temporaire est présente mais n'a pas encore"
                    + " commencée. Attente de la mise en indisponibilité côté Remocra avant de"
                    + " réessayer de traiter ce message");
          }
        } else {

          traiterIndispoTemporaire(message, reference);
        }

      } else {
        logger.info("PEI " + reference + " disponible - Fin de l'indispo temporaire EDP active");
        TypeReference<ArrayList<Map<String, Object>>> typeRef =
            new TypeReference<ArrayList<Map<String, Object>>>() {};
        ArrayList<Map<String, Object>> dataIndispoEnCours =
            mapper.readValue(indispoEnCours, typeRef);
        Long idIndispoTemp = JSONUtil.getLong(dataIndispoEnCours.get(0), "identifiant");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        indispoTemp.put("methode", "PUT");
        indispoTemp.put("path", "/api/deci/indispoTemporaire/" + idIndispoTemp);

        ObjectNode data = mapper.createObjectNode();

        data.put("date_debut", JSONUtil.getString(dataIndispoEnCours.get(0), "date_debut"));
        data.put("date_fin", traca.getDateMajE().format(formatter));
        data.put("motif", "Mise en indisponibilité Eau de Paris");
        data.put("statut", "EN_COURS");
        data.put("bascule_auto_dispo", true);
        data.put("bascule_auto_indispo", true);
        data.put("mel_avant_dispo", true);
        data.put("mel_avant_indispo", true);

        indispoTemp.set("data", data);
        this.remonteeMotifsIndispo(message, reference);
        notifRetourNormal(message);
        return indispoTemp;
      }
    }
    return null;
  }

  private void traiterIndispoTemporaire(MessageModel message, String reference) {
    logger.info(
        "PEI "
            + reference
            + " disponible - Aucune indispo temporaire EDP active, fin du traitement du message");
    notifRetourNormal(message);
    context
        .update(MESSAGE)
        .set(MESSAGE.STATUT, "TRAITE")
        .where(MESSAGE.ID.eq(message.getId()))
        .execute();
  }

  public void notifRetourNormal(MessageModel message) {
    // si le message était en erreur, on notifie que finalement le message est passé
    if ("EN ERREUR".equalsIgnoreCase(message.getStatut())) {
      notificationJob.sendNotifConnexionOk(message);
    }
  }

  /**
   * En cas de création d'une indispo temporaire, remonte les éventuels motifs d'indispo renseignées
   * dans Remocra via une visite Non Programmee
   *
   * @param message Le message à traiter
   * @param reference La référence du PEI
   * @return Un objet JSON contenant les données de la visite créée dans Remocra
   */
  private ObjectNode remonteeMotifsIndispo(MessageModel message, String reference)
      throws RequestException, APIConnectionException, APIAuthentException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode visite = mapper.createObjectNode();
    ArrayNode arrayAnomaliesControlees = mapper.createArrayNode();
    ArrayNode arrayAnomaliesConstatees = mapper.createArrayNode();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date dateChangement =
        context
            .select(TRACABILITE_PEI.DATE_MAJ_E)
            .from(MESSAGE)
            .join(TRACABILITE_PEI)
            .on(TRACABILITE_PEI.ID.eq(MESSAGE.ID_TRACA_PEI.cast(Integer.class)))
            .where(MESSAGE.ID.eq(message.getId()))
            .fetchOneInto(Date.class);

    ArrayList<String> anomaliesAControler = new ArrayList<String>();
    ArrayList<String> anomaliesAConstater = new ArrayList<String>();

    boolean updateMotifIndispo = false;
    boolean deleteMotifIndispo = false;
    boolean arretEauEnCours = false;

    try {
      TracabilitePei traca =
          context
              .selectFrom(TRACABILITE_PEI)
              .where(TRACABILITE_PEI.ID.eq(message.getId_traca_pei()))
              .fetchOneInto(TracabilitePei.class);

      /**
       * Mise en disponible : on retire toutes les anomalies que l'on a indiqué lors de la dernière
       * maj de l'indispo (on se base sur la traca)
       */
      if ("Disponible".equalsIgnoreCase(traca.getEtat())) {

        // Récupération des indispos précédemment remontées
        List<String> motifIndispo =
            context
                .select(MOTIF_INDISPO_ACTIF.MOTIF)
                .from(MOTIF_INDISPO_ACTIF)
                .where(MOTIF_INDISPO_ACTIF.REFERENCE.eq(traca.getReference()))
                .fetchInto(String.class);

        // "ARRET EAU" est une anomalie qui ne donne pas lieu à une visite dans REMOcRA
        // si elle est
        // la seule anomalie
        if (motifIndispo.size() == 1 && motifIndispo.contains("ARRET EAU")) {
          logger.info(
              "Mise en disponible avec pour seul motif actif ARRET EAU : pas de création de"
                  + " visite");
          context
              .deleteFrom(MOTIF_INDISPO_ACTIF)
              .where(MOTIF_INDISPO_ACTIF.REFERENCE.eq(traca.getReference()))
              .execute();
          return null;
        }

        // les indisposActive ne concerne pas qu'arret eau
        else {
          logger.info(
              "Mise en disponible avec D'AUTRE motif actif que ARRET EAU : : Création de visite");
          /**
           * On remplit le tableau anomaliesAControler pour confirmer dans la visite que les
           * anomalies ont été controllées mais pas constantées
           */
          for (String code : motifIndispo) {
            anomaliesAControler.add(code);
          }
          // ici, on ne return pas le null pour continuer l'execution et descendre à la
          // création de
          // la visite

        }

      } else {
        // Mise en indispo ou modification des motifs d'indispo

        // Récupération des motfis d'indispo envoyés par EDP pour cette synchro
        List<String> motifsIndispo =
            context
                .select(DSL.upper(TRACABILITE_INDISPO.MOTIF_INDISPO))
                .from(TRACABILITE_INDISPO)
                .where(TRACABILITE_INDISPO.ID_TRACA_PEI.eq(traca.getId().longValue()))
                .and(TRACABILITE_INDISPO.STATUT_MOTIF_INDISPO.eq("EN COURS"))
                .fetchInto(String.class);

        // Récupération des motifs d'indispo actuellement actifs ajoutés via une
        // précédente
        // synchronisation
        List<String> indispoActif =
            context
                .select(MOTIF_INDISPO_ACTIF.MOTIF)
                .from(MOTIF_INDISPO_ACTIF)
                .where(MOTIF_INDISPO_ACTIF.REFERENCE.eq(traca.getReference()))
                .fetchInto(String.class);

        List<String> anomaliesAjoutees = new ArrayList<String>();
        List<String> anomaliesSupprimees = new ArrayList<String>();

        for (String s : motifsIndispo) {
          if (!indispoActif.contains(s)) {
            anomaliesAjoutees.add(s);
          }
        }
        for (String s : indispoActif) {
          if (!motifsIndispo.contains(s)) {
            anomaliesSupprimees.add(s);
          }
        }

        // Si ARRET EAU en cours, on l'ajoute (ou le maintient) dans la liste des motifs
        // d'indispo
        // actifs.
        if (anomaliesAjoutees.contains("ARRET EAU")
            || (indispoActif.contains("ARRET EAU") && !anomaliesSupprimees.contains("ARRET EAU"))) {
          arretEauEnCours = true;
        }

        /**
         * Si les seules modifications au niveau des motifs d'indispo sont sur des ARRET EAU, on ne
         * cré pas de visite côté Remocra
         */
        if (anomaliesSupprimees.size() == 0
            && anomaliesAjoutees.size() == 1
            && anomaliesAjoutees.indexOf("ARRET EAU") == 0) {
          logger.info("Modification uniquement sur ARRET EAU; pas de création de visite");
          context
              .insertInto(MOTIF_INDISPO_ACTIF)
              .set(MOTIF_INDISPO_ACTIF.REFERENCE, traca.getReference())
              .set(MOTIF_INDISPO_ACTIF.MOTIF, "ARRET EAU")
              .execute();
          return null;
        } else if (anomaliesAjoutees.size() == 0
            && anomaliesSupprimees.size() == 1
            && anomaliesSupprimees.indexOf("ARRET EAU") == 0) {
          logger.info("Modification uniquement sur ARRET EAU; pas de création de visite");
          context
              .deleteFrom(MOTIF_INDISPO_ACTIF)
              .where(
                  MOTIF_INDISPO_ACTIF
                      .REFERENCE
                      .eq(traca.getReference())
                      .and(MOTIF_INDISPO_ACTIF.MOTIF.equalIgnoreCase("ARRET EAU")))
              .execute();
          return null;
        }

        // Si on détecte d'autres modifs sur des motifs autre que ARRET EAU, on créé une
        // visite dans
        // Remocra pour les remonter
        logger.info("Modification autre que uniquement ARRET EAU; création de visite");

        // Reprise des anomalies constatées à cette syncrho
        for (String code : motifsIndispo) {
          anomaliesAControler.add(code);
          anomaliesAConstater.add(code);
        }

        // On ajoute en contrôle les anomalies supprimées (aka constatées lors de la
        // dernière
        // synchro et pas indiquées lors de cette synchro)
        for (String code : indispoActif) {
          if (!anomaliesAControler.contains(code) && !"ARRET EAU".equalsIgnoreCase(code)) {
            anomaliesAControler.add(code);
          }
        }
      }

      updateMotifIndispo = true;

      // Transformation des motifs d'indispo en anomalies BSPP
      List<String> codesBSPPControles =
          context
              .selectDistinct(REFERENTIEL_ANOMALIES.CODE_BSPP)
              .from(REFERENTIEL_ANOMALIES)
              .where(REFERENTIEL_ANOMALIES.CODE_EDP.in(anomaliesAControler))
              .fetchInto(String.class);

      List<String> codesBSPPConstates =
          context
              .selectDistinct(REFERENTIEL_ANOMALIES.CODE_BSPP)
              .from(REFERENTIEL_ANOMALIES)
              .where(REFERENTIEL_ANOMALIES.CODE_EDP.in(anomaliesAConstater))
              .fetchInto(String.class);

      for (String s : codesBSPPControles) {
        arrayAnomaliesControlees.add(s);
      }

      for (String s : codesBSPPConstates) {
        arrayAnomaliesConstatees.add(s);
      }

      visite.put("contexte", "NP");
      visite.put("date", formatter.format(dateChangement));
      visite.set("anomaliesControlees", arrayAnomaliesControlees);
      visite.set("anomaliesConstatees", arrayAnomaliesConstatees);
      visite.put("agent1", "Eau de Paris");

      String json = mapper.writeValueAsString(visite);
      logger.info("JSON remontée des motifs d'indispo : " + json);
      context
          .update(MESSAGE)
          .set(MESSAGE.JSON_CREATION_VISITE, json)
          .where(MESSAGE.ID.eq(message.getId()))
          .execute();

      Integer codeRetour =
          this.requestManager.sendRequest("POST", "/api/deci/pei/" + reference + "/visites", json);
      logger.info("Disponibilité: création d'une visite (retour : " + codeRetour + ")");

      // On modifie les motifs d'indispo actifs APRES la création de visite et si elle
      // s'est bien
      // passé
      // pour éviter corrompre le suivi en cas d'échec de la requête
      if (codeRetour == 201) {
        if (updateMotifIndispo) {

          context
              .deleteFrom(MOTIF_INDISPO_ACTIF)
              .where(MOTIF_INDISPO_ACTIF.REFERENCE.eq(traca.getReference()))
              .execute();

          for (String s : anomaliesAConstater) {
            if (!"ARRET EAU".equalsIgnoreCase(s)) {
              context
                  .insertInto(MOTIF_INDISPO_ACTIF)
                  .set(MOTIF_INDISPO_ACTIF.REFERENCE, traca.getReference())
                  .set(MOTIF_INDISPO_ACTIF.MOTIF, s)
                  .execute();
            }
          }
          if (arretEauEnCours) {
            context
                .insertInto(MOTIF_INDISPO_ACTIF)
                .set(MOTIF_INDISPO_ACTIF.REFERENCE, traca.getReference())
                .set(MOTIF_INDISPO_ACTIF.MOTIF, "ARRET EAU")
                .execute();
          }
        } else if (deleteMotifIndispo) {
          context
              .deleteFrom(MOTIF_INDISPO_ACTIF)
              .where(MOTIF_INDISPO_ACTIF.REFERENCE.eq(traca.getReference()))
              .execute();
        }
      }

      return visite;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Traite les messages concernant les données patrimoniales
   *
   * @param message Les informations du message
   * @param reference La référence du PEI
   * @return RequestException Erreur renvoyée par l'API
   * @throws APIConnectionException Impossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier à l'API
   */
  private ObjectNode traiterMessageCaracteristiques(MessageModel message, String reference)
      throws APIConnectionException, APIAuthentException, RequestException, InternalException {
    try {
      String dataPei =
          this.requestManager.sendGetRequest("/api/deci/pei/" + reference + "/caracteristiques");

      ObjectMapper mapper = new ObjectMapper();
      TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
      Map<String, Object> data = mapper.readValue(dataPei, typeRef);

      ObjectNode caracteristiques = mapper.createObjectNode();

      TracabilitePei traca =
          context
              .selectFrom(TRACABILITE_PEI)
              .where(TRACABILITE_PEI.ID.eq(message.getId_traca_pei()))
              .fetchOneInto(TracabilitePei.class);

      String codeDiametre =
          context
              .select(REFERENTIEL_HYDRANT_DIAMETRES.CODE_BSPP)
              .from(REFERENTIEL_HYDRANT_DIAMETRES)
              .where(REFERENTIEL_HYDRANT_DIAMETRES.CODE_EDP.eq(String.valueOf(traca.getDiametre())))
              .fetchOneInto(String.class);

      // Code de diamètre fourni null ou ne possédant pas de correspondance dans le
      // référentiel
      if (codeDiametre == null) {
        TypeErreur typeErreur =
            context
                .selectFrom(TYPE_ERREUR)
                .where(TYPE_ERREUR.CODE.equal("I1001"))
                .fetchOneInto(TypeErreur.class);
        throw new InternalException(typeErreur.getCode(), typeErreur.getMessageErreur());
      }
      // Détermination du couple marque/modèle
      String codeModele = null;
      String codeMarque = null;
      if (traca.getModele() != null && traca.getModele().length() > 0) {
        // Requête jooq retournant marque+modele du referentiel
        ReferentielMarquesModeles marqueModele =
            context
                .selectFrom(REFERENTIEL_MARQUES_MODELES)
                .where(
                    REFERENTIEL_MARQUES_MODELES.CODE_MODELE_EDP.equalIgnoreCase(traca.getModele()))
                .fetchOneInto(ReferentielMarquesModeles.class);

        if (marqueModele != null) {
          codeMarque = marqueModele.getCodeMarqueRemocra();
          codeModele = marqueModele.getCodeModeleRemocra();
        } else {
          TypeErreur typeErreur =
              context
                  .selectFrom(TYPE_ERREUR)
                  .where(TYPE_ERREUR.CODE.equal("I1000"))
                  .fetchOneInto(TypeErreur.class);
          throw new InternalException(typeErreur.getCode(), typeErreur.getMessageErreur());
        }
      }

      // Données EDP
      caracteristiques.put("codeMarque", codeMarque);
      caracteristiques.put("codeModele", codeModele);
      caracteristiques.put(
          "diametreCanalisation",
          traca.getDiametreCanalisation() != null
              ? Integer.valueOf(traca.getDiametreCanalisation())
              : null);
      caracteristiques.put("codeDiametre", codeDiametre);

      // Consolidation données remocra
      caracteristiques.put("capaciteIllimitee", JSONUtil.getBoolean(data, "illimite"));
      caracteristiques.put("ressourceIncertaine", JSONUtil.getBoolean(data, "incertaine"));
      caracteristiques.put("codeNatureReseau", JSONUtil.getString(data, "natureReseau"));
      caracteristiques.put(
          "codeNatureCanalisation", JSONUtil.getString(data, "natureCanalisation"));
      caracteristiques.put("reseauSurpresse", JSONUtil.getBoolean(data, "reseauSurpresse"));
      caracteristiques.put("reseauAdditive", JSONUtil.getBoolean(data, "reseauAdditive"));
      caracteristiques.put("capacite", JSONUtil.getString(data, "capacite"));
      caracteristiques.put("debitAppoint", JSONUtil.getDouble(data, "debitAppoint"));
      caracteristiques.put("codeMateriau", JSONUtil.getString(data, "codeMateriau"));
      caracteristiques.put("equipeHBE", JSONUtil.getBoolean(data, "equipeHBE"));
      caracteristiques.put("peiJumele", JSONUtil.getString(data, "jumelage"));
      caracteristiques.put("inviolabilite", JSONUtil.getBoolean(data, "inviolabilite"));
      caracteristiques.put("renversable", JSONUtil.getBoolean(data, "renversable"));
      caracteristiques.put("anneeFabrication", JSONUtil.getInteger(data, "anneeFabrication"));

      return caracteristiques;

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Message lié à une intervention Eau de Paris
   *
   * @param message Message a synchroniser
   * @param reference Référence du PEI
   * @return Un json contenant les données de la visite à envoyer à Remocra
   */
  private ObjectNode traiterMessageSpecifique(MessageModel message, String reference)
      throws APIConnectionException, RequestException, JsonProcessingException,
          APIAuthentException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode visite = mapper.createObjectNode();

    ArrayNode arrayAnomaliesControlees = mapper.createArrayNode();
    ArrayNode arrayAnomaliesConstatees = mapper.createArrayNode();

    TracabilitePei traca =
        context
            .selectFrom(TRACABILITE_PEI)
            .where(TRACABILITE_PEI.ID.eq(message.getId_traca_pei()))
            .fetchOneInto(TracabilitePei.class);

    String typeDerniereVisite = traca.getTypeDerniereVisite().toUpperCase();

    if (typeDerniereVisite.startsWith("PICF")) {
      visite.put("contexte", "CTRL");
    } else if (typeDerniereVisite.startsWith("PIQP")) {
      visite.put("contexte", "CTRL");
      visite = this.recuperationsValeursDebitPression(visite, traca);
    } else if ((typeDerniereVisite.startsWith("NPQP"))) {
      // Dans le cas d'une NPQP, le comportement attendu est identique à celui d'une
      // intervention manuelle (NP avec anomalies sans débit/pression)
      return this.traiterMessageManuelle(message, reference);
    } else if ((typeDerniereVisite.startsWith("NP"))) {
      visite.put("contexte", "NP");
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Afichage de l'erreur dans les logs
    try {
      visite.put("date", traca.getDateDerniereVisite().format(formatter));
    } catch (Exception e) {
      logger.error(
          "La date de dernière visite du message "
              + message.getId()
              + " (traca "
              + traca.getId()
              + ") présente une anomalie");
      throw e;
    }
    visite.put("agent1", "Eau de Paris");
    visite = this.recuperationAnomalies(visite, traca);
    return visite;
  }

  /**
   * Message lié à une intervention manuelle Eau de Paris
   *
   * @param message Message a synchroniser
   * @param reference Référence du PEI
   * @return Un json contenant les données de la visite à envoyer à Remocra
   */
  private ObjectNode traiterMessageManuelle(MessageModel message, String reference)
      throws APIConnectionException, RequestException, JsonProcessingException,
          APIAuthentException {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode visite = mapper.createObjectNode();

    ArrayNode arrayAnomaliesControlees = mapper.createArrayNode();
    ArrayNode arrayAnomaliesConstatees = mapper.createArrayNode();

    TracabilitePei traca =
        context
            .selectFrom(TRACABILITE_PEI)
            .where(TRACABILITE_PEI.ID.eq(message.getId_traca_pei()))
            .fetchOneInto(TracabilitePei.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    if ((traca.getTypeDerniereVisite() != null
            && traca.getTypeDerniereVisite().toUpperCase().startsWith("NPQP"))
        && !message.getType().toUpperCase().equalsIgnoreCase("MANUELLE")) {
      visite.put("date", traca.getDateDerniereVisite().format(formatter));
    } else {
      visite.put("date", traca.getDateEssai().format(formatter));
    }
    visite.put("contexte", "NP");
    visite.put("agent1", "Eau de Paris");
    visite = this.recuperationAnomalies(visite, traca);
    return visite;
  }

  /**
   * Récupère les valeurs de débit/pression d'un PEI et les ajoute à une visite
   *
   * @param v ObjectNode contenant les informations de la visite
   * @param traca la ligne de tracabilité du PEI souhaité
   * @return Les données de la visite
   */
  private ObjectNode recuperationsValeursDebitPression(ObjectNode v, TracabilitePei traca) {
    v.put(
        "pression",
        (traca.getEssaiPressionStatique() != null)
            ? traca.getEssaiPressionStatique().doubleValue()
            : null);
    v.put(
        "pressionDynamique",
        (traca.getEssaiPressionDynamique() != null)
            ? traca.getEssaiPressionDynamique().doubleValue()
            : null);
    v.put("debit", (traca.getEssaiDebit() != null) ? traca.getEssaiDebit().intValue() : null);
    return v;
  }

  /**
   * Récupère les anomalies d'un PEI suivant certaines contraintes métier transmises par Eau de
   * Paris
   *
   * @param v ObjectNode contenant les informations de la visite
   * @param traca la ligne de tracabilité du PEI souhaité
   * @return Les données de la visite
   */
  private ObjectNode recuperationAnomalies(ObjectNode v, TracabilitePei traca)
      throws APIConnectionException, RequestException, JsonProcessingException,
          APIAuthentException {
    String typeDerniereVisite =
        (traca.getTypeDerniereVisite() != null)
            ? traca.getTypeDerniereVisite().toUpperCase()
            : null;

    ObjectMapper mapper = new ObjectMapper();
    ArrayNode arrayAnomaliesControlees = mapper.createArrayNode();
    ArrayNode arrayAnomaliesConstatees = mapper.createArrayNode();
    ArrayList<String> anomaliesBloquante =
        this.peiRepository.getNaturesAnomaliesAccessibles(
            traca.getReference(), v.get("contexte").textValue(), true);
    ArrayList<String> toutesAnomalies =
        this.peiRepository.getNaturesAnomaliesAccessibles(
            traca.getReference(), v.get("contexte").textValue(), false);

    if (typeDerniereVisite != null) {
      // Aucune anomalie
      if ((typeDerniereVisite.contains("EN SERVICE"))
          || (typeDerniereVisite.contains("CONTROLE REALISE"))) {
        for (String code : anomaliesBloquante) {
          arrayAnomaliesControlees.add(code);
        }
        v.set("anomaliesControlees", arrayAnomaliesControlees);
        v.set("anomaliesConstatees", arrayAnomaliesConstatees);
        return v;
      }

      // Déposé ou en travaux

      if (typeDerniereVisite.contains("TRAVAUX") || typeDerniereVisite.contains("RENOUVELER")) {
        arrayAnomaliesConstatees = this.addAnomalie(arrayAnomaliesConstatees, "BSPP_APDP");
        arrayAnomaliesControlees = this.addAnomalie(arrayAnomaliesControlees, "BSPP_APDP");
      }

      // Sans eaux
      if (typeDerniereVisite.contains("SANS EAU")) {
        arrayAnomaliesConstatees = this.addAnomalie(arrayAnomaliesConstatees, "BSPP_APSE");
        arrayAnomaliesControlees = this.addAnomalie(arrayAnomaliesControlees, "BSPP_APSE");
      }

      // Signalisation dégradée (BI), Identification dégradée (PI)
      if (typeDerniereVisite.contains("PB SIGNALETIQUE")) {
        if (toutesAnomalies.contains("BSPP_PISD")) {
          arrayAnomaliesConstatees = this.addAnomalie(arrayAnomaliesConstatees, "BSPP_PISD");
          arrayAnomaliesControlees = this.addAnomalie(arrayAnomaliesControlees, "BSPP_PISD");
        }

        if (toutesAnomalies.contains("BSPP_BISD")) {
          arrayAnomaliesConstatees = this.addAnomalie(arrayAnomaliesConstatees, "BSPP_BISD");
          arrayAnomaliesControlees = this.addAnomalie(arrayAnomaliesControlees, "BSPP_BISD");
        }
      }
    }

    v.set("anomaliesControlees", arrayAnomaliesControlees);
    v.set("anomaliesConstatees", arrayAnomaliesConstatees);
    return v;
  }

  /**
   * Ajoute une anomalie dans un tableau JSON Si l'anomalie est déjà présente, elle n'est pas
   * ajoutée afin d'éviter les doublons
   *
   * @param an Le tableau JSON
   * @param anomalie L'anomalie à ajouter
   */
  public ArrayNode addAnomalie(ArrayNode an, String anomalie) {
    if (an.findValue(anomalie) == null) {
      return an.add(anomalie);
    }
    return an;
  }

  /**
   * Si le Pushworker a crash à sa dernière itération, on met tous les messages qui doivent être
   * traités en erreur et on notifie à EDP
   */
  public void handlePushWorkerFailure() {
    logger.warn("PushWorker failure detected, handling messages causing the error");
    // Message d'erreur global
    TypeErreur te =
        context
            .selectFrom(TYPE_ERREUR)
            .where(TYPE_ERREUR.CODE.eq("I0001"))
            .fetchOneInto(TypeErreur.class);
    this.erreurRepository.addError(te.getCode(), te.getMessageErreur(), null);

    // Message d'erreur pour chaque PEI impacté
    for (MessageModel m : this.getListeMessages()) {
      te =
          context
              .selectFrom(TYPE_ERREUR)
              .where(TYPE_ERREUR.CODE.eq("I1004"))
              .fetchOneInto(TypeErreur.class);

      context
          .update(MESSAGE)
          .set(MESSAGE.STATUT, "EN ERREUR")
          .set(MESSAGE.SYNCHRONISATIONS, m.getSynchronisations() + 1)
          .set(MESSAGE.SYNCHRONISER, false)
          .set(MESSAGE.ERREUR, te.getMessageErreur())
          .where(MESSAGE.ID.eq(m.getId()))
          .execute();

      this.erreurRepository.addError(te.getCode(), te.getMessageErreur(), Long.valueOf(m.getId()));
    }

    this.parametresRepository.set("FLAG_STATUS_PUSHWORKER", "TERMINE");
  }
}
