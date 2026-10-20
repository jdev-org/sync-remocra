package fr.eaudeparis.syncremocra.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/** Verifies every validated EDP temporary-unavailability anomaly mapping. */
public class ReferentielAnomaliesMigrationTest {

  @Test
  public void shouldShipEveryValidatedTemporaryUnavailabilityMapping() throws IOException {
    String v117 = readMigration("db/migration/V1.17__passage_preprod.sql");
    assertContainsMappings(
        v117,
        new String[][] {
          {"BSPP_APDP", "APP A RENOUVELER"},
          {"BSPP_APDP", "APP A REPARER"},
          {"BSPP_APSE", "APP SANS EAU"},
          {"BSPP_APSE", "APP SIGNALE"},
          {"BSPP_APSE", "FERME DEMANDE TIERS"},
          {"BSPP_APSE", "SANS CONDUITE"},
          {"BSPP_APSE", "ARRET EAU"},
          {"BSPP_APSE", "PUISAGE"},
          {"BSPP_APSE", "FERMETURE HIVERNALE"},
          {"BSPP_APEN", "APP SUR CHANTIER"},
          {"BSPP_INTE", "INACCESSIBLE"}
        });

    String v139 = readMigration("db/migration/V1.39__ajout_motifs_indispo_ssq_projet.sql");
    assertContainsMappings(
        v139, new String[][] {{"BSPP_APSE", "FERME DEMANDE SSQ"}, {"BSPP_APSE", "APP PROJET"}});
  }

  @Test
  public void shouldRenameStreetpoolingCodeToStrtpool() throws IOException {
    String v126 = readMigration("db/migration/V1.26__trigger_pullworker_auteur_modification.sql");

    assertTrue(v126.contains("SET code_bspp = 'STRTPOOL' WHERE code_bspp = 'BSPP_STPO'"));
  }

  private String readMigration(String resource) throws IOException {
    try (InputStream migration = getClass().getClassLoader().getResourceAsStream(resource)) {
      assertNotNull(resource, migration);
      return new String(migration.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private void assertContainsMappings(String sql, String[][] mappings) {
    for (String[] mapping : mappings) {
      assertTrue(sql.contains("('" + mapping[0] + "', '" + mapping[1] + "')"));
    }
  }
}
