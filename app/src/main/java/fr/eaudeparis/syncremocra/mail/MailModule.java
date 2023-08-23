package fr.eaudeparis.syncremocra.mail;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import java.util.Properties;
import javax.inject.Singleton;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Value.Enclosing
public class MailModule extends AbstractModule {
  private static Logger logger = LoggerFactory.getLogger(MailModule.class);

  final MailSettings mailSettings;

  public static MailModule create(Config config) {
    ImmutableMailSettings.Builder builder = ImmutableMailSettings.builder();
    if (config.hasPath("username")) {
      builder.username(config.getString("username"));
    }
    if (config.hasPath("password")) {
      builder.password(config.getString("password"));
    }
    InternetAddress from;
    if (config.hasPath("from")) {
      try {
        from = new InternetAddress(config.getString("from"));
      } catch (AddressException e) {
        from = null;
      }
    } else {
      logger.warn(
          "No valid email sender, current user used by default (cf. javax.mail.internet"
              + ".InternetAddress.getLocalAddress)");
      from = InternetAddress.getLocalAddress(null);
    }
    if (from == null) {
      throw new RuntimeException("Unabled to find emails sender.");
    }
    builder
        .sender(from)
        .protocol(config.getString("protocol"))
        .starttls(config.getBoolean("starttls.enable"))
        .host(config.getString("host"))
        .port(config.getInt("port"));
    return new MailModule(builder.build());
  }

  public MailModule(ImmutableMailSettings settings) {
    this.mailSettings = settings;
  }

  @Override
  protected void configure() {
    bind(MailSettings.class).toInstance(mailSettings);
  }

  @Provides
  @Singleton
  Session provideSession() {
    final String protocol = mailSettings.protocol();
    Properties props = new Properties();
    props.put("mail." + protocol + ".starttls.enable", mailSettings.starttls() ? "true" : "false");
    props.put("mail." + protocol + ".host", mailSettings.host());
    props.put("mail." + protocol + ".port", Integer.toString(mailSettings.port()));
    Authenticator authenticator;
    if (mailSettings.username().isPresent() && mailSettings.password().isPresent()) {
      props.put("mail." + protocol + ".auth", "true");
      authenticator =
          new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(
                  mailSettings.username().get(), mailSettings.password().get());
            }
          };
    } else {
      authenticator = null;
    }
    Session session = Session.getInstance(props, authenticator);
    session.setProtocolForAddress("rfc822", protocol);
    return session;
  }
}
