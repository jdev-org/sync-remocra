ALTER FOREIGN TABLE edp.vue_pei_edp_remocra
ADD COLUMN date_maj_ae TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE edp.tracabilite_pei
ADD COLUMN date_maj_ae TIMESTAMP WITHOUT TIME ZONE;

CREATE OR REPLACE FUNCTION edp.changedatacapture()
RETURNS integer
LANGUAGE plpgsql
COST 100
VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
  remotePei record;
  remoteMotifIndispo record;
  tracaPeiId integer;
  current_execution timestamp without time zone;
  countIndispo integer;
  messageRecord_statut character varying;
  messageRecord_erreur character varying;
  messageRecord_synchroniser boolean;
  messageRecordId bigint;
  derniereRemontee timestamp without time zone;
  remonteeActuelle timestamp without time zone;
  messageDate timestamp without time zone;
  messageDeclencheur character varying;
BEGIN
  derniereRemontee = TO_TIMESTAMP(
    (SELECT valeur FROM edp.parametres WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE'),
    'YYYY-MM-DD HH24:MI:SS');
  remonteeActuelle = derniereRemontee;
  current_execution = CURRENT_TIMESTAMP(0)::TIMESTAMP WITHOUT TIME ZONE;

  FOR remotePei IN (
    SELECT gid_edp, reference, type_objet, prive, precision_pei_prive, adresse, notes_localisation,
      diametre, modele, diametre_canalisation, etat, date_essai, essai_pression_statique,
      essai_pression_dynamique, essai_debit, date_derniere_visite, type_derniere_visite,
      date_maj_p, date_maj_e, date_maj_qp, date_maj_cf, date_maj_np, date_maj_mj, date_maj_ae
    FROM edp.vue_pei_edp_remocra
    WHERE date_maj_e > derniereRemontee
      OR date_maj_p > derniereRemontee
      OR date_maj_qp > derniereRemontee
      OR date_maj_cf > derniereRemontee
      OR date_maj_np > derniereRemontee
      OR date_maj_mj > derniereRemontee
      OR date_maj_ae > derniereRemontee
  ) LOOP
    messageRecord_statut = 'A TRAITER';
    messageRecord_erreur = null;
    messageRecord_synchroniser = null;

    IF remotePei.date_maj_e IS NOT NULL AND remotePei.date_maj_e > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_e;
    END IF;
    IF remotePei.date_maj_p IS NOT NULL AND remotePei.date_maj_p > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_p;
    END IF;
    IF remotePei.date_maj_qp IS NOT NULL AND remotePei.date_maj_qp > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_qp;
    END IF;
    IF remotePei.date_maj_cf IS NOT NULL AND remotePei.date_maj_cf > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_cf;
    END IF;
    IF remotePei.date_maj_np IS NOT NULL AND remotePei.date_maj_np > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_np;
    END IF;
    IF remotePei.date_maj_mj IS NOT NULL AND remotePei.date_maj_mj > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_mj;
    END IF;
    IF remotePei.date_maj_ae IS NOT NULL AND remotePei.date_maj_ae > remonteeActuelle THEN
      remonteeActuelle = remotePei.date_maj_ae;
    END IF;

    IF remotePei.reference IS NULL THEN
      INSERT INTO edp.erreur(description, type_erreur, message)
      SELECT message_erreur, id, null
      FROM edp.type_erreur
      WHERE code = 'I1002';
    ELSE
      INSERT INTO edp.tracabilite_pei(
        reference, geometry1, type_objet, prive, precision_pei_prive, adresse,
        notes_localisation, diametre, modele, diametre_canalisation, etat, date_essai,
        essai_pression_statique, essai_pression_dynamique, essai_debit, date_derniere_visite,
        type_derniere_visite, date_maj_p, date_maj_e, date_maj_qp, date_maj_cf, date_maj_np,
        date_maj_mj, date_maj_ae, date_traca)
      VALUES (
        remotePei.reference, NULL, remotePei.type_objet, remotePei.prive,
        remotePei.precision_pei_prive, remotePei.adresse, remotePei.notes_localisation,
        remotePei.diametre, remotePei.modele, remotePei.diametre_canalisation, remotePei.etat,
        remotePei.date_essai, remotePei.essai_pression_statique, remotePei.essai_pression_dynamique,
        remotePei.essai_debit, remotePei.date_derniere_visite, remotePei.type_derniere_visite,
        remotePei.date_maj_p, remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf,
        remotePei.date_maj_np, remotePei.date_maj_mj, remotePei.date_maj_ae, current_execution)
      RETURNING id INTO tracaPeiId;

      SELECT COUNT(*) INTO countIndispo
      FROM edp.vue_pei_motif_indispo_edp_remocra
      WHERE gid_edp = remotePei.gid_edp
        AND UPPER(statut_indispo) = 'EN COURS'
        AND motif_indispo IS NOT NULL;

      FOR remoteMotifIndispo IN (
        SELECT *
        FROM edp.vue_pei_motif_indispo_edp_remocra
        WHERE gid_edp = remotePei.gid_edp
          AND UPPER(statut_indispo) = 'EN COURS'
          AND motif_indispo IS NOT NULL
      ) LOOP
        INSERT INTO edp.tracabilite_indispo(
          id_traca_pei, reference, motif_indispo, date_debut, date_fin_prevue, date_traca)
        VALUES (
          tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo,
          remoteMotifIndispo.date_debut, remoteMotifIndispo.date_fin_prevue, current_execution);
      END LOOP;

      IF UPPER(remotePei.etat) = 'INDISPONIBLE' AND countIndispo = 0 THEN
        INSERT INTO edp.tracabilite_indispo(
          id_traca_pei, reference, motif_indispo, date_debut, date_fin_prevue, date_traca)
        VALUES (tracaPeiId, remotePei.reference, 'EDP_INDISPO', NOW(), NULL, current_execution);
      END IF;

      IF UPPER(remotePei.etat) = 'DISPONIBLE' AND countIndispo > 0 THEN
        messageRecord_statut = 'EN ERREUR';
        messageRecord_synchroniser = false;
        SELECT message_erreur INTO messageRecord_erreur
        FROM edp.type_erreur
        WHERE code = 'I1008';
      END IF;

      IF remotePei.date_maj_e > derniereRemontee
         OR remotePei.date_maj_ae > derniereRemontee THEN
        IF remotePei.date_maj_ae IS NOT NULL
           AND (remotePei.date_maj_e IS NULL OR remotePei.date_maj_ae > remotePei.date_maj_e) THEN
          messageDate = remotePei.date_maj_ae;
          messageDeclencheur = 'date_maj_ae';
        ELSE
          messageDate = remotePei.date_maj_e;
          messageDeclencheur = 'date_maj_e';
        END IF;
        INSERT INTO edp.message(
          id_traca_pei, date, type, statut, erreur, synchroniser, declencheur)
        VALUES (
          tracaPeiId, messageDate, 'INDISPO_TEMPORAIRE', messageRecord_statut,
          messageRecord_erreur, messageRecord_synchroniser, messageDeclencheur)
        RETURNING id INTO messageRecordId;
      END IF;
      IF remotePei.date_maj_p > derniereRemontee THEN
        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
        VALUES (tracaPeiId, remotePei.date_maj_p, 'CARACTERISTIQUES', 'date_maj_p');
      END IF;
      IF remotePei.date_maj_qp > derniereRemontee THEN
        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
        VALUES (tracaPeiId, remotePei.date_maj_qp, 'SPECIFIQUE', 'date_maj_qp');
      END IF;
      IF remotePei.date_maj_cf > derniereRemontee THEN
        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
        VALUES (tracaPeiId, remotePei.date_maj_cf, 'SPECIFIQUE', 'date_maj_cf');
      END IF;
      IF remotePei.date_maj_np > derniereRemontee THEN
        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
        VALUES (tracaPeiId, remotePei.date_maj_np, 'SPECIFIQUE', 'date_maj_np');
      END IF;
      IF remotePei.date_maj_mj > derniereRemontee THEN
        INSERT INTO edp.message(id_traca_pei, date, type, declencheur)
        VALUES (tracaPeiId, remotePei.date_maj_mj, 'MANUELLE', 'date_maj_mj');
      END IF;
    END IF;
  END LOOP;

  UPDATE edp.parametres
  SET valeur = remonteeActuelle
  WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE';
  RETURN 0;
END
$BODY$;

ALTER FUNCTION edp.changedatacapture() OWNER TO edp;
