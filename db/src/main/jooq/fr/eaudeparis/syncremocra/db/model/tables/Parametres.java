/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables;

import fr.eaudeparis.syncremocra.db.model.Edp;
import fr.eaudeparis.syncremocra.db.model.Keys;
import fr.eaudeparis.syncremocra.db.model.tables.records.ParametresRecord;
import java.util.Arrays;
import java.util.List;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row2;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

/** This class is generated by jOOQ. */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Parametres extends TableImpl<ParametresRecord> {

  private static final long serialVersionUID = 1L;

  /** The reference instance of <code>edp.parametres</code> */
  public static final Parametres PARAMETRES = new Parametres();

  /** The class holding records for this type */
  @Override
  public Class<ParametresRecord> getRecordType() {
    return ParametresRecord.class;
  }

  /** The column <code>edp.parametres.code</code>. */
  public final TableField<ParametresRecord, String> CODE =
      createField(DSL.name("code"), SQLDataType.VARCHAR.nullable(false), this, "");

  /** The column <code>edp.parametres.valeur</code>. */
  public final TableField<ParametresRecord, String> VALEUR =
      createField(DSL.name("valeur"), SQLDataType.VARCHAR, this, "");

  private Parametres(Name alias, Table<ParametresRecord> aliased) {
    this(alias, aliased, null);
  }

  private Parametres(Name alias, Table<ParametresRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /** Create an aliased <code>edp.parametres</code> table reference */
  public Parametres(String alias) {
    this(DSL.name(alias), PARAMETRES);
  }

  /** Create an aliased <code>edp.parametres</code> table reference */
  public Parametres(Name alias) {
    this(alias, PARAMETRES);
  }

  /** Create a <code>edp.parametres</code> table reference */
  public Parametres() {
    this(DSL.name("parametres"), null);
  }

  public <O extends Record> Parametres(Table<O> child, ForeignKey<O, ParametresRecord> key) {
    super(child, key, PARAMETRES);
  }

  @Override
  public Schema getSchema() {
    return Edp.EDP;
  }

  @Override
  public UniqueKey<ParametresRecord> getPrimaryKey() {
    return Keys.PARAMETRES_PKEY;
  }

  @Override
  public List<UniqueKey<ParametresRecord>> getKeys() {
    return Arrays.<UniqueKey<ParametresRecord>>asList(Keys.PARAMETRES_PKEY);
  }

  @Override
  public Parametres as(String alias) {
    return new Parametres(DSL.name(alias), this);
  }

  @Override
  public Parametres as(Name alias) {
    return new Parametres(alias, this);
  }

  /** Rename this table */
  @Override
  public Parametres rename(String name) {
    return new Parametres(DSL.name(name), null);
  }

  /** Rename this table */
  @Override
  public Parametres rename(Name name) {
    return new Parametres(name, null);
  }

  // -------------------------------------------------------------------------
  // Row2 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row2<String, String> fieldsRow() {
    return (Row2) super.fieldsRow();
  }
}
