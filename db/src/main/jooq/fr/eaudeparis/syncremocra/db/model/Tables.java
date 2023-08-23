/*
 * This file is generated by jOOQ.
 */
package fr.eaudeparis.syncremocra.db.model;


import fr.eaudeparis.syncremocra.db.model.tables.Erreur;
import fr.eaudeparis.syncremocra.db.model.tables.Message;
import fr.eaudeparis.syncremocra.db.model.tables.MotifIndispoActif;
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
import fr.eaudeparis.syncremocra.db.model.tables.VueErreurToNotify;
import fr.eaudeparis.syncremocra.db.model.tables.VueErreursEdp;
import fr.eaudeparis.syncremocra.db.model.tables.VuePeiEdpRemocra;
import fr.eaudeparis.syncremocra.db.model.tables.VuePeiMotifIndispoEdpRemocra;
import fr.eaudeparis.syncremocra.db.model.tables.VueVisite;


/**
 * Convenience access to all tables in edp.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * Erreur rencontrée lors du processus global de synchronisation entre REMOCRA et EDP et pouvant donner lieu à notification
     */
    public static final Erreur ERREUR = Erreur.ERREUR;

    /**
     * The table <code>edp.message</code>.
     */
    public static final Message MESSAGE = Message.MESSAGE;

    /**
     * The table <code>edp.motif_indispo_actif</code>.
     */
    public static final MotifIndispoActif MOTIF_INDISPO_ACTIF = MotifIndispoActif.MOTIF_INDISPO_ACTIF;

    /**
     * The table <code>edp.parametres</code>.
     */
    public static final Parametres PARAMETRES = Parametres.PARAMETRES;

    /**
     * The table <code>edp.pull_hydrant</code>.
     */
    public static final PullHydrant PULL_HYDRANT = PullHydrant.PULL_HYDRANT;

    /**
     * The table <code>edp.pull_hydrant_anomalies</code>.
     */
    public static final PullHydrantAnomalies PULL_HYDRANT_ANOMALIES = PullHydrantAnomalies.PULL_HYDRANT_ANOMALIES;

    /**
     * The table <code>edp.pull_hydrant_visite</code>.
     */
    public static final PullHydrantVisite PULL_HYDRANT_VISITE = PullHydrantVisite.PULL_HYDRANT_VISITE;

    /**
     * The table <code>edp.pull_message</code>.
     */
    public static final PullMessage PULL_MESSAGE = PullMessage.PULL_MESSAGE;

    /**
     * The table <code>edp.referentiel_anomalies</code>.
     */
    public static final ReferentielAnomalies REFERENTIEL_ANOMALIES = ReferentielAnomalies.REFERENTIEL_ANOMALIES;

    /**
     * The table <code>edp.referentiel_hydrant_diametres</code>.
     */
    public static final ReferentielHydrantDiametres REFERENTIEL_HYDRANT_DIAMETRES = ReferentielHydrantDiametres.REFERENTIEL_HYDRANT_DIAMETRES;

    /**
     * The table <code>edp.referentiel_marques_modeles</code>.
     */
    public static final ReferentielMarquesModeles REFERENTIEL_MARQUES_MODELES = ReferentielMarquesModeles.REFERENTIEL_MARQUES_MODELES;

    /**
     * The table <code>edp.tracabilite_indispo</code>.
     */
    public static final TracabiliteIndispo TRACABILITE_INDISPO = TracabiliteIndispo.TRACABILITE_INDISPO;

    /**
     * The table <code>edp.tracabilite_pei</code>.
     */
    public static final TracabilitePei TRACABILITE_PEI = TracabilitePei.TRACABILITE_PEI;

    /**
     * Typologie des erreurs pouvant être rencontrées lors du processus global de synchronisation entre REMOCRA et EDP
     */
    public static final TypeErreur TYPE_ERREUR = TypeErreur.TYPE_ERREUR;

    /**
     * The table <code>edp.vue_erreur_to_notify</code>.
     */
    public static final VueErreurToNotify VUE_ERREUR_TO_NOTIFY = VueErreurToNotify.VUE_ERREUR_TO_NOTIFY;

    /**
     * The table <code>edp.vue_erreurs_edp</code>.
     */
    public static final VueErreursEdp VUE_ERREURS_EDP = VueErreursEdp.VUE_ERREURS_EDP;

    /**
     * The table <code>edp.vue_pei_edp_remocra</code>.
     */
    public static final VuePeiEdpRemocra VUE_PEI_EDP_REMOCRA = VuePeiEdpRemocra.VUE_PEI_EDP_REMOCRA;

    /**
     * The table <code>edp.vue_pei_motif_indispo_edp_remocra</code>.
     */
    public static final VuePeiMotifIndispoEdpRemocra VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA = VuePeiMotifIndispoEdpRemocra.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA;

    /**
     * The table <code>edp.vue_visite</code>.
     */
    public static final VueVisite VUE_VISITE = VueVisite.VUE_VISITE;
}
