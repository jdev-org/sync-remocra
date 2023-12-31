/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model.tables;


import fr.eaudeparis.syncremocra.db.model.Edp;
import fr.eaudeparis.syncremocra.db.model.tables.records.VueErreurToNotifyRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.XML;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class VueErreurToNotify extends TableImpl<VueErreurToNotifyRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>edp.vue_erreur_to_notify</code>
     */
    public static final VueErreurToNotify VUE_ERREUR_TO_NOTIFY = new VueErreurToNotify();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VueErreurToNotifyRecord> getRecordType() {
        return VueErreurToNotifyRecord.class;
    }

    /**
     * The column <code>edp.vue_erreur_to_notify.contexte</code>.
     */
    public final TableField<VueErreurToNotifyRecord, String> CONTEXTE = createField(DSL.name("contexte"), SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>edp.vue_erreur_to_notify.erreurs_nombre</code>.
     */
    public final TableField<VueErreurToNotifyRecord, Long> ERREURS_NOMBRE = createField(DSL.name("erreurs_nombre"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>edp.vue_erreur_to_notify.erreurs_ids</code>.
     */
    public final TableField<VueErreurToNotifyRecord, String> ERREURS_IDS = createField(DSL.name("erreurs_ids"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>edp.vue_erreur_to_notify.xml_notification</code>.
     */
    public final TableField<VueErreurToNotifyRecord, XML> XML_NOTIFICATION = createField(DSL.name("xml_notification"), SQLDataType.XML, this, "");

    private VueErreurToNotify(Name alias, Table<VueErreurToNotifyRecord> aliased) {
        this(alias, aliased, null);
    }

    private VueErreurToNotify(Name alias, Table<VueErreurToNotifyRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("create view \"vue_erreur_to_notify\" as  SELECT terr.contexte,\n    count(*) AS erreurs_nombre,\n    array_to_string(array_agg(err.id), ','::text) AS erreurs_ids,\n    XMLELEMENT(NAME rapport, XMLELEMENT(NAME erreurs, xmlagg(XMLELEMENT(NAME erreur, XMLELEMENT(NAME \"dateHeureSynchronisation\", to_char(err.date, 'dd/mm/yyyy hh24:mi:ss'::text)), XMLELEMENT(NAME \"refPEI\", COALESCE(tpei.reference, '-'::character varying)), XMLELEMENT(NAME \"messageErreur\", err.description), XMLELEMENT(NAME \"messageAction\", terr.message_action), XMLELEMENT(NAME donnees, XMLELEMENT(NAME \"contexteVisite\", COALESCE(replace(((mess.json_creation_visite)::json ->> 'contexte'::text), 'NP'::text, 'Visite non programmée'::text), replace(((mess.json)::json ->> 'contexte'::text), 'NP'::text, 'Visite non programmée'::text), '--'::text)), XMLELEMENT(NAME \"dateHeureVisite\", COALESCE(to_char((((mess.json_creation_visite)::json ->> 'date'::text))::timestamp without time zone, 'dd/mm/yyyy à hh24h mi'::text), to_char((((mess.json)::json ->> 'date'::text))::timestamp without time zone, 'dd/mm/yyyy à hh24h mi'::text), '--'::text)), XMLELEMENT(NAME motifs, array_to_string(ARRAY( SELECT json_array_elements_text(((mess.json_creation_visite)::json -> 'anomaliesConstatees'::text)) AS json_array_elements_text), ', '::text))))))) AS xml_notification\n   FROM (((erreur err\n     JOIN type_erreur terr ON ((terr.id = err.type_erreur)))\n     LEFT JOIN message mess ON ((mess.id = err.message)))\n     LEFT JOIN tracabilite_pei tpei ON ((tpei.id = mess.id_traca_pei)))\n  WHERE (NOT err.notifie)\n  GROUP BY terr.contexte;"));
    }

    /**
     * Create an aliased <code>edp.vue_erreur_to_notify</code> table reference
     */
    public VueErreurToNotify(String alias) {
        this(DSL.name(alias), VUE_ERREUR_TO_NOTIFY);
    }

    /**
     * Create an aliased <code>edp.vue_erreur_to_notify</code> table reference
     */
    public VueErreurToNotify(Name alias) {
        this(alias, VUE_ERREUR_TO_NOTIFY);
    }

    /**
     * Create a <code>edp.vue_erreur_to_notify</code> table reference
     */
    public VueErreurToNotify() {
        this(DSL.name("vue_erreur_to_notify"), null);
    }

    public <O extends Record> VueErreurToNotify(Table<O> child, ForeignKey<O, VueErreurToNotifyRecord> key) {
        super(child, key, VUE_ERREUR_TO_NOTIFY);
    }

    @Override
    public Schema getSchema() {
        return Edp.EDP;
    }

    @Override
    public VueErreurToNotify as(String alias) {
        return new VueErreurToNotify(DSL.name(alias), this);
    }

    @Override
    public VueErreurToNotify as(Name alias) {
        return new VueErreurToNotify(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public VueErreurToNotify rename(String name) {
        return new VueErreurToNotify(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public VueErreurToNotify rename(Name name) {
        return new VueErreurToNotify(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<String, Long, String, XML> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}
