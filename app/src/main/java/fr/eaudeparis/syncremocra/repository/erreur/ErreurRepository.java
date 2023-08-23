package fr.eaudeparis.syncremocra.repository.erreur;

import static fr.eaudeparis.syncremocra.db.model.tables.Erreur.ERREUR;
import static fr.eaudeparis.syncremocra.db.model.tables.TypeErreur.TYPE_ERREUR;
import static fr.eaudeparis.syncremocra.db.model.tables.VueErreurToNotify.VUE_ERREUR_TO_NOTIFY;

import fr.eaudeparis.syncremocra.db.model.tables.pojos.VueErreurToNotify;
import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import fr.eaudeparis.syncremocra.repository.typeErreur.model.TypeErreurModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.XML;

public class ErreurRepository {

  private final DSLContext context;

  @Inject
  ErreurRepository(DSLContext context) {
    this.context = context;
  }

  @Inject ParametresRepository parametresRepository;

  /**
   * Ajoute une erreur à notifier
   *
   * @param codeErreur Code de l'erreur tel qu'indiqué dans la table edp.type_erreur
   * @param message Message descriptif
   * @param idMessage Identifiant du message auquel rattacher cette erreur
   */
  public void addError(String codeErreur, String message, Long idMessage) {
    TypeErreurModel typeErreur =
        context
            .selectFrom(TYPE_ERREUR)
            .where(TYPE_ERREUR.CODE.equal(codeErreur))
            .fetchOneInto(TypeErreurModel.class);

    /* Si c'est une erreur de connexion (base Oracle/API), on ne rajoute pas l'erreur si il en existe déjà
      une identique qui n'a pas encore été notifiée
    */
    if (typeErreur.getCode().startsWith("0")) {
      Integer nb =
          context
              .selectCount()
              .from(ERREUR)
              .where(ERREUR.TYPE_ERREUR.eq(typeErreur.getId()).and(ERREUR.NOTIFIE.isFalse()))
              .fetchOneInto(Integer.class);
      if (nb > 0) {
        return;
      }
    }

    context
        .insertInto(ERREUR, ERREUR.DESCRIPTION, ERREUR.TYPE_ERREUR, ERREUR.MESSAGE)
        .values(message, typeErreur.getId(), idMessage)
        .execute();
  }

  public Optional<VueErreurToNotify> getVueErreurToNotify(String contexte) {
    Optional<VueErreurToNotify> vue =
        context
            .select(VUE_ERREUR_TO_NOTIFY.fields())
            .from(VUE_ERREUR_TO_NOTIFY)
            .where(VUE_ERREUR_TO_NOTIFY.CONTEXTE.equal(contexte))
            .fetchOptionalInto(VueErreurToNotify.class);

    // Remplacement des variables
    if (vue.isPresent()) {
      String xmlData = vue.get().getXmlNotification().toString();
      try {
        if (this.parametresRepository.get("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE") != null) {
          Date d =
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                  .parse(this.parametresRepository.get("DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE"));
          SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
          xmlData =
              xmlData.replaceAll(
                  "\\[DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE]", formatter.format(d));
        }
        if (this.parametresRepository.get("LAST_UPDATE_PULLWORKER") != null) {
          Date d =
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                  .parse(this.parametresRepository.get("LAST_UPDATE_PULLWORKER"));
          SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
          xmlData = xmlData.replaceAll("\\[LAST_UPDATE_PULLWORKER]", formatter.format(d));
        }
        vue.get().setXmlNotification(XML.valueOf(xmlData));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return vue;
  }

  public int updateStatutNotifieErreur(List<Integer> idErreurs, Boolean notifie) {
    return context
        .update(ERREUR)
        .set(ERREUR.NOTIFIE, notifie)
        .where(ERREUR.ID.in(idErreurs))
        .execute();
  }
}
