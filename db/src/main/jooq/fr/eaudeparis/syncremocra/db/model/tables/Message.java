/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables;

import fr.eaudeparis.syncremocra.db.model.Edp;
import fr.eaudeparis.syncremocra.db.model.Keys;
import fr.eaudeparis.syncremocra.db.model.tables.records.MessageRecord;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row12;
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
public class Message extends TableImpl<MessageRecord> {

  private static final long serialVersionUID = 1L;

  /** The reference instance of <code>edp.message</code> */
  public static final Message MESSAGE = new Message();

  /** The class holding records for this type */
  @Override
  public Class<MessageRecord> getRecordType() {
    return MessageRecord.class;
  }

  /** The column <code>edp.message.id</code>. */
  public final TableField<MessageRecord, Integer> ID =
      createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

  /** The column <code>edp.message.id_traca_pei</code>. */
  public final TableField<MessageRecord, Long> ID_TRACA_PEI =
      createField(DSL.name("id_traca_pei"), SQLDataType.BIGINT.nullable(false), this, "");

  /**
   * The column <code>edp.message.date</code>. Date et heure du changement tel que renseigné dans la
   * vue READ_ONLY.VUE_PEI_EDP_REMOCRA
   */
  public final TableField<MessageRecord, LocalDateTime> DATE =
      createField(
          DSL.name("date"),
          SQLDataType.LOCALDATETIME(6),
          this,
          " Date et heure du changement tel que renseigné dans la vue"
              + " READ_ONLY.VUE_PEI_EDP_REMOCRA");

  /**
   * The column <code>edp.message.type</code>. "CARACTERISTIQUES" pour les données techniques,
   * localisation, canalisation, etc., "VISITES" pour les disponibilités et les anomalies associées
   */
  public final TableField<MessageRecord, String> TYPE =
      createField(
          DSL.name("type"),
          SQLDataType.VARCHAR,
          this,
          "\"CARACTERISTIQUES\" pour les données techniques, localisation, canalisation, etc.,"
              + " \"VISITES\" pour les disponibilités et les anomalies associées");

  /**
   * The column <code>edp.message.statut</code>. A traiter : message à jouer ou à rejouer, Terminé :
   * le message a été joué avec succès ou avec échec le type d'erreur empêche tout rejeu
   */
  public final TableField<MessageRecord, String> STATUT =
      createField(
          DSL.name("statut"),
          SQLDataType.VARCHAR
              .nullable(false)
              .defaultValue(DSL.field("'A TRAITER'::character varying", SQLDataType.VARCHAR)),
          this,
          " A traiter : message à jouer ou à rejouer, Terminé : le message a été joué avec succès"
              + " ou avec échec le type d'erreur empêche tout rejeu");

  /**
   * The column <code>edp.message.synchroniser</code>. Indique si le message est à rejouer ou non
   */
  public final TableField<MessageRecord, Boolean> SYNCHRONISER =
      createField(
          DSL.name("synchroniser"),
          SQLDataType.BOOLEAN,
          this,
          "Indique si le message est à rejouer ou non");

  /**
   * The column <code>edp.message.synchronisations</code>. Nombre de fois où le message a été joué
   * ou rejoué
   */
  public final TableField<MessageRecord, Integer> SYNCHRONISATIONS =
      createField(
          DSL.name("synchronisations"),
          SQLDataType.INTEGER.nullable(false).defaultValue(DSL.field("0", SQLDataType.INTEGER)),
          this,
          "Nombre de fois où le message a été joué ou rejoué");

  /** The column <code>edp.message.erreur</code>. Message d'erreur éventuellement rencontré */
  public final TableField<MessageRecord, String> ERREUR =
      createField(
          DSL.name("erreur"),
          SQLDataType.VARCHAR,
          this,
          "Message d'erreur éventuellement rencontré");

  /** The column <code>edp.message.json</code>. */
  public final TableField<MessageRecord, String> JSON =
      createField(DSL.name("json"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.message.date_debut_verif</code>. */
  public final TableField<MessageRecord, LocalDateTime> DATE_DEBUT_VERIF =
      createField(DSL.name("date_debut_verif"), SQLDataType.LOCALDATETIME(6), this, "");

  /** The column <code>edp.message.json_creation_visite</code>. */
  public final TableField<MessageRecord, String> JSON_CREATION_VISITE =
      createField(DSL.name("json_creation_visite"), SQLDataType.VARCHAR, this, "");

  /** The column <code>edp.message.declencheur</code>. */
  public final TableField<MessageRecord, String> DECLENCHEUR =
      createField(DSL.name("declencheur"), SQLDataType.VARCHAR, this, "");

  private Message(Name alias, Table<MessageRecord> aliased) {
    this(alias, aliased, null);
  }

  private Message(Name alias, Table<MessageRecord> aliased, Field<?>[] parameters) {
    super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
  }

  /** Create an aliased <code>edp.message</code> table reference */
  public Message(String alias) {
    this(DSL.name(alias), MESSAGE);
  }

  /** Create an aliased <code>edp.message</code> table reference */
  public Message(Name alias) {
    this(alias, MESSAGE);
  }

  /** Create a <code>edp.message</code> table reference */
  public Message() {
    this(DSL.name("message"), null);
  }

  public <O extends Record> Message(Table<O> child, ForeignKey<O, MessageRecord> key) {
    super(child, key, MESSAGE);
  }

  @Override
  public Schema getSchema() {
    return Edp.EDP;
  }

  @Override
  public Identity<MessageRecord, Integer> getIdentity() {
    return (Identity<MessageRecord, Integer>) super.getIdentity();
  }

  @Override
  public UniqueKey<MessageRecord> getPrimaryKey() {
    return Keys.MESSAGE_PKEY;
  }

  @Override
  public List<UniqueKey<MessageRecord>> getKeys() {
    return Arrays.<UniqueKey<MessageRecord>>asList(Keys.MESSAGE_PKEY);
  }

  @Override
  public List<ForeignKey<MessageRecord, ?>> getReferences() {
    return Arrays.<ForeignKey<MessageRecord, ?>>asList(Keys.MESSAGE__FK_ID_TRACA_PEI);
  }

  private transient TracabilitePei _tracabilitePei;

  public TracabilitePei tracabilitePei() {
    if (_tracabilitePei == null)
      _tracabilitePei = new TracabilitePei(this, Keys.MESSAGE__FK_ID_TRACA_PEI);

    return _tracabilitePei;
  }

  @Override
  public Message as(String alias) {
    return new Message(DSL.name(alias), this);
  }

  @Override
  public Message as(Name alias) {
    return new Message(alias, this);
  }

  /** Rename this table */
  @Override
  public Message rename(String name) {
    return new Message(DSL.name(name), null);
  }

  /** Rename this table */
  @Override
  public Message rename(Name name) {
    return new Message(name, null);
  }

  // -------------------------------------------------------------------------
  // Row12 type methods
  // -------------------------------------------------------------------------

  @Override
  public Row12<
          Integer,
          Long,
          LocalDateTime,
          String,
          String,
          Boolean,
          Integer,
          String,
          String,
          LocalDateTime,
          String,
          String>
      fieldsRow() {
    return (Row12) super.fieldsRow();
  }
}
