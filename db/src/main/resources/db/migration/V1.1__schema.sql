SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE SCHEMA IF NOT EXISTS edp;
SET search_path = edp, public, pg_catalog;

ALTER SCHEMA edp OWNER TO edp;

-- Connexion Oracle -> Middleware via oracle_fdw

CREATE SERVER edp FOREIGN DATA WRAPPER oracle_fdw
  OPTIONS (dbserver '//localhost:1521/XE'); -- A modifier

GRANT USAGE ON FOREIGN SERVER edp TO edp;

CREATE USER MAPPING FOR edp SERVER edp
OPTIONS (user 'READ_ONLY', password 'oracle_password'); -- A modifier

-- Tables distantes
CREATE FOREIGN TABLE edp.VUE_PEI_EDP_REMOCRA (
  gid_edp serial NOT NULL,
  reference CHARACTER VARYING(15),
  geometry1 geometry(POINTZ, 2154),
  type_objet CHARACTER VARYING(15),
  prive INTEGER,
  precision_pei_prive CHARACTER VARYING(73),
  adresse CHARACTER VARYING(107),
  notes_localisation CHARACTER VARYING(120),
  diametre INTEGER,
  modele CHARACTER VARYING(50),
  diametre_canalisation CHARACTER VARYING(4000),
  etat CHARACTER VARYING(12),
  date_maj_p TIMESTAMP WITHOUT TIME ZONE,
  date_maj_e TIMESTAMP WITHOUT TIME ZONE
) SERVER edp OPTIONS (schema 'READ_ONLY', table 'VUE_PEI_EDP_REMOCRA');

ALTER TABLE edp.VUE_PEI_EDP_REMOCRA OWNER TO edp;

CREATE FOREIGN TABLE edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA (
  gid_edp serial NOT NULL,
  reference CHARACTER VARYING(15),
  motif_indispo CHARACTER VARYING(80),
  statut_indispo CHARACTER VARYING(15),
  date_debut TIMESTAMP WITHOUT TIME ZONE,
  date_fin_prevue TIMESTAMP WITHOUT TIME ZONE
) SERVER edp OPTIONS (schema 'READ_ONLY', table 'VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA');

ALTER TABLE edp.VUE_PEI_MOTIF_INDISPO_EDP_REMOCRA OWNER TO edp;


-- Tables du middleware
CREATE TABLE edp.parametres (
  code CHARACTER VARYING PRIMARY KEY,
  valeur CHARACTER VARYING
);
ALTER TABLE edp.VUE_PEI_EDP_REMOCRA OWNER TO edp;

INSERT INTO edp.parametres(code, valeur) VALUES
('LAST_EXECUTION_CHANGE_DATA_CAPTURE', CURRENT_TIMESTAMP(0)::TIMESTAMP WITHOUT TIME ZONE);

CREATE TABLE edp.tracabilite_pei (
  id serial PRIMARY KEY,
  reference CHARACTER VARYING(15),
  geometry1 geometry(POINTZ, 2154),
  type_objet CHARACTER VARYING(15),
  prive BOOLEAN,
  precision_pei_prive CHARACTER VARYING(73),
  adresse CHARACTER VARYING(107),
  notes_localisation CHARACTER VARYING(120),
  diametre INTEGER,
  modele CHARACTER VARYING(50),
  diametre_canalisation CHARACTER VARYING(4000),
  etat CHARACTER VARYING(12),
  date_maj_p TIMESTAMP WITHOUT TIME ZONE,
  date_maj_e TIMESTAMP WITHOUT TIME ZONE,
  date_traca TIMESTAMP WITHOUT TIME ZONE
);
ALTER TABLE edp.tracabilite_pei OWNER TO edp;

CREATE TABLE edp.tracabilite_indispo (
  id serial PRIMARY KEY,
  id_traca_pei BIGINT NOT NULL,
  reference CHARACTER VARYING(15),
  motif_indispo CHARACTER VARYING,
  date_debut TIMESTAMP WITHOUT TIME ZONE,
  date_fin_prevue TIMESTAMP WITHOUT TIME ZONE,
  date_traca TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT fk_id_traca_pei FOREIGN KEY (id_traca_pei)
    REFERENCES edp.tracabilite_pei (id) MATCH SIMPLE
);
ALTER TABLE edp.tracabilite_indispo OWNER TO edp;

CREATE TABLE edp.message (
  id serial PRIMARY KEY,
  id_traca_pei BIGINT NOT NULL,
  date TIMESTAMP WITHOUT TIME ZONE,
  type CHARACTER VARYING,
  statut CHARACTER VARYING NOT NULL DEFAULT 'A TRAITER',
  synchroniser BOOLEAN,
  synchronisations integer NOT NULL DEFAULT 0,
  erreur CHARACTER VARYING DEFAULT NULL,
  CONSTRAINT fk_id_traca_pei FOREIGN KEY (id_traca_pei)
    REFERENCES edp.tracabilite_pei (id) MATCH SIMPLE
);
ALTER TABLE edp.message OWNER TO edp;
COMMENT ON COLUMN edp.message.date IS ' Date et heure du changement tel que renseigné dans la vue READ_ONLY.VUE_PEI_EDP_REMOCRA';
COMMENT ON COLUMN edp.message.type IS '"CARACTERISTIQUES" pour les données techniques, localisation, canalisation, etc., "VISITES" pour les disponibilités et les anomalies associées';
COMMENT ON COLUMN edp.message.statut IS ' A traiter : message à jouer ou à rejouer, Terminé : le message a été joué avec succès ou avec échec le type d''erreur empêche tout rejeu';
COMMENT ON COLUMN edp.message.synchroniser IS 'Indique si le message est à rejouer ou non';
COMMENT ON COLUMN edp.message.synchronisations IS 'Nombre de fois où le message a été joué ou rejoué';
COMMENT ON COLUMN edp.message.erreur IS 'Message d''erreur éventuellement rencontré';

CREATE TABLE edp.erreur (
  id serial PRIMARY KEY,
  date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  contexte CHARACTER VARYING,
  description CHARACTER VARYING,
  notifie BOOLEAN NOT NULL DEFAULT FALSE
);
ALTER TABLE edp.erreur OWNER TO edp;

-- Fonction de récupération des données du ChangeDataCapture
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

  -- Pour tous les PEI ayant bougé depuis la dernière exécution (changement de dispo uniquement pour l'instant)
  FOR remotePei IN (SELECT gid_edp, reference, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele,
	diametre_canalisation, etat, date_maj_p, date_maj_e
	FROM edp.VUE_PEI_EDP_REMOCRA
	WHERE date_maj_e > last_execution
  ) LOOP

    -- On enregiste l'état courant dans la table de traca des pei, ainsi que les motifs d'indispo (si présent)
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
	) LOOP
	  INSERT INTO edp.tracabilite_indispo(id_traca_pei, reference, motif_indispo, date_debut,
	    date_fin_prevue, date_traca) VALUES
	  (tracaPeiId, remoteMotifIndispo.reference, remoteMotifIndispo.motif_indispo, remoteMotifIndispo.date_debut,
	  remoteMotifIndispo.date_fin_prevue, current_execution);
	END LOOP;

    -- Création du/des message(s)
	INSERT INTO edp.message(id_traca_pei, date, type) VALUES
	(tracaPeiId, remotePei.date_maj_e, 'VISITES');


  END LOOP;

  UPDATE edp.parametres SET valeur = current_execution WHERE code = 'LAST_EXECUTION_CHANGE_DATA_CAPTURE';
  RETURN 0;
END
$BODY$
LANGUAGE plpgsql VOLATILE;