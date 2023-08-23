package fr.eaudeparis.syncremocra;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import fr.eaudeparis.syncremocra.api.ApiModule;
import fr.eaudeparis.syncremocra.db.DatabaseModule;
import fr.eaudeparis.syncremocra.http.HttpClientModule;
import fr.eaudeparis.syncremocra.job.ChangeDataCaptureJob;
import fr.eaudeparis.syncremocra.job.CompositeJob;
import fr.eaudeparis.syncremocra.job.Job;
import fr.eaudeparis.syncremocra.job.NotificationJob;
import fr.eaudeparis.syncremocra.job.PullWorkerJob;
import fr.eaudeparis.syncremocra.job.PushWorkerJob;
import fr.eaudeparis.syncremocra.job.test.DbTestJob;
import fr.eaudeparis.syncremocra.job.test.HttpTestJob;
import fr.eaudeparis.syncremocra.job.test.MailTestJob;
import fr.eaudeparis.syncremocra.json.JacksonModule;
import fr.eaudeparis.syncremocra.jvm.JvmInitializer;
import fr.eaudeparis.syncremocra.jvm.JvmModule;
import fr.eaudeparis.syncremocra.mail.MailModule;
import fr.eaudeparis.syncremocra.notification.NotificationModule;
import fr.eaudeparis.syncremocra.tools.SettingsLoader;
import fr.eaudeparis.syncremocra.tools.VersionProvider;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(
    name = "syncremocra",
    header = "Eau de Paris - synchronisation avec Remocra",
    description = "Configuration de la journalisation : -Dlog4j.configurationFile=<file>",
    subcommands = HelpCommand.class,
    mixinStandardHelpOptions = true,
    versionProvider = VersionProvider.class)
public class App implements Runnable {

  @Inject JvmInitializer jvmInitializer;

  public static void main(String[] args) throws Throwable {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    System.exit(new CommandLine(new App()).execute(args));
  }

  @Option(
      names = {"-c", "--conf"},
      description = "Fichier de configuration")
  private Path configurationPath;

  /**
   * Initialise l'application (paramètres).
   *
   * @return
   */
  private Config init() {
    return SettingsLoader.load(configurationPath);
  }

  /**
   * Initialise l'application (configuration) et injecte les modules.
   *
   * @return
   */
  private Injector initWithAllModules() {
    Config config = init();
    final Injector injector = Guice.createInjector(getModules(config));
    injector.injectMembers(this);
    jvmInitializer.initialize();
    return injector;
  }

  /**
   * Initialise l'application (configuration) et injecte les modules. <br>
   * Réalise également la migration.<br>
   * TODO voir si on conserve la migration à terme.
   *
   * @return
   */
  private Injector initWithAllModules(boolean migrate) {
    Injector injector = initWithAllModules();
    if (migrate) {
      injector.getInstance(Flyway.class).migrate();
    }
    return injector;
  }

  /**
   * Créer la liste des modules de l'application.
   *
   * @param config
   * @return
   */
  private List<AbstractModule> getModules(final Config config) {
    List<AbstractModule> res = new ArrayList<>();

    res.add(
        DatabaseModule.create(config.getConfig("edp.database"), config.getConfig("edp.flyway")));
    res.add(JvmModule.create(config.getConfig("edp.jvm")));
    res.add(MailModule.create(config.getConfig("edp.mail")));
    res.add(ApiModule.create(config.getConfig("edp.api")));
    res.add(NotificationModule.create(config.getConfig("edp.notification")));
    res.add(new HttpClientModule());
    res.add(new JacksonModule());

    return res;
  }

  /* -----------------
   Commande par défaut
  -------------------- */
  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }

  /* -----------------
   Commandes de gestion de la base de données
  -------------------- */
  private Flyway flywayCommand() {
    Config config = init();
    return Guice.createInjector(
            DatabaseModule.create(config.getConfig("edp.database"), config.getConfig("edp.flyway")))
        .getInstance(Flyway.class);
  }

  @Command(name = "db-migrate", description = "Met à jour le schéma de la base de données")
  public void migrateDb() {
    flywayCommand().migrate();
  }

  @Command(
      name = "db-validate",
      description = "Vérifie que le schéma de la base de données est à jour")
  public void validateDb() {
    flywayCommand().validate();
  }

  @Command(
      name = "db-repair",
      hidden = true,
      description = "Répare la table de suivi de la version de schéma de la base de données")
  public void repairDb() {
    flywayCommand().repair();
  }

  /* -----------------
   Jobs
  -------------------- */
  @Command(name = "changedatacapture", description = "Récupère les données depuis EDP")
  public int changeDataCapture(
      @Option(
              names = "--migrate",
              description = "Activer la migration  (défaut : ${DEFAULT-VALUE})",
              defaultValue = "true")
          boolean migrate) {
    Injector injector = initWithAllModules(migrate);
    return new CompositeJob(new Job[] {injector.getInstance(ChangeDataCaptureJob.class)}).run();
  }

  @Command(name = "pushworker", description = "Envoie les données des PEI à Remocra")
  public int pushWorker(
      @Option(
              names = "--migrate",
              description = "Activer la migration  (défaut : ${DEFAULT-VALUE})",
              defaultValue = "true")
          boolean migrate) {
    Injector injector = initWithAllModules(migrate);
    return new CompositeJob(new Job[] {injector.getInstance(PushWorkerJob.class)}).run();
  }

  @Command(name = "notification", description = "Envoie les emails liées aux erreurs")
  public int notification(
      @Option(
              names = "--migrate",
              description = "Activer la migration  (défaut : ${DEFAULT-VALUE})",
              defaultValue = "true")
          boolean migrate) {
    Injector injector = initWithAllModules(migrate);
    return new CompositeJob(new Job[] {injector.getInstance(NotificationJob.class)}).run();
  }

  @Command(name = "pullworker", description = "Récupère les données depuis Remocra")
  public int pullworker(
      @Option(
              names = "--migrate",
              description = "Activer la migration  (défaut : ${DEFAULT-VALUE})",
              defaultValue = "true")
          boolean migrate) {
    Injector injector = initWithAllModules(migrate);
    return new CompositeJob(new Job[] {injector.getInstance(PullWorkerJob.class)}).run();
  }

  /* -----------------
   Autres commandes
  -------------------- */
  @Command(name = "job-test-all", description = "Exécute les jobs de tests")
  public int testjobs(
      @Option(
              names = "--migrate",
              description = "Activer la migration  (défaut : ${DEFAULT-VALUE})",
              defaultValue = "true")
          boolean migrate) {
    Injector injector = initWithAllModules(migrate);
    return new CompositeJob(
            new Job[] {
              injector.getInstance(DbTestJob.class),
              injector.getInstance(HttpTestJob.class),
              injector.getInstance(MailTestJob.class)
            })
        .run();
  }
}
