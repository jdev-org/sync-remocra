package fr.eaudeparis.syncremocra.repository.pullmessage;

import static fr.eaudeparis.syncremocra.db.model.tables.PullHydrant.PULL_HYDRANT;
import static fr.eaudeparis.syncremocra.db.model.tables.PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES;
import static fr.eaudeparis.syncremocra.db.model.tables.PullHydrantVisite.PULL_HYDRANT_VISITE;
import static fr.eaudeparis.syncremocra.db.model.tables.PullMessage.PULL_MESSAGE;
import static fr.eaudeparis.syncremocra.db.model.tables.TypeErreur.TYPE_ERREUR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.PullHydrant;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TypeErreur;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import fr.eaudeparis.syncremocra.repository.pullmessage.model.PeiDiffModel;
import fr.eaudeparis.syncremocra.util.APIAuthentException;
import fr.eaudeparis.syncremocra.util.APIConnectionException;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import fr.eaudeparis.syncremocra.util.RequestException;
import fr.eaudeparis.syncremocra.util.RequestManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PullMessageRepository {

  private final DSLContext context;

  private static Logger logger = LoggerFactory.getLogger(PullMessageRepository.class);

  @Inject
  PullMessageRepository(DSLContext context) {
    this.context = context;
  }

  @Inject ParametresRepository parametresRepository;

  @Inject RequestManager requestManager;

  @Inject ErreurRepository erreurRepository;

  /** Récupération des informations depuis Remocra */
  public void recupererInformations() {
    try {
      this.parametresRepository.set("FLAG_STATUS_PULLWORKER", "EN COURS");
      String date = this.parametresRepository.get("LAST_UPDATE_PULLWORKER");

      logger.info("Récupération des modifications depuis " + date);
      Map<String, String> params = new HashMap<String, String>();
      params.put("date", date);
      String json = this.requestManager.sendGetRequest("/api/deci/pei/diff", params);

      ObjectMapper objectMapper = new ObjectMapper();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      objectMapper.setDateFormat(df);
      List<PeiDiffModel> listePei =
          objectMapper.readValue(json, new TypeReference<List<PeiDiffModel>>() {});
      logger.info(listePei.size() + " entrées récupérées");

      List<String> numeros =
          listePei.stream().map(o -> o.getNumero()).distinct().collect(Collectors.toList());
      Date lastUpdate = null;
      for (String numero : numeros) {
        PeiDiffModel modifCaracteristiques = null;
        PeiDiffModel modifVisites = null;

        // Pour chaque PEI, déterminer la modification avec la date la plus récente pour les visites
        // et caractéristiques
        List<PeiDiffModel> modifs =
            listePei.stream()
                .filter(m -> m.getNumero().equals(numero))
                .collect(Collectors.toList());
        for (PeiDiffModel pei : modifs) {
          if ("VISITES".equals(pei.getType())
              && (modifVisites == null
                  || pei.getDateModification().compareTo(modifVisites.getDateModification()) > 0)) {
            modifVisites = pei;
          } else if ("CARACTERISTIQUES".equals(pei.getType())
              && (modifVisites == null
                  || pei.getDateModification().compareTo(modifVisites.getDateModification()) > 0)) {
            modifCaracteristiques = pei;
          }
        }

        // Modification détectée sur les caractéristiques
        if (modifCaracteristiques != null) {
          logger.info(
              "["
                  + modifCaracteristiques.getNumero()
                  + "] Modification sur les caractéristiques détectée à "
                  + modifCaracteristiques.getDateModification());
          if (lastUpdate == null
              || modifCaracteristiques.getDateModification().compareTo(lastUpdate) > 0) {
            lastUpdate = modifCaracteristiques.getDateModification();
          }
          this.modificationCaracteristiques(modifCaracteristiques);
        }

        // Modification détectée sur les visites
        if (modifVisites != null) {
          logger.info(
              "["
                  + modifVisites.getNumero()
                  + "] Modification sur les visites détectée à "
                  + modifVisites.getDateModification());
          if (lastUpdate == null || modifVisites.getDateModification().compareTo(lastUpdate) > 0) {
            lastUpdate = modifVisites.getDateModification();
          }
          this.modificationVisite(modifVisites);
        }
      }

      if (lastUpdate != null) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lastUpdate.setTime(lastUpdate.getTime() + 1000);
        this.parametresRepository.set("LAST_UPDATE_PULLWORKER", dateFormat.format(lastUpdate));
        logger.info("Date des informations les plus récentes : " + dateFormat.format(lastUpdate));
      }

    } catch (RequestException e) { // Erreur retournée par l'API ou erreur interne
      logger.info("Récupération des modifications : erreur détectée");
      TypeErreur typeErreur =
          context
              .selectFrom(TYPE_ERREUR)
              .where(TYPE_ERREUR.CODE.equal(e.getCodeErreur()))
              .fetchOneInto(TypeErreur.class);
      this.erreurRepository.addError(typeErreur.getCode(), typeErreur.getMessageErreur(), null);

    } catch (APIConnectionException
        | APIAuthentException e) { // Erreur de connexion ou d'authentification à l'API
      String typeErreur = null;
      if (e instanceof APIConnectionException) {
        typeErreur = "0003";
        logger.info("Erreur de connexion à l'API détectée, arrêt récupération des modifications");
      } else if (e instanceof APIAuthentException) {
        typeErreur = "0200";
        logger.info(
            "Erreur d'authentification à l'API détectée, arrêt récupération des modifications");
      }
      String messageErreur =
          context
              .select(TYPE_ERREUR.MESSAGE_ERREUR)
              .from(TYPE_ERREUR)
              .where(TYPE_ERREUR.CODE.eq(typeErreur))
              .fetchOneInto(String.class);
      this.erreurRepository.addError(typeErreur, messageErreur, null);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    this.parametresRepository.set("FLAG_STATUS_PULLWORKER", "TERMINE");
  }

  /**
   * Détection de modification de visite
   *
   * @param modif La modification de visite la plus récente
   * @throws APIConnectionException
   * @throws RequestException
   * @throws JsonProcessingException
   * @throws APIAuthentException
   */
  private void modificationVisite(PeiDiffModel modif)
      throws APIConnectionException, RequestException, JsonProcessingException,
          APIAuthentException {
    String nomOrganisme = this.parametresRepository.get("NOM_ORGANISME");

    // Si les données n'ont pas été modifiées par l'organisme courant (ou un de ses utilisateurs, on
    // remonte l'info
    if ((("ETL".equals(modif.getAuteurModificationFlag())
                || "USER".equals(modif.getAuteurModificationFlag()))
            && !modif.getUtilisateurModificationOrganisme().equals(nomOrganisme))
        || ("API".equals(modif.getAuteurModificationFlag())
            && !modif.getOrganismeModification().equals(nomOrganisme))) {

      this.recuperationVisites(modif.getNumero());

      context
          .insertInto(PULL_MESSAGE)
          .set(
              PULL_MESSAGE.DATE,
              modif
                  .getDateModification()
                  .toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime())
          .set(PULL_MESSAGE.OPERATION, modif.getOperation())
          .set(PULL_MESSAGE.TYPE, modif.getType())
          .set(PULL_MESSAGE.HYDRANT, modif.getNumero())
          .execute();
    }
  }

  /**
   * Détection de modification des données patrimoniales
   *
   * @param modif La modification des caractéristiques techniques la plus récente
   * @throws APIConnectionException
   * @throws RequestException
   * @throws APIAuthentException
   * @throws JsonProcessingException
   */
  private void modificationCaracteristiques(PeiDiffModel modif)
      throws APIConnectionException, RequestException, APIAuthentException,
          JsonProcessingException {
    if ("DELETE".equals(modif.getOperation())) {
      System.out.println("[" + modif.getNumero() + "] Opération effectuée : DELETE");
      context
          .deleteFrom(PULL_HYDRANT)
          .where(PULL_HYDRANT.NUMERO.equalIgnoreCase(modif.getNumero()))
          .execute();
    } else {
      Long id =
          context
              .select(PULL_HYDRANT.ID)
              .from(PULL_HYDRANT)
              .where(PULL_HYDRANT.NUMERO.equalIgnoreCase(modif.getNumero()))
              .fetchOneInto(Long.class);

      String jsonPei = this.requestManager.sendGetRequest("/api/deci/pei/" + modif.getNumero());
      String jsonPeiCarac =
          this.requestManager.sendGetRequest(
              "/api/deci/pei/" + modif.getNumero() + "/caracteristiques");

      ObjectMapper mapper = new ObjectMapper();
      TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
      Map<String, Object> dataPei = mapper.readValue(jsonPei, typeRef);
      Map<String, Object> dataPeiCarac = mapper.readValue(jsonPeiCarac, typeRef);

      String auteurModification =
          ("USER".equals(modif.getAuteurModificationFlag()))
              ? modif.getUtilisateurModificationOrganisme()
                  + "_"
                  + modif.getUtilisateurModification()
              : modif.getOrganismeModification();

      // Le PEI n'existe pas en base => Création
      if (id == null) {
        logger.info("[" + modif.getNumero() + "] Opération effectuée : CREATE");
        Integer idPeiAjoute =
            context
                .insertInto(PULL_HYDRANT)
                .set(PULL_HYDRANT.NUMERO, modif.getNumero())
                .set(
                    PULL_HYDRANT.DATE_MODIFICATION,
                    modif
                        .getDateModification()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .set(PULL_HYDRANT.AUTEUR_MODIFICATION, auteurModification)
                .set(PULL_HYDRANT.DIAMETRE, JSONUtil.getString(dataPeiCarac, "diametre"))
                .set(PULL_HYDRANT.MARQUE, JSONUtil.getString(dataPeiCarac, "marque"))
                .set(PULL_HYDRANT.MODELE, JSONUtil.getString(dataPeiCarac, "modele"))
                .set(
                    PULL_HYDRANT.DIAMETRE_CANALISATION,
                    JSONUtil.getInteger(dataPeiCarac, "diametreCanalisation"))
                .set(
                    PULL_HYDRANT.ANNEE_FABRICATION,
                    JSONUtil.getString(dataPeiCarac, "anneeFabrication"))
                .set(PULL_HYDRANT.COMPLEMENT, JSONUtil.getString(dataPei, "complement"))
                .set(PULL_HYDRANT.DISPO_TERRESTRE, JSONUtil.getString(dataPei, "dispoTerrestre"))
                .set(PULL_HYDRANT.DISPO_HBE, JSONUtil.getString(dataPei, "dispoAerienne"))
                .set(PULL_HYDRANT.NUMERO_VOIE, JSONUtil.getInteger(dataPei, "numeroVoie"))
                .set(PULL_HYDRANT.SUFFIXE_VOIE, JSONUtil.getString(dataPei, "suffixeVoie"))
                .set(PULL_HYDRANT.NIVEAU, JSONUtil.getString(dataPei, "niveau"))
                .set(PULL_HYDRANT.VOIE, JSONUtil.getString(dataPei, "voie"))
                .set(PULL_HYDRANT.VOIE2, JSONUtil.getString(dataPei, "carrefour"))
                .set(PULL_HYDRANT.EN_FACE, JSONUtil.getBoolean(dataPei, "enFace"))
                .set(PULL_HYDRANT.DOMAINE, JSONUtil.getString(dataPei, "domaine"))
                .set(PULL_HYDRANT.COMMUNE, JSONUtil.getString(dataPei, "commune"))
                .set(PULL_HYDRANT.NATURE, JSONUtil.getString(dataPei, "nature"))
                .set(PULL_HYDRANT.NATURE_DECI, JSONUtil.getString(dataPei, "natureDeci"))
                .set(
                    PULL_HYDRANT.INDISPO_TEMPORAIRE,
                    JSONUtil.getBoolean(dataPei, "indispoTemporaire"))
                .returning(PULL_HYDRANT.ID)
                .fetchOne()
                .getValue(PULL_HYDRANT.ID);

        this.recuperationVisites(modif.getNumero());

      } else { // Le PEI existe en base => Modification
        logger.info("[" + modif.getNumero() + "] Opération effectuée : UPDATE");
        context
            .update(PULL_HYDRANT)
            .set(
                PULL_HYDRANT.DATE_MODIFICATION,
                modif
                    .getDateModification()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime())
            .set(PULL_HYDRANT.AUTEUR_MODIFICATION, auteurModification)
            .set(PULL_HYDRANT.DIAMETRE, JSONUtil.getString(dataPeiCarac, "diametre"))
            .set(PULL_HYDRANT.MARQUE, JSONUtil.getString(dataPeiCarac, "marque"))
            .set(PULL_HYDRANT.MODELE, JSONUtil.getString(dataPeiCarac, "modele"))
            .set(
                PULL_HYDRANT.DIAMETRE_CANALISATION,
                JSONUtil.getInteger(dataPeiCarac, "diametreCanalisation"))
            .set(
                PULL_HYDRANT.ANNEE_FABRICATION,
                JSONUtil.getString(dataPeiCarac, "anneeFabrication"))
            .set(PULL_HYDRANT.COMPLEMENT, JSONUtil.getString(dataPei, "complement"))
            .set(PULL_HYDRANT.DISPO_TERRESTRE, JSONUtil.getString(dataPei, "dispoTerrestre"))
            .set(PULL_HYDRANT.DISPO_HBE, JSONUtil.getString(dataPei, "dispoAerienne"))
            .set(PULL_HYDRANT.NUMERO_VOIE, JSONUtil.getInteger(dataPei, "numeroVoie"))
            .set(PULL_HYDRANT.SUFFIXE_VOIE, JSONUtil.getString(dataPei, "suffixeVoie"))
            .set(PULL_HYDRANT.NIVEAU, JSONUtil.getString(dataPei, "niveau"))
            .set(PULL_HYDRANT.VOIE, JSONUtil.getString(dataPei, "voie"))
            .set(PULL_HYDRANT.VOIE2, JSONUtil.getString(dataPei, "carrefour"))
            .set(PULL_HYDRANT.EN_FACE, JSONUtil.getBoolean(dataPei, "enFace"))
            .set(PULL_HYDRANT.DOMAINE, JSONUtil.getString(dataPei, "domaine"))
            .set(PULL_HYDRANT.COMMUNE, JSONUtil.getString(dataPei, "commune"))
            .set(PULL_HYDRANT.NATURE, JSONUtil.getString(dataPei, "nature"))
            .set(PULL_HYDRANT.NATURE_DECI, JSONUtil.getString(dataPei, "natureDeci"))
            .set(PULL_HYDRANT.INDISPO_TEMPORAIRE, JSONUtil.getBoolean(dataPei, "indispoTemporaire"))
            .where(PULL_HYDRANT.ID.eq(Math.toIntExact(id)))
            .execute();
      }
    }
  }

  /**
   * Récupération des visites d'un PEI La récupération supprime toutes les visites en mémoire pour
   * les remplacer par les données de visite actualisées
   *
   * @param numero Le numéro du PEI
   * @throws APIConnectionException
   * @throws RequestException
   * @throws APIAuthentException
   * @throws JsonProcessingException
   */
  private void recuperationVisites(String numero)
      throws APIConnectionException, RequestException, APIAuthentException,
          JsonProcessingException {

    // Récupération infos PEI
    PullHydrant pei =
        context
            .selectFrom(PULL_HYDRANT)
            .where(PULL_HYDRANT.NUMERO.equalIgnoreCase(numero))
            .fetchOneInto(PullHydrant.class);

    // Suppression visites existantes
    context
        .deleteFrom(PULL_HYDRANT_VISITE)
        .where(PULL_HYDRANT_VISITE.HYDRANT.eq(Long.valueOf(pei.getId())))
        .execute();

    String jsonVisites = this.requestManager.sendGetRequest("/api/deci/pei/" + numero + "/visites");

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<List<Map<String, Object>>> typeRef =
        new TypeReference<List<Map<String, Object>>>() {};
    TypeReference<Map<String, Object>> typeRefKeyValue =
        new TypeReference<Map<String, Object>>() {};
    TypeReference<List<String>> typeRefAnomalies = new TypeReference<List<String>>() {};

    List<Map<String, Object>> dataVisites = mapper.readValue(jsonVisites, typeRef);

    logger.info("[" + numero + "] Récupération de " + dataVisites.size() + " visite(s)");

    // Données spécifiques des visites
    for (Map<String, Object> visite : dataVisites) {
      String jsonVisiteSpecifique =
          this.requestManager.sendGetRequest(
              "/api/deci/pei/" + pei.getNumero() + "/visites/" + visite.get("identifiant"));
      Map<String, Object> dataVisiteSpecifique =
          mapper.readValue(jsonVisiteSpecifique, typeRefKeyValue);

      // Ajout de la visite
      Integer idVisite =
          context
              .insertInto(PULL_HYDRANT_VISITE)
              .set(PULL_HYDRANT_VISITE.HYDRANT, Long.valueOf(pei.getId()))
              .set(
                  PULL_HYDRANT_VISITE.DATE,
                  JSONUtil.getLocalDateTime(dataVisiteSpecifique, "date", "yyyy-MM-dd HH:mm"))
              .set(PULL_HYDRANT_VISITE.TYPE, JSONUtil.getString(dataVisiteSpecifique, "contexte"))
              .set(
                  PULL_HYDRANT_VISITE.CTRL_DEBIT_PRESSION,
                  JSONUtil.getBoolean(dataVisiteSpecifique, "ctrlDebitPression"))
              .set(PULL_HYDRANT_VISITE.AGENT1, JSONUtil.getString(dataVisiteSpecifique, "agent1"))
              .set(PULL_HYDRANT_VISITE.AGENT2, JSONUtil.getString(dataVisiteSpecifique, "agent2"))
              .set(PULL_HYDRANT_VISITE.DEBIT, JSONUtil.getInteger(dataVisiteSpecifique, "debit"))
              .set(
                  PULL_HYDRANT_VISITE.DEBIT_MAX,
                  JSONUtil.getInteger(dataVisiteSpecifique, "debitMax"))
              .set(
                  PULL_HYDRANT_VISITE.PRESSION,
                  JSONUtil.getDouble(dataVisiteSpecifique, "pression"))
              .set(
                  PULL_HYDRANT_VISITE.PRESSION_DYN,
                  JSONUtil.getDouble(dataVisiteSpecifique, "pressionDyn"))
              .set(
                  PULL_HYDRANT_VISITE.PRESSION_DYN_DEB,
                  JSONUtil.getDouble(dataVisiteSpecifique, "pressionDynDeb"))
              .set(
                  PULL_HYDRANT_VISITE.OBSERVATIONS,
                  JSONUtil.getString(dataVisiteSpecifique, "observations"))
              .returning(PULL_HYDRANT_VISITE.ID)
              .fetchOne()
              .getValue(PULL_HYDRANT_VISITE.ID);

      System.out.println("Visite ajoutée id : " + idVisite);
      String jsonAnomalies = JSONUtil.getString(dataVisiteSpecifique, "anomaliesConstatees");
      String[] anomalies =
          jsonAnomalies.replaceAll("\\[", "").replaceAll("]", "").split("\\s*,\\s*");

      // Ajout des anomalies de la visite
      for (String s : anomalies) {
        context
            .insertInto(PULL_HYDRANT_ANOMALIES)
            .set(PULL_HYDRANT_ANOMALIES.VISITE, Long.valueOf(idVisite))
            .set(PULL_HYDRANT_ANOMALIES.CODE, s)
            .execute();
      }
    }
  }

  /** Si le Pullworker a crash à sa dernière itération, on notifie l'erreur */
  public void handlePushWorkerFailure() {
    logger.warn("Pullworker failure detected, handling messages causing the error");
    // Message d'erreur global
    TypeErreur te =
        context
            .selectFrom(TYPE_ERREUR)
            .where(TYPE_ERREUR.CODE.eq("I0003"))
            .fetchOneInto(TypeErreur.class);
    this.erreurRepository.addError(te.getCode(), te.getMessageErreur(), null);

    this.parametresRepository.set("FLAG_STATUS_PULLWORKER", "TERMINE");
  }
}
