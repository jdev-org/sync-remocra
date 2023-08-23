package fr.eaudeparis.syncremocra.mail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.immutables.value.Value;

@Value.Enclosing
public class SendMail {
  @Inject Session session;
  @Inject MailSettings settings;

  public void send(Message message) throws MessagingException {

    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(settings.sender());
    String[] arrayRecipient = message.recipient().split(",");
    for (String recipient : arrayRecipient) {
      msg.addRecipients(javax.mail.Message.RecipientType.TO, recipient.trim());
    }
    msg.setSubject(message.subject(), StandardCharsets.UTF_8.name());
    if (message.attachments().isEmpty()) {
      msg.setText(
          message.body(), StandardCharsets.UTF_8.name(), message.isHtml() ? "html" : "plain");
    } else {
      MimeBodyPart body = new MimeBodyPart();
      body.setText(
          message.body(), StandardCharsets.UTF_8.name(), message.isHtml() ? "html" : "plain");
      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(body);
      for (Attachment attachment : message.attachments()) {
        MimeBodyPart attachementBodyPart = new MimeBodyPart();
        attachementBodyPart.setDataHandler(
            new DataHandler(
                new FileDataSource(attachment.content()) {
                  @Override
                  public String getContentType() {
                    return attachment.contentType();
                  }
                }));
        attachementBodyPart.setFileName(attachment.filename());
        attachementBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
        multipart.addBodyPart(attachementBodyPart);
      }
      msg.setContent(multipart);
    }
    Transport.send(msg);
  }

  @Value.Immutable
  public interface Message {
    String subject();

    String body();

    String recipient();

    @Value.Default
    default boolean isHtml() {
      return false;
    }

    List<Attachment> attachments();
  }

  @Value.Immutable
  public interface Attachment {
    File content();

    String contentType();

    String filename();
  }
}
