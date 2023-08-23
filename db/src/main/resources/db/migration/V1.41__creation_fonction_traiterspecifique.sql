CREATE OR REPLACE FUNCTION edp.traiter_specifique(dateLaPlusRecente timestamp without time zone
, derniereRemontee timestamp without time zone
, current_execution timestamp without time zone
, param_reference VARCHAR
, param_type VARCHAR)
    RETURNS timestamp without time zone
    LANGUAGE plpgsql
AS
$function$
DECLARE

    visite             RECORD;
    remoteMotifIndispo RECORD;
    remotepei          RECORD;
    tracaPeiId         integer;
    type               VARCHAR;
    declencheur        VARCHAR;


BEGIN

    SELECT gid_edp
         , reference
         , type_objet
         , prive
         , precision_pei_prive
         , adresse
         , notes_localisation
         , diametre
         , modele
         , diametre_canalisation
         , materiau_canalisation
         , date_essai
         , essai_pression_statique
         , essai_pression_dynamique
         , essai_debit
         , etat
         , date_derniere_visite
         , type_derniere_visite
         , date_maj_p
         , date_maj_e
         , date_maj_qp
         , date_maj_cf
         , date_maj_np
         , date_maj_mj


    INTO remotepei
    FROM edp.vue_pei_edp_remocra pei
    WHERE pei.reference = param_reference;

    IF param_type = 'NP' THEN
        type = 'NP%';
        declencheur = 'date_maj_np';
    ELSEIF param_type = 'CF' THEN
        type = 'PICF%';
        declencheur = 'date_maj_cf';
    ELSEIF param_type = 'QP' THEN
        type = 'PIQP%';
        declencheur = 'date_maj_qp';
    ELSE -- ce else ne devrait jamais être vrais mais je le laisse pour être sûr qu'on est pas dans un cas inconnu
        type = 'INCONNU';
        declencheur = 'INCONNU';
    end if;

    FOR visite IN (SELECT vv.gid_edp,
                          vv.reference,
                          vv.date_visite,
                          vv.type_visite,
                          vv.essaie_pression_static,
                          vv.essaie_pression_dynamique,
                          vv.essaie_debit
                   FROM edp.vue_visite vv
                   WHERE vv.date_visite > derniereRemontee
                     AND vv.type_visite LIKE type
                     AND vv.reference = remotePei.reference
                   ORDER BY vv.date_visite DESC)
        LOOP

            INSERT INTO edp.tracabilite_pei(reference, geometry1, type_objet,
                                            prive, precision_pei_prive, adresse, notes_localisation,
                                            diametre,
                                            modele,
                                            diametre_canalisation, etat, date_essai,
                                            essai_pression_statique,
                                            essai_pression_dynamique,
                                            essai_debit, date_derniere_visite, type_derniere_visite,
                                            date_maj_p,
                                            date_maj_e, date_maj_qp,
                                            date_maj_cf, date_maj_np, date_maj_mj, date_traca)
            VALUES (remotePei.reference, NULL, remotePei.type_objet, remotePei.prive,
                    remotePei.precision_pei_prive, remotePei.adresse, remotePei.notes_localisation,
                    remotePei.diametre,
                    remotePei.modele, remotePei.diametre_canalisation, remotePei.etat,
                    remotePei.date_essai,
                    visite.essaie_pression_static,
                    visite.essaie_pression_dynamique, visite.essaie_debit,
                    visite.date_visite,
                    visite.type_visite, remotePei.date_maj_p,
                    remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
                    remotePei.date_maj_np,
                    remotePei.date_maj_mj, current_execution)
            RETURNING id INTO tracaPeiId;


            --On récupère les motifs indispo qui ÉTAIENT présente lors de cette visite
            FOR remoteMotifIndispo IN (SELECT *
                                       FROM edp.vue_pei_motif_indispo_edp_remocra
                                       WHERE gid_edp = remotePei.gid_edp
                                         AND (date_debut <= visite.date_visite AND
                                              date_fin_prevu >= visite.date_visite)
                                         AND motif_indispo IS NOT NULL)
                LOOP
                    INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo,
                                                        date_debut,
                                                        date_fin_prevue, date_traca)
                    VALUES (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
                            remoteMotifIndispo.date_debut,
                            remoteMotifIndispo.date_fin_prevu, current_execution);
                END LOOP;


            INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
            VALUES (tracaPeiId, visite.date_visite, 'SPECIFIQUE', declencheur);
        end loop;

    if visite.date_visite > dateLaPlusRecente then
        dateLaPlusRecente = visite.date_visite;
    end if;
    RETURN dateLaPlusRecente;


END
$function$
;
