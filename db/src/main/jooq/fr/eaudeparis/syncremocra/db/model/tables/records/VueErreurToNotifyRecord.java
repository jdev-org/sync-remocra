/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables.records;

import fr.eaudeparis.syncremocra.db.model.tables.VueErreurToNotify;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.XML;
import org.jooq.impl.TableRecordImpl;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class VueErreurToNotifyRecord extends TableRecordImpl<VueErreurToNotifyRecord>
    implements Record4<String, Long, String, XML> {

  private static final long serialVersionUID = 1L;

  /** Setter for <code>edp.vue_erreur_to_notify.contexte</code>. */
  public VueErreurToNotifyRecord setContexte(String value) {
    set(0, value);
    return this;
  }

  /** Getter for <code>edp.vue_erreur_to_notify.contexte</code>. */
  public String getContexte() {
    return (String) get(0);
  }

  /** Setter for <code>edp.vue_erreur_to_notify.erreurs_nombre</code>. */
  public VueErreurToNotifyRecord setErreursNombre(Long value) {
    set(1, value);
    return this;
  }

  /** Getter for <code>edp.vue_erreur_to_notify.erreurs_nombre</code>. */
  public Long getErreursNombre() {
    return (Long) get(1);
  }

  /** Setter for <code>edp.vue_erreur_to_notify.erreurs_ids</code>. */
  public VueErreurToNotifyRecord setErreursIds(String value) {
    set(2, value);
    return this;
  }

  /** Getter for <code>edp.vue_erreur_to_notify.erreurs_ids</code>. */
  public String getErreursIds() {
    return (String) get(2);
  }

  /** Setter for <code>edp.vue_erreur_to_notify.xml_notification</code>. */
  public VueErreurToNotifyRecord setXmlNotification(XML value) {
    set(3, value);
    return this;
  }

  /** Getter for <code>edp.vue_erreur_to_notify.xml_notification</code>. */
  public XML getXmlNotification() {
    return (XML) get(3);
  }

  // -------------------------------------------------------------------------
  // Record4 type implementation
  // -------------------------------------------------------------------------

  @Override
  public Row4<String, Long, String, XML> fieldsRow() {
    return (Row4) super.fieldsRow();
  }

  @Override
  public Row4<String, Long, String, XML> valuesRow() {
    return (Row4) super.valuesRow();
  }

  @Override
  public Field<String> field1() {
    return VueErreurToNotify.VUE_ERREUR_TO_NOTIFY.CONTEXTE;
  }

  @Override
  public Field<Long> field2() {
    return VueErreurToNotify.VUE_ERREUR_TO_NOTIFY.ERREURS_NOMBRE;
  }

  @Override
  public Field<String> field3() {
    return VueErreurToNotify.VUE_ERREUR_TO_NOTIFY.ERREURS_IDS;
  }

  @Override
  public Field<XML> field4() {
    return VueErreurToNotify.VUE_ERREUR_TO_NOTIFY.XML_NOTIFICATION;
  }

  @Override
  public String component1() {
    return getContexte();
  }

  @Override
  public Long component2() {
    return getErreursNombre();
  }

  @Override
  public String component3() {
    return getErreursIds();
  }

  @Override
  public XML component4() {
    return getXmlNotification();
  }

  @Override
  public String value1() {
    return getContexte();
  }

  @Override
  public Long value2() {
    return getErreursNombre();
  }

  @Override
  public String value3() {
    return getErreursIds();
  }

  @Override
  public XML value4() {
    return getXmlNotification();
  }

  @Override
  public VueErreurToNotifyRecord value1(String value) {
    setContexte(value);
    return this;
  }

  @Override
  public VueErreurToNotifyRecord value2(Long value) {
    setErreursNombre(value);
    return this;
  }

  @Override
  public VueErreurToNotifyRecord value3(String value) {
    setErreursIds(value);
    return this;
  }

  @Override
  public VueErreurToNotifyRecord value4(XML value) {
    setXmlNotification(value);
    return this;
  }

  @Override
  public VueErreurToNotifyRecord values(String value1, Long value2, String value3, XML value4) {
    value1(value1);
    value2(value2);
    value3(value3);
    value4(value4);
    return this;
  }

  // -------------------------------------------------------------------------
  // Constructors
  // -------------------------------------------------------------------------

  /** Create a detached VueErreurToNotifyRecord */
  public VueErreurToNotifyRecord() {
    super(VueErreurToNotify.VUE_ERREUR_TO_NOTIFY);
  }

  /** Create a detached, initialised VueErreurToNotifyRecord */
  public VueErreurToNotifyRecord(
      String contexte, Long erreursNombre, String erreursIds, XML xmlNotification) {
    super(VueErreurToNotify.VUE_ERREUR_TO_NOTIFY);

    setContexte(contexte);
    setErreursNombre(erreursNombre);
    setErreursIds(erreursIds);
    setXmlNotification(xmlNotification);
  }
}
