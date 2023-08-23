-- Tables distantes
DROP FOREIGN TABLE IF EXISTS edp.VUE_VISITE;
CREATE FOREIGN TABLE edp.VUE_VISITE (
    gid_edp serial NOT NULL,
    reference CHARACTER VARYING,
    date_visite TIMESTAMP WITHOUT TIME ZONE,
    type_visite CHARACTER VARYING,
    essaie_pression_static INTEGER,
    essaie_pression_dynamique INTEGER,
    essaie_debit INTEGER
    ) SERVER edp OPTIONS (schema 'READ_ONLY', table 'VUE_PEI_VISITES');
ALTER TABLE edp.VUE_VISITE
    OWNER TO edp;



CREATE OR REPLACE FUNCTION edp.changedatacapture(
)
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS
$BODY$
DECLARE
    remotePei                  record;
    remoteMotifIndispo         record;
    visite                     record;
    tracaPeiId                 integer;
    current_execution          timestamp without time zone;
    messageRecord_statut       character varying;
    messageRecord_erreur       character varying;
    messageRecord_synchroniser boolean;
    messageRecordId            bigint;
    derniereRemontee           timestamp without time zone;
    dateLaPlusRecente          timestamp without time zone;

BEGIN
    derniereRemontee =
            TO_TIMESTAMP((SELECT valeur FROM edp.parametres WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE'),
                         'YYYY-MM-DD HH24:MI:SS');

    dateLaPlusRecente = derniereRemontee;
    current_execution = CURRENT_TIMESTAMP(0)::TIMESTAMP WITHOUT TIME ZONE;

    -- Pour tous les PEI ayant bougé depuis la dernière exécution
    FOR remotePei IN (SELECT gid_edp,
                             reference,
                             type_objet,
                             prive,
                             precision_pei_prive,
                             adresse,
                             notes_localisation,
                             diametre,
                             modele,
                             diametre_canalisation,
                             etat,
                             date_essai,
                             essai_pression_statique,
                             essai_pression_dynamique,
                             essai_debit,
                             date_derniere_visite,
                             type_derniere_visite,
                             date_maj_p,
                             date_maj_e,
                             date_maj_qp,
                             date_maj_cf,
                             date_maj_np,
                             date_maj_mj
                      FROM edp.VUE_PEI_EDP_REMOCRA
                      WHERE (date_maj_e > derniereRemontee)
                         OR (date_maj_p > derniereRemontee)
                         OR (date_maj_qp > derniereRemontee)
                         OR (date_maj_cf > derniereRemontee)
                         OR (date_maj_np > derniereRemontee)
                         OR (date_maj_mj > derniereRemontee))
        LOOP

            messageRecord_statut = 'A TRAITER';
            messageRecord_erreur = null;
            messageRecord_synchroniser = null;

            IF remotePei.reference IS NULL THEN
                INSERT INTO edp.erreur(description, type_erreur, message)
                SELECT message_erreur, id, null
                FROM edp.type_erreur
                WHERE code = 'I1002';
            ELSE


--On traite les message QP

                IF remotePei.date_maj_qp > derniereRemontee THEN

                    FOR visite IN (SELECT vv.gid_edp,
                                          vv.reference,
                                          vv.date_visite,
                                          vv.type_visite,
                                          vv.essaie_pression_static,
                                          vv.essaie_pression_dynamique,
                                          vv.essaie_debit
                                   FROM edp.vue_visite vv
                                   WHERE vv.date_visite > derniereRemontee
                                     AND vv.type_visite LIKE 'PIQP%'
                                     AND vv.reference = remotePei.reference
                                   ORDER BY vv.date_visite DESC)
                        LOOP
                            --Cette traca dois avoir pour infos les infos de la visite en cours de traitement
                            remotePei.type_derniere_visite = visite.type_visite;
                            remotePei.date_derniere_visite = visite.date_visite;
                            remotePei.essai_debit = visite.essaie_debit;
                            remotePei.essai_pression_dynamique = visite.essaie_pression_dynamique;
                            remotePei.essai_pression_statique = visite.essaie_pression_static;

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
                                    remotePei.essai_pression_statique,
                                    remotePei.essai_pression_dynamique, remotePei.essai_debit,
                                    remotePei.date_derniere_visite,
                                    remotePei.type_derniere_visite, remotePei.date_maj_p,
                                    remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
                                    remotePei.date_maj_np,
                                    remotePei.date_maj_mj, current_execution)
                            RETURNING id INTO tracaPeiId;

                            --On récupère les motifs indispo qui ÉTAIENT présente lors de cette visite
                            FOR remoteMotifIndispo IN (SELECT *
                                                       FROM edp.vue_pei_motif_indispo
                                                       WHERE gid_edp = remotePei.gid_edp
                                                         AND (date_debut <= visite.date_visite AND
                                                              date_fin_prevue >= visite.date_visite)
                                                         AND motif_indispo IS NOT NULL)
                                LOOP
                                    INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo,
                                                                        date_debut,
                                                                        date_fin_prevue, date_traca)
                                    VALUES (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
                                            remoteMotifIndispo.date_debut,
                                            remoteMotifIndispo.date_fin_prevue, current_execution);
                                END LOOP;


                            INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
                            VALUES (tracaPeiId, visite.date_visite, 'SPECIFIQUE', 'date_maj_qp');
                        end loop;

                    if visite.date_visite > dateLaPlusRecente then
                        dateLaPlusRecente = visite.date_visite;
                    end if;
                END IF;


