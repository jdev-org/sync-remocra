INSERT INTO edp.referentiel_anomalies(code_bspp, code_edp) VALUES
('EDP_INDISPO', 'EDP_INDISPO');

INSERT INTO edp.type_erreur(code, iterations, contexte, message_action, message_erreur) VALUES
('I2001', 1, 'EDP_METIER', 'Au moins un motif d''indisponibilité n''est pas valable pour un pei de ce type et de cette nature', 'Retirer le ou les motifs d''indisponibilité concernés');

CREATE OR REPLACE FUNCTION edp.changeDataCapture() RETURNS INT AS
$BODY$
DECLARE
  remotePei record;
  remoteMotifIndispo record;
  tracaPeiId integer;
  last_execution timestamp without time zone;
  current_execution timestamp without time zone;
  countIndispo integer;
BEGIN
  last_execution = TO_TIMESTAMP((SELECT valeur FROM edp.parametres WHERE code = 'LAST_EXECUTION_CHANGE_DATA_CAPTURE'),'YYYY-MM-DD HH24:MI:SS');
  current_execution = CURRENT_TIMESTAMP(0)::TIMESTAMP WITHOUT TIME ZONE;

  -- Pour tous les PEI ayant bougé depuis la dernière exécution
  FOR remotePei IN (SELECT gid_edp, reference, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele,
	diametre_canalisation, etat, date_maj_p, date_maj_e
	FROM edp.VUE_PEI_EDP_REMOCRA
	WHERE (date_maj_e > last_execution AND  date_maj_e < current_execution)
	  --OR (date_maj_p > last_execution AND date_maj_p < current_execution)
  ) LOOP

    -- On enregiste l'état courant dans la table de traca des pei
	INSERT INTO edp.tracabilite_pei(reference, geometry1, type_objet,
	  prive, precision_pei_prive, adresse, notes_localisation, diametre, modele,
	  diametre_canalisation, etat, date_maj_p, date_maj_e, date_traca) VALUES
	(remotePei.reference, NULL, remotePei.type_objet, remotePei.prive,
	 remotePei.precision_pei_prive, remotePei.adresse, remotePei.notes_localisation, remotePei.diametre,
	 remotePei.modele, remotePei.diametre_canalisation, remotePei.etat, remotePei.date_maj_p,
	 remotePei.date_maj_e, current_execution) RETURNING id INTO tracaPeiId;

	-- Récupération nombre indispo
	SELECT COUNT(*) INTO countIndispo FROM edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA
	 WHERE gid_edp = remotePei.gid_edp
       AND UPPER(statut_indispo) = 'EN COURS';
	   RAISE INFO 'PEI % statut % nbIndispo %', remotePei.reference, remotePei.etat, countIndispo;

	-- On enregistre toutes les indispos actives de ce PEI
	FOR remoteMotifIndispo IN (SELECT *
	 FROM edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA
	 WHERE gid_edp = remotePei.gid_edp
       AND UPPER(statut_indispo) = 'EN COURS'
	) LOOP
	  INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
	    date_fin_prevue, date_traca) VALUES
	  (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo, remoteMotifIndispo.date_debut,
	  remoteMotifIndispo.date_fin_prevue, current_execution);
	END LOOP;

	-- Si PEI indispo mais pas de motif d'indispo, on utilise l'indispo INDISPO_EDP
	IF UPPER(remotePei.etat) = 'INDISPONIBLE' AND countIndispo = 0 THEN
	  INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
	    date_fin_prevue, date_traca) VALUES
	  (tracaPeiId, remotePei.reference, 'EDP_INDISPO', NOW(),
	  NULL, current_execution);
	END IF;

	-- Si PEI dispo mais avec des motifs d'indispo, on déclenche une erreur

    -- Création du/des message(s)
	IF (remotePei.date_maj_e > last_execution) THEN
      INSERT INTO edp.message(id_traca_pei, date, type) VALUES
	  (tracaPeiId, remotePei.date_maj_e, 'VISITES');
	END IF;
	/*IF (remotePei.date_maj_p > last_execution) THEN
	  INSERT INTO edp.message(id_traca_pei, date, type) VALUES
	  (tracaPeiId, remotePei.date_maj_e, 'CARACTERISTIQUES');
	END IF;*/

  END LOOP;

  UPDATE edp.parametres SET valeur = current_execution WHERE code = 'LAST_EXECUTION_CHANGE_DATA_CAPTURE';
  RETURN 0;
END
$BODY$
LANGUAGE plpgsql VOLATILE;