package fr.eaudeparis.syncremocra.repository.pei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.eaudeparis.syncremocra.repository.message.PeiType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class PeiNatureReferenceResolverTest {

  @Test
  public void shouldResolveLegacyPibiNature() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("type", "PIBI");
    pei.put("nature", "PI");

    PeiNatureReference reference =
        PeiNatureReferenceResolver.resolve(
            pei, Arrays.asList(nature("pibi-id", "PI")), Arrays.asList(nature("pena-id", "PA")));

    assertEquals(PeiType.PIBI, reference.getPeiType());
    assertEquals("PI", reference.getNatureCode());
  }

  @Test
  public void shouldResolveV3PenaNatureId() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("peiNatureId", "pena-id");

    PeiNatureReference reference =
        PeiNatureReferenceResolver.resolve(
            pei, Arrays.asList(nature("pibi-id", "PI")), Arrays.asList(nature("pena-id", "PA")));

    assertEquals(PeiType.PENA, reference.getPeiType());
    assertEquals("PA", reference.getNatureCode());
  }

  @Test
  public void shouldReturnNullWhenNatureCannotBeResolved() {
    List<Map<String, Object>> pibiNatures = Arrays.asList(nature("pibi-id", "PI"));
    List<Map<String, Object>> penaNatures = Arrays.asList(nature("pena-id", "PA"));

    assertNull(PeiNatureReferenceResolver.resolve(new HashMap<>(), pibiNatures, penaNatures));

    Map<String, Object> pei = new HashMap<>();
    pei.put("peiNatureId", "unknown");
    assertNull(PeiNatureReferenceResolver.resolve(pei, pibiNatures, penaNatures));
  }

  private Map<String, Object> nature(String id, String code) {
    Map<String, Object> nature = new HashMap<>();
    nature.put("natureId", id);
    nature.put("natureCode", code);
    return nature;
  }
}
