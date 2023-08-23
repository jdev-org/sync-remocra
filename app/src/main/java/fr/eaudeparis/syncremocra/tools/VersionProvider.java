package fr.eaudeparis.syncremocra.tools;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.IVersionProvider;

public class VersionProvider implements IVersionProvider {
  private static final Logger logger = LoggerFactory.getLogger(VersionProvider.class);

  public enum Mode {
    DEV,
    PACKAGED,
    UNKNOWN
  };

  public Mode getMode() {
    URL classUrl =
        VersionProvider.class
            .getClassLoader()
            .getResource(VersionProvider.class.getName().replace('.', '/') + ".class");
    if (classUrl == null) {
      logger.error("Classe non trouvée, mode : " + Mode.UNKNOWN);
      return Mode.UNKNOWN;
    }
    return classUrl.toString().startsWith("jar:") ? Mode.PACKAGED : Mode.DEV;
  }

  public String[] getVersion() throws Exception {
    if (getMode() != Mode.PACKAGED) {
      return new String[] {"dev"};
    }
    String version = null;
    URL url =
        VersionProvider.class
            .getClassLoader()
            .getResource("META-INF/maven/fr.eaudeparis/syncremocra-app/pom.properties");
    if (url == null) {
      logger.error("Absence pom.properties");
    } else {
      Properties properties = new Properties();
      try {
        properties.load(url.openStream());
        version = properties.getProperty("version");
        if (version == null) {
          logger.error("Absence version dans pom.properties");
        }
      } catch (IOException e) {
        logger.error("Chargement du pom.properties", e);
      }
    }
    return new String[] {version == null ? "inconnue" : version};
  }
}
