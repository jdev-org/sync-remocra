package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class PeiTypeResolverTest {

  @Test
  public void shouldResolvePibiUsingV3NatureId() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("peiNatureId", "nature-pibi-id");

    List<Map<String, Object>> pibiNatures =
        Arrays.asList(nature("nature-pibi-id", "PI"), nature("other", "BI"));
    List<Map<String, Object>> penaNatures = Arrays.asList(nature("pena-id", "CITERNE"));

    assertEquals(PeiType.PIBI, PeiTypeResolver.resolve(pei, pibiNatures, penaNatures));
  }

  @Test
  public void shouldResolvePenaUsingLegacyNatureCode() {
    Map<String, Object> pei = new HashMap<>();
    pei.put("nature", "PA");

    List<Map<String, Object>> pibiNatures = Arrays.asList(nature("pibi-id", "PI"));
    List<Map<String, Object>> penaNatures = Arrays.asList(nature("pena-id", "PA"));

    assertEquals(PeiType.PENA, PeiTypeResolver.resolve(pei, pibiNatures, penaNatures));
  }

  @Test
  public void shouldReturnNullWhenNatureIsMissingOrUnknown() {
    List<Map<String, Object>> pibiNatures = Arrays.asList(nature("pibi-id", "PI"));
    List<Map<String, Object>> penaNatures = Arrays.asList(nature("pena-id", "PA"));

    assertNull(PeiTypeResolver.resolve(new HashMap<>(), pibiNatures, penaNatures));

    Map<String, Object> pei = new HashMap<>();
    pei.put("peiNatureId", "unknown");
    assertNull(PeiTypeResolver.resolve(pei, pibiNatures, penaNatures));
  }

  private Map<String, Object> nature(String id, String code) {
    Map<String, Object> nature = new HashMap<>();
    nature.put("natureId", id);
    nature.put("natureCode", code);
    return nature;
  }
}
