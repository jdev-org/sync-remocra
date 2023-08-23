package fr.eaudeparis.syncremocra.job.test;

import fr.eaudeparis.syncremocra.job.Job;
import fr.eaudeparis.syncremocra.mail.MailUtil;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailTestJob implements Job {

  private static Logger logger = LoggerFactory.getLogger(MailTestJob.class);

  private final MailUtil mailer;

  @Inject
  public MailTestJob(MailUtil mailer) {
    this.mailer = mailer;
  }

  public int run() {
    logger.info("Avant envoi courriel.");
    try {
      int messagesInError = (int) (Math.random() * 100 + 1);
      logger.info("Courriel : random=" + messagesInError);
      mailer.sendMail(
          "Bonjour,<br/><br/>Nous avons constaté "
              + messagesInError
              + " erreurs.<br/><br/>Mail envoyé automatiquement"
              + " par le pusher",
          "[EDP] Synchronisation Remocra",
          "edp+destinataire-erreurs@atolcd.com",
          true);
      logger.info("Courriel envoyé. Erreurs aléatoires : " + messagesInError);
      return 0;
    } catch (Exception e) {
      logger.error("Erreur lors de l'envoi du mail", e);
      return -1;
    } finally {
      logger.info("Après tentative d'envoi du mail.");
    }
  }
}
