package fr.eaudeparis.syncremocra.job;

import static fr.eaudeparis.syncremocra.db.model.tables.Message.MESSAGE;
import static fr.eaudeparis.syncremocra.db.model.tables.TracabilitePei.TRACABILITE_PEI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.Message;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.TracabilitePei;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.VueErreurToNotify;
import fr.eaudeparis.syncremocra.mail.MailUtil;
import fr.eaudeparis.syncremocra.notification.NotificationSettings;
import fr.eaudeparis.syncremocra.repository.erreur.ErreurRepository;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import fr.eaudeparis.syncremocra.util.RequestManager;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationJob implements Job {

  static final Logger logger = LoggerFactory.getLogger(NotificationJob.class);

  private final MailUtil mailer;

  private final ErreurRepository erreurRepository;

  private final DSLContext context;

  @Inject NotificationSettings settings;

  @Inject RequestManager requestManager;

  @Inject
  public NotificationJob(MailUtil mailer, ErreurRepository erreurRepository, DSLContext context) {
    this.mailer = mailer;
    this.erreurRepository = erreurRepository;
    this.context = context;
  }

  public int run() {
    boolean result = true;

    try {
      for (Map.Entry<String, String> entry : this.getListContextKeyEmail().entrySet()) {
        if (result) {
          result =
              this.sendEmailFromContext(
                  entry.getKey(), settings.objetMail().replaceAll("%20", " "), entry.getValue());
        }
      }

      // En cas de maj de la disponibilité, on vérifie si les infos sont correctes dans les deux
      // bases
      this.checkDisponibiliteCoherence();
      return 0;
    } catch (Exception e) {
      logger.error("Erreur lors de l'envoi du mail", e);
      return -1;
    }
  }

  protected Map<String, String> getListContextKeyEmail() {
    Map<String, String> mapContext = new Hashtable<>();
    mapContext.put("EDP_SYSTEME", settings.edpSystemeMail().replaceAll("%20", " "));
    mapContext.put("EDP_METIER", settings.edpMetierMail().replaceAll("%20", " "));
    mapContext.put("REMOCRA_METIER", settings.remocraMetierMail().replaceAll("%20", " "));
    mapContext.put("REMOCRA_SYSTEME", settings.remocraSystemeMail().replaceAll("%20", " "));
    mapContext.put("INCOHERENCE", settings.remocraIncoherenceMail().replaceAll("%20", " "));
    return mapContext;
  }

  protected boolean sendEmailFromContext(String context, String subject, String emails) {
    Optional<VueErreurToNotify> vueErreurToNotify = erreurRepository.getVueErreurToNotify(context);
    if (vueErreurToNotify.isPresent()) {
      String message =
          this.getHtmlFromXml(
              this.getXlsContentNotification(),
              vueErreurToNotify.get().getXmlNotification().toString());
      if (this.send(emails, subject, message)) {
        List<Integer> idErreursNotified =
            Arrays.stream(vueErreurToNotify.get().getErreursIds().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        logger.info(
            vueErreurToNotify.get().getErreursNombre()
                + " erreurs à notifier pour le contexte "
                + context
                + ". Identifiant des erreurs: "
                + idErreursNotified.toString());
        erreurRepository.updateStatutNotifieErreur(idErreursNotified, true);
      }
    }
    return true;
  }

  protected boolean send(String toEmail, String subject, String msg) {
    try {

      for (String email : toEmail.split(" ")) {
        mailer.sendMail(msg, subject, email, true);
        logger.info("Mail envoyé à " + email);
      }
      return true;
    } catch (AddressException e) {
      logger.error(
          "Erreur lors de l'envoi du mail, le format de l'adresse mail ("
              + toEmail
              + ") n'est pas correcte.",
          e);
      return false;
    } catch (Exception e) {
      logger.error("Erreur lors de l'envoi du mail", e);
      return false;
    }
  }

  protected String getHtmlFromXml(String xsl, String xml) {
    try {
      StringWriter writer = new StringWriter();
      StreamResult streamResult = new StreamResult(writer);
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsl)));
      transformer.transform(new StreamSource(new StringReader(xml)), streamResult);
      return writer.toString();
    } catch (TransformerConfigurationException e) {
      logger.error("Erreur lors de la lecture du contenu xsl ", e);
      return null;
    } catch (TransformerException e) {
      logger.error("Erreur lors de la transformation du xml en html ", e);
      return null;
    } catch (Exception e) {
      logger.error("Erreur ", e);
      return null;
    }
  }

  protected String getXlsContentNotification() {
    try {
      InputStream is = getClass().getClassLoader().getResourceAsStream("edp_notification.xsl");
      return readFromInputStream(is);
    } catch (Exception e) {
      logger.error("Erreur lors de la lecture du fichier de ressource edp_notification.xsl", e);
      return null;
    }
  }

  protected String readFromInputStream(InputStream inputStream) throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }

  private void checkDisponibiliteCoherence() {
    ObjectMapper mapper = new ObjectMapper();

    List<Message> messages =
        context
            .selectFrom(MESSAGE)
            .where(
                MESSAGE
                    .STATUT
                    .equalIgnoreCase("A VERIFIER")
                    .and(MESSAGE.DATE_DEBUT_VERIF.lessThan(LocalDateTime.now())))
            .fetchInto(Message.class);

    for (Message m : messages) {
      logger.info("[Disponibilité] Vérification message " + m.getId() + "...");
      TracabilitePei traca =
          context
              .selectFrom(TRACABILITE_PEI)
              .where(TRACABILITE_PEI.ID.eq(m.getIdTracaPei().intValue()))
              .fetchOneInto(TracabilitePei.class);

      try {
        String indispoEnCours =
            this.requestManager.sendGetRequest("/api/deci/pei/" + traca.getReference());

        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> dataHydrant = mapper.readValue(indispoEnCours, typeRef);
        String dispoRemocra = JSONUtil.getString(dataHydrant, "dispoTerrestre");
        String dispoEdp = traca.getEtat();
        // Pas d'incohérence détectée
        if (("DISPO".equalsIgnoreCase(dispoRemocra) && "Disponible".equalsIgnoreCase(dispoEdp))
            || ("INDISPO".equalsIgnoreCase(dispoRemocra)
                && "Indisponible".equalsIgnoreCase(dispoEdp))) {
          context
              .update(MESSAGE)
              .set(MESSAGE.STATUT, "TRAITE")
              .where(MESSAGE.ID.eq(m.getId()))
              .execute();
        } else {
          String objet = "Incohérence de disponibilité - Echanges EDP/Remocra";

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
          String date = traca.getDateMajE().format(formatter);

          String msg =
              "<b>"
                  + date
                  + "</b> - Le PEI "
                  + traca.getReference()
                  + " a été signalé <b>"
                  + dispoEdp
                  + "</b> par Eau de Paris mais présente un état <b>"
                  + ("DISPO".equalsIgnoreCase(dispoRemocra) ? "Disponible" : "Indisponible")
                  + "</b> dans Remocra malgré la synchronisation";

          this.send(this.getListContextKeyEmail().get("INCOHERENCE"), objet, msg);
          logger.info(
              traca.getReference()
                  + " en incohérence Remocra : "
                  + dispoRemocra
                  + ", EDP : "
                  + dispoEdp
                  + "; envoi d'un mail d'alerte");
          context
              .update(MESSAGE)
              .set(MESSAGE.STATUT, "TRAITE")
              .where(MESSAGE.ID.eq(m.getId()))
              .execute();
        }

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
