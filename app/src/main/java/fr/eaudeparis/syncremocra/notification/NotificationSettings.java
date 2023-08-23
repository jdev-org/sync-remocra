package fr.eaudeparis.syncremocra.notification;

import org.immutables.value.Value;

@Value.Immutable
public interface NotificationSettings {
  String objetMail();

  String edpMetierMail();

  String edpSystemeMail();

  String remocraMetierMail();

  String remocraSystemeMail();

  String remocraIncoherenceMail();
}
