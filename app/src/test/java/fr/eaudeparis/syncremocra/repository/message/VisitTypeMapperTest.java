package fr.eaudeparis.syncremocra.repository.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class VisitTypeMapperTest {

  @Test
  public void shouldMapLocalVisitTypesToRemocraTypes() {
    assertEquals("CTP", VisitTypeMapper.toRemocraType("CTRL"));
    assertEquals("CTP", VisitTypeMapper.toRemocraType("PICF - CONTROLE"));
    assertEquals("CTP", VisitTypeMapper.toRemocraType("PIQP TEST"));
    assertEquals("NP", VisitTypeMapper.toRemocraType("NP"));
    assertEquals("NP", VisitTypeMapper.toRemocraType("NPQP"));
  }

  @Test
  public void shouldRejectUnsupportedVisitTypes() {
    assertNull(VisitTypeMapper.toRemocraType(null));
    assertNull(VisitTypeMapper.toRemocraType("RECEPTION"));
    assertNull(VisitTypeMapper.toRemocraType("ROP"));
  }

  @Test
  public void shouldMapRemocraTypesBackToLegacyLocalTypes() {
    assertEquals("CTRL", VisitTypeMapper.toLocalType("CTP"));
    assertEquals("NP", VisitTypeMapper.toLocalType("NP"));
    assertEquals("RECEPTION", VisitTypeMapper.toLocalType("RECEPTION"));
  }
}