--On traite les message NP

                IF remotePei.date_maj_np > derniereRemontee THEN

                    FOR visite IN (SELECT vv.gid_edp,
                                          vv.reference,
                                          vv.date_visite,
                                          vv.type_visite,
                                          vv.essaie_pression_static,
                                          vv.essaie_pression_dynamique,
                                          vv.essaie_debit
                                   FROM edp.vue_visite vv
                                   WHERE vv.date_visite > derniereRemontee
                                     AND vv.type_visite LIKE 'NP%'
                                     AND vv.reference = remotePei.reference
                                   ORDER BY vv.date_visite DESC)
                        LOOP
                            --Cette traca dois avoir pour infos les infos de la visite en cours de traitement
                            remotePei.type_derniere_visite = visite.type_visite;
                            remotePei.date_derniere_visite = visite.date_visite;
                            remotePei.essai_debit = visite.essaie_debit;
                            remotePei.essai_pression_dynamique = visite.essaie_pression_dynamique;
                            remotePei.essai_pression_statique = visite.essaie_pression_static;


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
                                    remotePei.essai_pression_statique,
                                    remotePei.essai_pression_dynamique, remotePei.essai_debit,
                                    remotePei.date_derniere_visite,
                                    remotePei.type_derniere_visite, remotePei.date_maj_p,
                                    remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
                                    remotePei.date_maj_np,
                                    remotePei.date_maj_mj, current_execution)
                            RETURNING id INTO tracaPeiId;


                            --On récupère les motifs indispo qui ÉTAIENT présente lors de cette visite
                            FOR remoteMotifIndispo IN (SELECT *
                                                       FROM edp.vue_pei_motif_indispo
                                                       WHERE gid_edp = remotePei.gid_edp
                                                         AND (date_debut <= visite.date_visite AND
                                                              date_fin_prevue >= visite.date_visite)
                                                         AND motif_indispo IS NOT NULL)
                                LOOP
                                    INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo,
                                                                        date_debut,
                                                                        date_fin_prevue, date_traca)
                                    VALUES (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
                                            remoteMotifIndispo.date_debut,
                                            remoteMotifIndispo.date_fin_prevue, current_execution);
                                END LOOP;


                            INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
                            VALUES (tracaPeiId, visite.date_visite, 'SPECIFIQUE', 'date_maj_np');
                        end loop;

                    if visite.date_visite > dateLaPlusRecente then
                        dateLaPlusRecente = visite.date_visite;
                    end if;
                END IF;

--On traite les message CF

                IF remotePei.date_maj_cf > derniereRemontee THEN

                    FOR visite IN (SELECT vv.gid_edp,
                                          vv.reference,
                                          vv.date_visite,
                                          vv.type_visite,
                                          vv.essaie_pression_static,
                                          vv.essaie_pression_dynamique,
                                          vv.essaie_debit
                                   FROM edp.vue_visite vv
                                   WHERE vv.date_visite > derniereRemontee
                                     AND vv.type_visite LIKE 'PICF%'
                                     AND vv.reference = remotePei.reference
                                   ORDER BY vv.date_visite DESC)
                        LOOP
                            --Cette traca dois avoir pour infos les infos de la visite en cours de traitement
                            remotePei.type_derniere_visite = visite.type_visite;
                            remotePei.date_derniere_visite = visite.date_visite;
                            remotePei.essai_debit = visite.essaie_debit;
                            remotePei.essai_pression_dynamique = visite.essaie_pression_dynamique;
                            remotePei.essai_pression_statique = visite.essaie_pression_static;


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
                                    remotePei.essai_pression_statique,
                                    remotePei.essai_pression_dynamique, remotePei.essai_debit,
                                    remotePei.date_derniere_visite,
                                    remotePei.type_derniere_visite, remotePei.date_maj_p,
                                    remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
                                    remotePei.date_maj_np,
                                    remotePei.date_maj_mj, current_execution)
                            RETURNING id INTO tracaPeiId;


                            --On récupère les motifs indispo qui ÉTAIENT présente lors de cette visite
                            FOR remoteMotifIndispo IN (SELECT *
                                                       FROM edp.vue_pei_motif_indispo
                                                       WHERE gid_edp = remotePei.gid_edp
                                                         AND (date_debut <= visite.date_visite AND
                                                              date_fin_prevue >= visite.date_visite)
                                                         AND motif_indispo IS NOT NULL)
                                LOOP
                                    INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo,
                                                                        date_debut,
                                                                        date_fin_prevue, date_traca)
                                    VALUES (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
                                            remoteMotifIndispo.date_debut,
                                            remoteMotifIndispo.date_fin_prevue, current_execution);
                                END LOOP;


                            INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
                            VALUES (tracaPeiId, visite.date_visite, 'SPECIFIQUE', 'date_maj_cf');
                        end loop;

                    if visite.date_visite > dateLaPlusRecente then
                        dateLaPlusRecente = visite.date_visite;
                    end if;
                END IF;


