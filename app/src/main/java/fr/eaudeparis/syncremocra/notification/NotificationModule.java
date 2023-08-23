package fr.eaudeparis.syncremocra.notification;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import org.immutables.value.Value;

@Value.Enclosing
public class NotificationModule extends AbstractModule {

  final NotificationSettings notificationSettings;

  public static NotificationModule create(Config config) {
    ImmutableNotificationSettings.Builder builder = ImmutableNotificationSettings.builder();
    builder
        .objetMail(config.getString("objet_mail"))
        .edpMetierMail(config.getString("edp_metier_mail"))
        .edpSystemeMail(config.getString("edp_systeme_mail"))
        .remocraMetierMail(config.getString("remocra_metier_mail"))
        .remocraSystemeMail(config.getString("remocra_systeme_mail"))
        .remocraIncoherenceMail(config.getString("incoherence_mail"));
    return new NotificationModule(builder.build());
  }

  public NotificationModule(ImmutableNotificationSettings settings) {
    this.notificationSettings = settings;
  }

  @Override
  protected void configure() {
    bind(NotificationSettings.class).toInstance(notificationSettings);
  }
}
