package fr.eaudeparis.syncremocra.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/** Verifies the CDC migration for isolated changes to an AE end date. */
public class MigrationV140Test {

  @Test
  public void shouldDetectAndTraceDateMajAe() throws IOException {
    try (InputStream migration =
        getClass().getClassLoader().getResourceAsStream("db/migration/V1.40__date_maj_ae.sql")) {
      assertNotNull(migration);

      String sql = new String(migration.readAllBytes(), StandardCharsets.UTF_8);
      assertTrue(sql.contains("ADD COLUMN date_maj_ae"));
      assertTrue(sql.contains("OR date_maj_ae > derniereRemontee"));
      assertTrue(sql.contains("'date_maj_ae'"));
      assertTrue(sql.contains("'INDISPO_TEMPORAIRE'"));
    }
  }
}