--Une traca pour les autres déclencheurs
                if (remotePei.date_maj_e > derniereRemontee
                    OR remotePei.date_maj_p > derniereRemontee
                    OR remotePei.date_maj_mj > derniereRemontee
                    ) THEN
                    -- Traitement pour chaque déclencheur superieure a la remonté actuel

                    -- On enregiste l'état a la date de la remonté dans la table de traca des pei

                    INSERT INTO edp.tracabilite_pei(reference, geometry1, type_objet,
                                                    prive, precision_pei_prive, adresse, notes_localisation, diametre,
                                                    modele,
                                                    diametre_canalisation, etat, date_essai, essai_pression_statique,
                                                    essai_pression_dynamique,
                                                    essai_debit, date_derniere_visite, type_derniere_visite, date_maj_p,
                                                    date_maj_e, date_maj_qp,
                                                    date_maj_cf, date_maj_np, date_maj_mj, date_traca)
                    VALUES (remotePei.reference, NULL, remotePei.type_objet, remotePei.prive,
                            remotePei.precision_pei_prive, remotePei.adresse, remotePei.notes_localisation,
                            remotePei.diametre,
                            remotePei.modele, remotePei.diametre_canalisation, remotePei.etat, remotePei.date_essai,
                            remotePei.essai_pression_statique,
                            remotePei.essai_pression_dynamique, remotePei.essai_debit, remotePei.date_derniere_visite,
                            remotePei.type_derniere_visite, remotePei.date_maj_p,
                            remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf, remotePei.date_maj_np,
                            remotePei.date_maj_mj, current_execution)


                    RETURNING id INTO tracaPeiId;


                    --On récupère les motifs indispo qui ÉTAIENT présente lors de cette remontée
                    FOR remoteMotifIndispo IN (SELECT *
                                               FROM edp.vue_pei_motif_indispo
                                               WHERE gid_edp = remotePei.gid_edp
                                                 AND (date_debut < derniereRemontee AND date_fin_prevue > derniereRemontee)
                                                 AND motif_indispo IS NOT NULL)
                        LOOP
                            INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
                                                                date_fin_prevue, date_traca)
                            VALUES (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
                                    remoteMotifIndispo.date_debut,
                                    remoteMotifIndispo.date_fin_prevue, current_execution);
                        END LOOP;

--On traite les dates maj_e
                    IF remotePei.date_maj_e > derniereRemontee THEN


                        --Message
                        INSERT INTO edp.message(id_traca_pei, date, type, statut, erreur, synchroniser, declencheur)
                        VALUES (tracaPeiId, remotePei.date_maj_e, 'INDISPO_TEMPORAIRE', messageRecord_statut,
                                messageRecord_erreur, messageRecord_synchroniser, 'date_maj_e')
                        RETURNING id into messageRecordId;


                        --on enregistre la dernière remontée pour l'utiliser a la fin
                        IF (dateLaPlusRecente < remotePei.date_maj_e) THEN
                            dateLaPlusRecente = remotePei.date_maj_e;
                        END IF;
                    END IF;


-- si date_maj_p > alors on modifie juste les données patrimoniales
                    IF remotePei.date_maj_p > derniereRemontee THEN


                        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
                        VALUES (tracaPeiId, remotePei.date_maj_p, 'CARACTERISTIQUES', 'date_maj_p');

                        IF (dateLaPlusRecente < remotePei.date_maj_p) THEN
                            dateLaPlusRecente = remotePei.date_maj_p;
                        END IF;

                    END IF;

-- ON TRAITE LES date_maj_mj
                    IF remotePei.date_maj_mj > derniereRemontee THEN

                        -- On enregiste l'état a la date de la remonté dans la table de traca des pei

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
                                remotePei.modele, remotePei.diametre_canalisation, remotePei.etat, remotePei.date_essai,
                                remotePei.essai_pression_statique,
                                remotePei.essai_pression_dynamique, remotePei.essai_debit,
                                remotePei.date_derniere_visite,
                                remotePei.type_derniere_visite, remotePei.date_maj_p,
                                remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
                                remotePei.date_maj_np,
                                remotePei.date_maj_mj, current_execution)
                        RETURNING id INTO tracaPeiId;

                        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
                        VALUES (tracaPeiId, remotePei.date_maj_mj, 'MANUELLE', 'date_maj_mj');

                        IF (dateLaPlusRecente < remotePei.date_maj_mj) THEN
                            dateLaPlusRecente = remotePei.date_maj_mj;
                        END IF;

                    END IF; -- IF sdate maj mj
                END IF; --IF les autres déclencheur
            END IF; -- IF reference nulle

        END LOOP;

    UPDATE edp.parametres SET valeur = dateLaPlusRecente WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE';
    RETURN 0;
END
$BODY$;

ALTER FUNCTION edp.changedatacapture()
    OWNER TO edp;