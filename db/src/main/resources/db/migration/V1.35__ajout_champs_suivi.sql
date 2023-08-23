ALTER TABLE edp.message
ADD COLUMN json_creation_visite CHARACTER VARYING,
ADD COLUMN declencheur CHARACTER VARYING;

CREATE TABLE edp.motif_indispo_actif(
  reference CHARACTER VARYING,
  motif CHARACTER VARYING
);

CREATE OR REPLACE FUNCTION edp.changedatacapture(
	)
    RETURNS integer
    LANGUAGE 'plpgsql'
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
BEGIN
  derniereRemontee = TO_TIMESTAMP((SELECT valeur FROM edp.parametres WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE'),'YYYY-MM-DD HH24:MI:SS');
  remonteeActuelle = derniereRemontee;
  current_execution = CURRENT_TIMESTAMP(0)::TIMESTAMP WITHOUT TIME ZONE;

  -- Pour tous les PEI ayant bougé depuis la dernière exécution
  FOR remotePei IN (SELECT gid_edp, reference, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele,
	diametre_canalisation, etat, date_essai, essai_pression_statique, essai_pression_dynamique, essai_debit, date_derniere_visite, type_derniere_visite,
	date_maj_p, date_maj_e, date_maj_qp, date_maj_cf, date_maj_np, date_maj_mj
	FROM edp.VUE_PEI_EDP_REMOCRA
	WHERE (date_maj_e > derniereRemontee)
	  OR (date_maj_p > derniereRemontee)
	  OR (date_maj_qp > derniereRemontee)
	  OR (date_maj_cf > derniereRemontee)
	  OR (date_maj_np > derniereRemontee)
	  OR (date_maj_mj > derniereRemontee)
  ) LOOP

  messageRecord_statut = 'A TRAITER';
  messageRecord_erreur = null;
	messageRecord_synchroniser = null;

	-- Si c'est le message le plus récent remonté, on enregistre l'info
	IF remotePei.date_maj_e > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_e;
	ELSIF remotePei.date_maj_p > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_p;
	ELSIF remotePei.date_maj_qp > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_qp;
	ELSIF remotePei.date_maj_cf > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_cf;
	ELSIF remotePei.date_maj_np > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_np;
	ELSIF remotePei.date_maj_mj > remonteeActuelle THEN
	  remonteeActuelle = remotePei.date_maj_mj;
	END IF;

	IF remotePei.reference IS NULL THEN
		INSERT INTO edp.erreur(description, type_erreur, message)
        SELECT message_erreur, id, null
        FROM edp.type_erreur
	    WHERE code = 'I1002';
	ELSE
		-- On enregiste l'état courant dans la table de traca des pei
		INSERT INTO edp.tracabilite_pei(reference, geometry1, type_objet,
		  prive, precision_pei_prive, adresse, notes_localisation, diametre, modele,
		  diametre_canalisation, etat, date_essai, essai_pression_statique, essai_pression_dynamique,
		  essai_debit, date_derniere_visite, type_derniere_visite, date_maj_p, date_maj_e, date_maj_qp,
	 	  date_maj_cf, date_maj_np, date_maj_mj, date_traca) VALUES
		(remotePei.reference, NULL, remotePei.type_objet, remotePei.prive,
		 remotePei.precision_pei_prive, remotePei.adresse, remotePei.notes_localisation, remotePei.diametre,
		 remotePei.modele, remotePei.diametre_canalisation, remotePei.etat, remotePei.date_essai, remotePei.essai_pression_statique,
		 remotePei.essai_pression_dynamique, remotePei.essai_debit, remotePei.date_derniere_visite, remotePei.type_derniere_visite, remotePei.date_maj_p,
		 remotePei.date_maj_e, remotePei.date_maj_qp, remotePei.date_maj_cf, remotePei.date_maj_np, remotePei.date_maj_mj, current_execution) RETURNING id INTO tracaPeiId;

		-- Récupération nombre indispo
		SELECT COUNT(*) INTO countIndispo FROM edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA
		 WHERE gid_edp = remotePei.gid_edp
		   AND UPPER(statut_indispo) = 'EN COURS'
		   AND motif_indispo IS NOT NULL;

		-- On enregistre toutes les indispos actives de ce PEI
		FOR remoteMotifIndispo IN (SELECT *
		 FROM edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA
		 WHERE gid_edp = remotePei.gid_edp
		   AND UPPER(statut_indispo) = 'EN COURS'
		   AND motif_indispo IS NOT NULL
		) LOOP
		  INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
			date_fin_prevue, date_traca) VALUES
		  (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo, remoteMotifIndispo.date_debut,
		  remoteMotifIndispo.date_fin_prevue, current_execution);
		END LOOP;

		-- Création du/des message(s)
		IF (remotePei.date_maj_e > derniereRemontee) THEN
		  INSERT INTO edp.message(id_traca_pei, date, type, statut, erreur, synchroniser, declencheur) VALUES
		  (tracaPeiId, remotePei.date_maj_e, 'INDISPO_TEMPORAIRE', messageRecord_statut, messageRecord_erreur, messageRecord_synchroniser, 'date_maj_e') RETURNING id into messageRecordId;
		END IF;
		IF (remotePei.date_maj_p > derniereRemontee) THEN
		  INSERT INTO edp.message(id_traca_pei, date, type, declencheur) VALUES
		  (tracaPeiId, remotePei.date_maj_p, 'CARACTERISTIQUES', 'date_maj_p');
		END IF;
		IF (remotePei.date_maj_qp > derniereRemontee) THEN
		  INSERT INTO edp.message(id_traca_pei, date, type, declencheur) VALUES
		  (tracaPeiId, remotePei.date_maj_qp, 'SPECIFIQUE', 'date_maj_qp');
		END IF;
		IF (remotePei.date_maj_cf > derniereRemontee) THEN
		  INSERT INTO edp.message(id_traca_pei, date, type, declencheur) VALUES
		  (tracaPeiId, remotePei.date_maj_cf, 'SPECIFIQUE', 'date_maj_cf');
		END IF;
		IF (remotePei.date_maj_np > derniereRemontee) THEN
		  INSERT INTO edp.message(id_traca_pei, date, type, declencheur) VALUES
		  (tracaPeiId, remotePei.date_maj_np, 'SPECIFIQUE', 'date_maj_np');
		END IF;
		IF (remotePei.date_maj_mj > derniereRemontee) THEN
      INSERT INTO edp.message(id_traca_pei, date, type, declencheur) VALUES
      (tracaPeiId, remotePei.date_maj_mj, 'MANUELLE', 'date_maj_mj');
    END IF;

	END IF; -- IF reference nulle

  END LOOP;

  UPDATE edp.parametres SET valeur = remonteeActuelle WHERE code = 'DERNIERE_REMONTEE_CHANGE_DATA_CAPTURE';
  RETURN 0;
END
$BODY$;

ALTER FUNCTION edp.changedatacapture()
    OWNER TO edp;