/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model;


import fr.eaudeparis.syncremocra.db.model.tables.Erreur;
import fr.eaudeparis.syncremocra.db.model.tables.Message;
import fr.eaudeparis.syncremocra.db.model.tables.Parametres;
import fr.eaudeparis.syncremocra.db.model.tables.PullHydrant;
import fr.eaudeparis.syncremocra.db.model.tables.PullHydrantAnomalies;
import fr.eaudeparis.syncremocra.db.model.tables.PullHydrantVisite;
import fr.eaudeparis.syncremocra.db.model.tables.PullMessage;
import fr.eaudeparis.syncremocra.db.model.tables.ReferentielAnomalies;
import fr.eaudeparis.syncremocra.db.model.tables.ReferentielHydrantDiametres;
import fr.eaudeparis.syncremocra.db.model.tables.ReferentielMarquesModeles;
import fr.eaudeparis.syncremocra.db.model.tables.TracabiliteIndispo;
import fr.eaudeparis.syncremocra.db.model.tables.TracabilitePei;
import fr.eaudeparis.syncremocra.db.model.tables.TypeErreur;
import fr.eaudeparis.syncremocra.db.model.tables.records.ErreurRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.MessageRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.ParametresRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.PullHydrantAnomaliesRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.PullHydrantRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.PullHydrantVisiteRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.PullMessageRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.ReferentielAnomaliesRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.ReferentielHydrantDiametresRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.ReferentielMarquesModelesRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.TracabiliteIndispoRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.TracabilitePeiRecord;
import fr.eaudeparis.syncremocra.db.model.tables.records.TypeErreurRecord;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * edp.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ErreurRecord> ERREUR_PKEY = Internal.createUniqueKey(Erreur.ERREUR, DSL.name("erreur_pkey"), new TableField[] { Erreur.ERREUR.ID }, true);
    public static final UniqueKey<MessageRecord> MESSAGE_PKEY = Internal.createUniqueKey(Message.MESSAGE, DSL.name("message_pkey"), new TableField[] { Message.MESSAGE.ID }, true);
    public static final UniqueKey<ParametresRecord> PARAMETRES_PKEY = Internal.createUniqueKey(Parametres.PARAMETRES, DSL.name("parametres_pkey"), new TableField[] { Parametres.PARAMETRES.CODE }, true);
    public static final UniqueKey<PullHydrantRecord> PULL_HYDRANT_PKEY = Internal.createUniqueKey(PullHydrant.PULL_HYDRANT, DSL.name("pull_hydrant_pkey"), new TableField[] { PullHydrant.PULL_HYDRANT.ID }, true);
    public static final UniqueKey<PullHydrantAnomaliesRecord> PULL_HYDRANT_ANOMALIES_PKEY = Internal.createUniqueKey(PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES, DSL.name("pull_hydrant_anomalies_pkey"), new TableField[] { PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES.ID }, true);
    public static final UniqueKey<PullHydrantVisiteRecord> PULL_HYDRANT_VISITE_PKEY = Internal.createUniqueKey(PullHydrantVisite.PULL_HYDRANT_VISITE, DSL.name("pull_hydrant_visite_pkey"), new TableField[] { PullHydrantVisite.PULL_HYDRANT_VISITE.ID }, true);
    public static final UniqueKey<PullMessageRecord> PULL_MESSAGE_PKEY = Internal.createUniqueKey(PullMessage.PULL_MESSAGE, DSL.name("pull_message_pkey"), new TableField[] { PullMessage.PULL_MESSAGE.ID }, true);
    public static final UniqueKey<ReferentielAnomaliesRecord> REFERENTIEL_ANOMALIES_PKEY = Internal.createUniqueKey(ReferentielAnomalies.REFERENTIEL_ANOMALIES, DSL.name("referentiel_anomalies_pkey"), new TableField[] { ReferentielAnomalies.REFERENTIEL_ANOMALIES.CODE_BSPP, ReferentielAnomalies.REFERENTIEL_ANOMALIES.CODE_EDP }, true);
    public static final UniqueKey<ReferentielHydrantDiametresRecord> REFERENTIEL_HYDRANT_DIAMETRES_PKEY = Internal.createUniqueKey(ReferentielHydrantDiametres.REFERENTIEL_HYDRANT_DIAMETRES, DSL.name("referentiel_hydrant_diametres_pkey"), new TableField[] { ReferentielHydrantDiametres.REFERENTIEL_HYDRANT_DIAMETRES.CODE_BSPP, ReferentielHydrantDiametres.REFERENTIEL_HYDRANT_DIAMETRES.CODE_EDP }, true);
    public static final UniqueKey<ReferentielMarquesModelesRecord> REFERENTIEL_MARQUES_MODELES_PKEY = Internal.createUniqueKey(ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES, DSL.name("referentiel_marques_modeles_pkey"), new TableField[] { ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES.CODE_MODELE_EDP, ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES.CODE_MARQUE_REMOCRA, ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES.CODE_MODELE_REMOCRA }, true);
    public static final UniqueKey<TracabiliteIndispoRecord> TRACABILITE_INDISPO_PKEY = Internal.createUniqueKey(TracabiliteIndispo.TRACABILITE_INDISPO, DSL.name("tracabilite_indispo_pkey"), new TableField[] { TracabiliteIndispo.TRACABILITE_INDISPO.ID }, true);
    public static final UniqueKey<TracabilitePeiRecord> TRACABILITE_PEI_PKEY = Internal.createUniqueKey(TracabilitePei.TRACABILITE_PEI, DSL.name("tracabilite_pei_pkey"), new TableField[] { TracabilitePei.TRACABILITE_PEI.ID }, true);
    public static final UniqueKey<TypeErreurRecord> TYPE_ERREUR_CODE_KEY = Internal.createUniqueKey(TypeErreur.TYPE_ERREUR, DSL.name("type_erreur_code_key"), new TableField[] { TypeErreur.TYPE_ERREUR.CODE }, true);
    public static final UniqueKey<TypeErreurRecord> TYPE_ERREUR_PKEY = Internal.createUniqueKey(TypeErreur.TYPE_ERREUR, DSL.name("type_erreur_pkey"), new TableField[] { TypeErreur.TYPE_ERREUR.ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<ErreurRecord, MessageRecord> ERREUR__FK_ERREUR_MESSAGE = Internal.createForeignKey(Erreur.ERREUR, DSL.name("fk_erreur_message"), new TableField[] { Erreur.ERREUR.MESSAGE }, Keys.MESSAGE_PKEY, new TableField[] { Message.MESSAGE.ID }, true);
    public static final ForeignKey<ErreurRecord, TypeErreurRecord> ERREUR__FK_ERREUR_TYPE_ERREUR = Internal.createForeignKey(Erreur.ERREUR, DSL.name("fk_erreur_type_erreur"), new TableField[] { Erreur.ERREUR.TYPE_ERREUR }, Keys.TYPE_ERREUR_PKEY, new TableField[] { TypeErreur.TYPE_ERREUR.ID }, true);
    public static final ForeignKey<MessageRecord, TracabilitePeiRecord> MESSAGE__FK_ID_TRACA_PEI = Internal.createForeignKey(Message.MESSAGE, DSL.name("fk_id_traca_pei"), new TableField[] { Message.MESSAGE.ID_TRACA_PEI }, Keys.TRACABILITE_PEI_PKEY, new TableField[] { TracabilitePei.TRACABILITE_PEI.ID }, true);
    public static final ForeignKey<PullHydrantAnomaliesRecord, PullHydrantVisiteRecord> PULL_HYDRANT_ANOMALIES__FK_VISITE = Internal.createForeignKey(PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES, DSL.name("fk_visite"), new TableField[] { PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES.VISITE }, Keys.PULL_HYDRANT_VISITE_PKEY, new TableField[] { PullHydrantVisite.PULL_HYDRANT_VISITE.ID }, true);
    public static final ForeignKey<PullHydrantVisiteRecord, PullHydrantRecord> PULL_HYDRANT_VISITE__FK_HYDRANT = Internal.createForeignKey(PullHydrantVisite.PULL_HYDRANT_VISITE, DSL.name("fk_hydrant"), new TableField[] { PullHydrantVisite.PULL_HYDRANT_VISITE.HYDRANT }, Keys.PULL_HYDRANT_PKEY, new TableField[] { PullHydrant.PULL_HYDRANT.ID }, true);
    public static final ForeignKey<TracabiliteIndispoRecord, TracabilitePeiRecord> TRACABILITE_INDISPO__FK_ID_TRACA_PEI = Internal.createForeignKey(TracabiliteIndispo.TRACABILITE_INDISPO, DSL.name("fk_id_traca_pei"), new TableField[] { TracabiliteIndispo.TRACABILITE_INDISPO.ID_TRACA_PEI }, Keys.TRACABILITE_PEI_PKEY, new TableField[] { TracabilitePei.TRACABILITE_PEI.ID }, true);
}
