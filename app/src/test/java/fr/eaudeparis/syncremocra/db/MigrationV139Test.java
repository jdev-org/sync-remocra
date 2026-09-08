package fr.eaudeparis.syncremocra.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/** Verifies the required EDP temporary-unavailability mappings are shipped in migration V1.39. */
public class MigrationV139Test {

  @Test
  public void shouldMapSsqAndProjetMotifsToBSPPAPSE() throws IOException {
    try (InputStream migration =
        getClass()
            .getClassLoader()
            .getResourceAsStream("db/migration/V1.39__ajout_motifs_indispo_ssq_projet.sql")) {
      assertNotNull(migration);

      String sql = new String(migration.readAllBytes(), StandardCharsets.UTF_8);
      assertTrue(sql.contains("('BSPP_APSE', 'FERME DEMANDE SSQ')"));
      assertTrue(sql.contains("('BSPP_APSE', 'APP PROJET')"));
    }
  }
}
