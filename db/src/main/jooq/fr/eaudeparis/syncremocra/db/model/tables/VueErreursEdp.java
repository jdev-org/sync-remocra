/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables;

import fr.eaudeparis.syncremocra.db.model.Edp;
import fr.eaudeparis.syncremocra.db.model.tables.records.VueErreursEdpRecord;
import java.time.LocalDateTime;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class VueErreursEdp extends TableImpl<VueErreursEdpRecord> {

  private static final long serialVersionUID = 1L;

  /** The reference instance of <code>edp.vue_erreurs_edp</code> */
  public static final VueErreursEdp VUE_ERREURS_EDP = new VueErreursEdp();

  /** The class holding records for this type */
  @Override
  public Class<VueErreursEdpRecord> getRecordType() {
    return VueErreursEdpRecord.class;
  }

  /** The column <code>edp.vue_erreurs_edp.date</code>. */
  public final TableField<VueErreursEdpRecord, LocalDateTime> DATE =
      createField(DSL.name("date"), SQLDataType.LOCALDATETIME(6), this, "");

  /** The column <code>edp.vue_erreurs_edp.notifie</code>. */
  public final TableField<VueErreursEdpRecord, Boolean> NOTIFIE =
      createField(DSL.name("notifie"), SQLDataType.BOOLEAN, this, "");

  /** The column <code>edp.vue_erreurs_edp.message_erreur</code>. */
  public final TableField<VueErreursEdpRecord, String> MESSAGE_ERREUR =
      createField(DSL.name("message_erreur"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.vue_erreurs_edp.message_action</code>. */
  public final TableField<VueErreursEdpRecord, String> MESSAGE_ACTION =
      createField(DSL.name("message_action"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.vue_erreurs_edp.contexte</code>. */
  public final TableField<VueErreursEdpRecord, String> CONTEXTE =
      createField(DSL.name("contexte"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.vue_erreurs_edp.type</code>. */
  public final TableField<VueErreursEdpRecord, String> TYPE =
      createField(DSL.name("type"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.vue_erreurs_edp.reference</code>. */
  public final TableField<VueErreursEdpRecord, String> REFERENCE =
      createField(DSL.name("reference"), SQLDataType.VARCHAR(15), this, "");

  /** The column <code>edp.vue_erreurs_edp.json</code>. */
  public final TableField<VueErreursEdpRecord, String> JSON =
      createField(DSL.name("json"), SQLDataType.VARCHAR, this, "");

  private VueErreursEdp(Name alias, Table<VueErreursEdpRecord> aliased) {
    this(alias, aliased, null);
  }

  private VueErreursEdp(Name alias, Table<VueErreursEdpRecord> aliased, Field<?>[] parameters) {
    super(
        alias,
        null,
        aliased,
        parameters,
        DSL.comment(""),
        TableOptions.view(
            "create view \"vue_erreurs_edp\" as  SELECT e.date,\n"
                + "    e.notifie,\n"
                + "    te.message_erreur,\n"
                + "    te.message_action,\n"
                + "    te.contexte,\n"
                + "    m.type,\n"
                + "    tp.reference,\n"
                + "    m.json\n"
                + "   FROM (((erreur e\n"
                + "     JOIN type_erreur te ON ((te.id = e.type_erreur)))\n"
                + "     LEFT JOIN message m ON ((e.message = m.id)))\n"
                + "     LEFT JOIN tracabilite_pei tp ON ((tp.id = m.id_traca_pei)));"));
  }

  /** Create an aliased <code>edp.vue_erreurs_edp</code> table reference */
  public VueErreursEdp(String alias) {
    this(DSL.name(alias), VUE_ERREURS_EDP);
  }

  /** Create an aliased <code>edp.vue_erreurs_edp</code> table reference */
  public VueErreursEdp(Name alias) {
    this(alias, VUE_ERREURS_EDP);
  }

  /** Create a <code>edp.vue_erreurs_edp</code> table reference */
  public VueErreursEdp() {
    this(DSL.name("vue_erreurs_edp"), null);
  }

  public <O extends Record> VueErreursEdp(Table<O> child, ForeignKey<O, VueErreursEdpRecord> key) {
    super(child, key, VUE_ERREURS_EDP);
  }

  @Override
  public Schema getSchema() {
    return Edp.EDP;
  }

  @Override
  public VueErreursEdp as(String alias) {
    return new VueErreursEdp(DSL.name(alias), this);
  }

  @Override
  public VueErreursEdp as(Name alias) {
    return new VueErreursEdp(alias, this);
  }

  /** Rename this table */
  @Override
  public VueErreursEdp rename(String name) {
    return new VueErreursEdp(DSL.name(name), null);
  }

  /** Rename this table */
  @Override
  public VueErreursEdp rename(Name name) {
    return new VueErreursEdp(name, null);
  }

  // -------------------------------------------------------------------------
  // Row8 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row8<LocalDateTime, Boolean, String, String, String, String, String, String> fieldsRow() {
    return (Row8) super.fieldsRow();
  }
}
