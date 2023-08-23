CREATE TABLE edp.referentiel_hydrant_diametres(
  code_BSPP CHARACTER VARYING NOT NULL,
  code_EDP CHARACTER VARYING NOT NULL,
  PRIMARY KEY(code_BSPP, code_EDP)
);
ALTER TABLE edp.referentiel_hydrant_diametres OWNER TO edp;

INSERT INTO edp.referentiel_hydrant_diametres(code_BSPP, code_EDP) VALUES
('DIAM100', '100'),
('DIAM150', '150');

CREATE OR REPLACE FUNCTION edp.changeDataCapture() RETURNS INT AS
$BODY$
DECLARE
  remotePei record;
  remoteMotifIndispo record;
  tracaPeiId integer;
  last_execution timestamp without time zone;
  current_execution timestamp without time zone;
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

	FOR remoteMotifIndispo IN (SELECT *
	 FROM edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA
	 WHERE gid_edp = remotePei.gid_edp
       AND statut_indispo = 'EN COURS'
	) LOOP
	  INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
	    date_fin_prevue, date_traca) VALUES
	  (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo, remoteMotifIndispo.date_debut,
	  remoteMotifIndispo.date_fin_prevue, current_execution);
	END LOOP;

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

UPDATE edp.type_erreur SET message_action = 'Vérifier que le serveur Oracle WatGIS est opérationnel et vérifier le paramétrage de l''application Synchronisation REMOCRA selon le processus défini dans le manuel de paramétrage',message_erreur = 'La connexion à la base de données WatGIS (Oracle) est impossible ',contexte = 'EDP_SYSTEME' WHERE code = '0001';
UPDATE edp.type_erreur SET message_action = 'Vérifier que le serveur PostgreSQL Synchronisation REMOCRA est opérationnel et vérifier le paramétrage de l''application Synchronisation EDP selon le processus défini dans le manuel de paramétrage',message_erreur = 'La connexion à la base de données EDP Synchronisation REMOCRA (PostgreSQL)  est impossible',contexte = 'EDP_SYSTEME' WHERE code = '0002';
UPDATE edp.type_erreur SET message_action = 'Vérifier que le serveur REMOCRA est opérationnel (demande au prestatataire en charge de l''hébergement de REMOCRA) et que son accès est autorisé par EDP (firewall EDP, etc.)',message_erreur = 'Le serveur REMOCRA est injoignable',contexte = 'EDP_SYSTEME' WHERE code = '0003';
UPDATE edp.type_erreur SET message_action = 'Supprimer le PEI dans la base de données WatGIS',message_erreur = 'Le PEI référencé ne correspond à aucun hydrant présent en base de données REMOCRA',contexte = 'EDP_METIER' WHERE code = '1000';
UPDATE edp.type_erreur SET message_action = 'Demander à la ville de Paris d''affecter sur REMOCRA et pour le PEI référencé EDP comme : Service des eaux ou Service chargé de la "Maintenance et CTP"',message_erreur = 'Le PEI référencé existe en base de données REMOCRA mais la ville de Paris ne vous a pas accordé les droits sur ce PEI',contexte = 'EDP_METIER' WHERE code = '1300';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur du diamètre renseigné EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1001';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur de marque renseignée EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1002';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur du modèle renseigné EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1003';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur de la nature (PI, BI ou PA) renseignée EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1004';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur du type de canalisation renseigné EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1005';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à l''analyse des logs applicatifs par le prestataire en charge de l''hébergement de REMOCRA en fournissant les dates et heures de l''opération réalisée dans WatGIS',message_erreur = 'Une erreur dont l''origine est inconnue s''est produite',contexte = 'EDP_METIER' WHERE code = '1301';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur du type de matériau renseigné EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '1006';
UPDATE edp.type_erreur SET message_action = 'Demander à la ville de Paris de procéder au rapprochement géographique des deux PEI via l''interface applicative REMOCRA pour permettre un jumelage et de s''assurer que les PEI sont bien des BI',message_erreur = 'Le jumelage entre les deux hydrants référencés n''est pas possible. La distance entre les deux hydrants doit être inféreure à 25 mètres, et les hydrants doivent être de nature BI',contexte = 'EDP_METIER' WHERE code = '1100';
UPDATE edp.type_erreur SET message_action = 'Supprimer le PEI dans la base de données WatGIS',message_erreur = 'Le PEI référencé dans le jumelage ne correspond à aucun hydrant présent en base de données REMOCRA',contexte = 'EDP_METIER' WHERE code = '1007';
UPDATE edp.type_erreur SET message_action = 'Vérifier la date dans WatGIS',message_erreur = 'La date de changement d''état n''existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm',contexte = 'EDP_METIER' WHERE code = '2000';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2001';
UPDATE edp.type_erreur SET message_action = 'Vérifier dans WatGIS l''historique du changement d''état pour ce PEI et faire procéder si nécessaire à la mise à jour manuelle dans REMOCRA',message_erreur = 'Un changement d''état a déjà été réalisé dans REMOCRA pour cette date et cette heure pour ce PEI',contexte = 'EDP_METIER' WHERE code = '2100';
UPDATE edp.type_erreur SET message_action = 'Demander à la ville de Paris de procéder sur ce PEI à la saisie d''une visite de récéption et d''informer la BSPP de la nécessité de procéder à une reconnaissance opérationelle initiale',message_erreur = 'Le PEI n''est pas en mesure de recevoir ce changement d''état. Une visite de réception et une visite de reconnaissance opérationnelle initiale doivent être préalablement renseignées dans REMOCRA',contexte = 'EDP_METIER' WHERE code = '2101';
UPDATE edp.type_erreur SET message_action = 'Demander à la ville de Paris d''informer la BSPP de la nécessité de procéder à une reconnaissance opérationale initiale sur le PEI référencé',message_erreur = 'Le PEI n''est pas en mesure de recevoir ce changement d''état. Une visite de réception et une visite de reconnaissance opérationnelle initiale doivent être préalablement renseignées dans REMOCRA',contexte = 'EDP_METIER' WHERE code = '2102';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2103';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à la mise à jour de la table de correspondance au sein de la base de données EDP Synchronisation REMOCRA',message_erreur = 'La valeur de motif renseigné EDP n''existe pas dans REMOCRA.',contexte = 'EDP_METIER' WHERE code = '2002';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2104';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'Le débit ne peut être inférieur à 0',contexte = 'EDP_METIER' WHERE code = '2105';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'Le débit maximum ne peut être inférieur à 0',contexte = 'EDP_METIER' WHERE code = '2106';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'La pression ne peut être inférieure à 0',contexte = 'EDP_METIER' WHERE code = '2107';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'La pression dynamique ne peut être inférieure à 0',contexte = 'EDP_METIER' WHERE code = '2108';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'La pression dynamique au débit maximum ne peut être inférieure à 0',contexte = 'EDP_METIER' WHERE code = '2109';
UPDATE edp.type_erreur SET message_action = 'Faire procéder à l''analyse des logs applicatifs par le prestataire en charge de l''hébergement de REMOCRA en fournissant les dates et heures de l''opération réalisée dans WatGIS',message_erreur = 'Une erreur dont l''origine est inconnue s''est produite',contexte = 'EDP_METIER' WHERE code = '2300';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2003';
UPDATE edp.type_erreur SET message_action = 'Vérifier le paramétrage de la clef d''API selon le processus défini dans le manuel de paramétrage et faire procéder, si nécessaire à l''attribution des droits dans REMOCRA par la ville de Paris',message_erreur = 'Le serveur REMOCRA n''autorise pas le système de synchronisation REMOCRA d''EDP à se connecter pour synchroniser les données',contexte = 'EDP_SYSTEME' WHERE code = '0200';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2200';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2201';
UPDATE edp.type_erreur SET message_action = NULL,message_erreur = NULL,contexte = 'EDP_SYSTEME' WHERE code = '2110';
UPDATE edp.type_erreur SET message_action = 'Modifier la valeur dans WatGIS',message_erreur = 'Un PEI est indiqué comme disponible dans WatGIS alors qu''au moins un motif est renseigné',contexte = 'EDP_METIER' WHERE code = '1008';