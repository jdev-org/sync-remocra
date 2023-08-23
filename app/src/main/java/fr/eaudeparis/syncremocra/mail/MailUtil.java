package fr.eaudeparis.syncremocra.mail;

import javax.inject.Inject;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Classe utilitaire pour envoyer un mail */
public class MailUtil {

  @Inject SendMail sendMail;

  private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

  /**
   * Envoi d'un mail
   *
   * @param body Corps du mail
   * @param subject Object du mail
   * @param recipient Destinataire du mail
   * @throws MessagingException
   */
  public void sendMail(String body, String subject, String recipient, boolean html)
      throws MessagingException {
    ImmutableSendMail.Message.Builder message = ImmutableSendMail.Message.builder();
    message.body(body);
    message.subject(subject);
    message.recipient(recipient);
    message.isHtml(html);

    try {
      sendMail.send(message.build());
    } catch (MessagingException e) {
      logger.error("There was an error during the email sending : ", e);
      throw e;
    }
  }
}
