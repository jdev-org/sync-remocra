package fr.eaudeparis.syncremocra.job;

import static fr.eaudeparis.syncremocra.db.model.tables.Message.MESSAGE;
import static fr.eaudeparis.syncremocra.db.model.tables.Parametres.PARAMETRES;
import static fr.eaudeparis.syncremocra.db.model.tables.TypeErreur.TYPE_ERREUR;

import fr.eaudeparis.syncremocra.db.model.tables.pojos.TypeErreur;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeDataCaptureJob implements Job {

  private final DSLContext context;

  static final Logger logger = LoggerFactory.getLogger(NotificationJob.class);

  @Inject ErreurRepository erreurRepository;

  @Inject
  ChangeDataCaptureJob(DSLContext context) {
    this.context = context;
  }

  @Inject ParametresRepository parametresRepository;

  @Override
  public int run() {
    if (this.parametresRepository
        .get("FLAG_STATUS_CHANGEDATACAPTURE")
        .equalsIgnoreCase("EN COURS")) {
      this.handleChangeDataCaptureFailure();

    } else {
      this.changeDataCapture();
    }
    return 0;
  }

  private void changeDataCapture() {
    this.parametresRepository.set("FLAG_STATUS_CHANGEDATACAPTURE", "EN COURS");
    try {
      String dateDerniereRemontee =
          this.context
              .select(PARAMETRES.VALEUR)
              .from(PARAMETRES)
              .where(PARAMETRES.CODE.eq("DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE"))
              .fetchOneInto(String.class);
      int nbMessagesAvantRemontee = this.context.fetchCount(MESSAGE);
      logger.info("Récupération des changements depuis " + dateDerniereRemontee);

      this.context.selectFrom("edp.changeDataCapture()").fetchOneInto(Integer.class);
      int nbMessagesApresRemontee = this.context.fetchCount(MESSAGE);
      dateDerniereRemontee =
          this.context
              .select(PARAMETRES.VALEUR)
              .from(PARAMETRES)
              .where(PARAMETRES.CODE.eq("DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE"))
              .fetchOneInto(String.class);
      logger.info(
          nbMessagesApresRemontee
              - nbMessagesAvantRemontee
              + " message(s) remonté(s) depuis la base EDP");
      logger.info(
          "Date de mise à jour la plus récente ayant été remontée : " + dateDerniereRemontee);
      this.parametresRepository.set("FLAG_STATUS_CHANGEDATACAPTURE", "TERMINE");
      this.parametresRepository.set("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE", null);
    } catch (DataAccessException e) {
      if (e.getCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getCause();
        logger.warn("CDC Failure: " + sqlEx);

        if ("HV00N".equalsIgnoreCase(sqlEx.getSQLState())) { // PB accès base Oracle
          this.erreurRepository.addError(
              "0001", "Le ChangeDataCapture n'a pas pu se connecter à la base Oracle", null);
          this.parametresRepository.set("FLAG_STATUS_CHANGEDATACAPTURE", "TERMINE");
          this.parametresRepository.set("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE", null);
        } else if ("08001".equalsIgnoreCase(sqlEx.getSQLState())) { // PB accès base postgresql
          // TODO : gérer le cas où on ne peut se connecter à la base POSTGRES (si géré dans ce cas)
          this.parametresRepository.set("FLAG_STATUS_CHANGEDATACAPTURE", "TERMINE");
          this.parametresRepository.set("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE", null);
        }
      }
    }
  }

  /** Gestion failure CDC lors de la dernière itération */
  private void handleChangeDataCaptureFailure() {
    logger.warn("CDC failure detected");

    /**
     * Si DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE null, on ajoute une erreur Cette variable revient
     * à null chaque fois que le CDC se termine correctement, on évite ainsi le spam de
     * notifications pendant toute la durée du problème TODO : utiliser ce code avec une
     * notification pour avertir du retour à la normale
     */
    // if(this.parametresRepository.get("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE") == null) {
    TypeErreur te =
        context
            .selectFrom(TYPE_ERREUR)
            .where(TYPE_ERREUR.CODE.eq("I0002"))
            .fetchOneInto(TypeErreur.class);

    this.erreurRepository.addError(te.getCode(), te.getMessageErreur(), null);
    // }

    String derniereRemontee =
        this.parametresRepository.get("DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE");
    if (this.parametresRepository.get("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE") == null) {
      this.parametresRepository.set("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE", derniereRemontee);
    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    this.parametresRepository.set(
        "DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE", formatter.format(new Date()));

    this.parametresRepository.set("FLAG_STATUS_CHANGEDATACAPTURE", "TERMINE");
  }
}
