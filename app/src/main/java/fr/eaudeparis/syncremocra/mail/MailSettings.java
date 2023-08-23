package fr.eaudeparis.syncremocra.mail;

import java.util.Optional;
import javax.mail.internet.InternetAddress;
import org.immutables.value.Value;

@Value.Immutable
public interface MailSettings {
  InternetAddress sender();

  Optional<String> username();

  Optional<String> password();

  String protocol();

  boolean starttls();

  String host();

  int port();
}
