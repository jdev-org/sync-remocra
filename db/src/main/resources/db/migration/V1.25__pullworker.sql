CREATE TABLE edp.pull_message(
    id SERIAL PRIMARY KEY,
    date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    date_modification TIMESTAMP WITHOUT TIME ZONE,
    operation CHARACTER VARYING,
    type CHARACTER VARYING,
    hydrant CHARACTER VARYING
);
ALTER TABLE edp.pull_message OWNER TO EDP;

CREATE TABLE edp.pull_hydrant(
    id SERIAL PRIMARY KEY,
    numero CHARACTER VARYING NOT NULL,
    date_modification TIMESTAMP WITHOUT TIME ZONE,
    auteur_modification CHARACTER VARYING,
    diametre CHARACTER VARYING,
    marque CHARACTER VARYING,
    modele CHARACTER VARYING,
    diametre_canalisation INTEGER,
    annee_fabrication CHARACTER VARYING,
    dispo_terrestre CHARACTER VARYING,
    dispo_hbe CHARACTER VARYING,
    numero_voie INTEGER,
    suffixe_voie CHARACTER VARYING,
    niveau CHARACTER VARYING,
    voie CHARACTER VARYING,
    voie2 CHARACTER VARYING,
    complement CHARACTER VARYING,
    en_face BOOLEAN,
    commune CHARACTER VARYING,
    domaine CHARACTER VARYING,
    nature CHARACTER VARYING,
    nature_deci CHARACTER VARYING,
    indispo_temporaire BOOLEAN
);
ALTER TABLE edp.pull_hydrant OWNER TO edp;

CREATE TABLE edp.pull_hydrant_visite (
    id SERIAL PRIMARY KEY,
    hydrant BIGINT,
    date TIMESTAMP WITHOUT TIME ZONE,
    type CHARACTER VARYING,
    ctrl_debit_pression BOOLEAN,
    agent1 CHARACTER VARYING,
    agent2  CHARACTER VARYING,
    debit INTEGER,
    debit_max INTEGER,
    pression DOUBLE PRECISION,
    pression_dyn DOUBLE PRECISION,
    pression_dyn_deb DOUBLE PRECISION,
    observations CHARACTER VARYING,
    CONSTRAINT fk_hydrant FOREIGN KEY (hydrant)
        REFERENCES edp.pull_hydrant (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
ALTER TABLE edp.pull_hydrant_visite OWNER TO edp;

CREATE TABLE edp.pull_hydrant_anomalies (
    id SERIAL PRIMARY KEY,
    visite BIGINT,
    code CHARACTER VARYING,
    CONSTRAINT fk_visite FOREIGN KEY (visite)
        REFERENCES edp.pull_hydrant_visite (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
ALTER TABLE edp.pull_hydrant_anomalies OWNER TO edp;

INSERT INTO edp.parametres(code, valeur) VALUES
('FLAG_STATUS_PULLWORKER', 'TERMINE'),
('NOM_ORGANISME', 'VEOLIA IDF Seine'),
('LAST_UPDATE_PULLWORKER', NOW());

CREATE OR REPLACE FUNCTION edp.trg_pull_hydrant_insert_update()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
  r_new record;
  r_old record;
  nomOrganisme CHARACTER VARYING;
BEGIN
  r_new = NEW;

  SELECT valeur INTO nomOrganisme FROM edp.parametres WHERE code = 'NOM_ORGANISME';

  IF (TG_OP = 'UPDATE') THEN
    r_old = CASE WHEN OLD IS NOT NULL THEN OLD ELSE NULL END;
    IF(r_new.auteur_modification != nomOrganisme AND r_new.auteur_modification NOT LIKE CONCAT(nomOrganisme, '_%')) THEN
	  -- Si un des champs observés par EDP est modifié, on enregistre le changement
	  IF r_old IS NOT NULL AND (r_new.marque != r_old.marque
								 OR r_new.modele != r_new.modele
								 OR r_new.diametre != r_old.diametre
								 OR r_new.diametre_canalisation != r_old.diametre_canalisation) THEN
        INSERT INTO edp.pull_message(date, date_modification, operation, type, hydrant) VALUES
	    (NOW(), r_new.date_modification, TG_OP, 'CARACTERISTIQUES', r_new.numero);
	  END IF;
	END IF;
  ELSEIF (TG_OP = 'INSERT') THEN
    INSERT INTO edp.pull_message(date, date_modification, operation, type, hydrant) VALUES
	(NOW(), r_new.date_modification, TG_OP, 'CARACTERISTIQUES', r_new.numero);
  END IF;

  RETURN r_new;
END;
$BODY$;

ALTER FUNCTION edp.trg_pull_hydrant_insert_update()
OWNER TO edp;

CREATE TRIGGER trig_pull_hydrant_insert_update
AFTER INSERT OR UPDATE
ON edp.pull_hydrant
FOR EACH ROW
EXECUTE PROCEDURE edp.trg_pull_hydrant_insert_update();

CREATE OR REPLACE FUNCTION edp.trg_pull_hydrant_delete()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
DECLARE
  r_old record;
BEGIN
  r_old = OLD;

  IF (TG_OP = 'DELETE') THEN
	INSERT INTO edp.pull_message(date, date_modification, operation, type, hydrant) VALUES
	(NOW(), NOW(), 'DELETE', 'CARACTERISTIQUES', r_old.numero);
  END IF;
  RETURN NULL;
END;
$BODY$;

ALTER FUNCTION edp.trg_pull_hydrant_delete()
OWNER TO edp;

CREATE TRIGGER trig_pull_hydrant_delete
AFTER DELETE
ON edp.pull_hydrant
FOR EACH ROW
EXECUTE PROCEDURE edp.trg_pull_hydrant_delete();

INSERT INTO edp.type_erreur(code, iterations, contexte, message_action, message_erreur) VALUES
('I0003', 1, 'EDP_SYSTEME', 'Contacter le prestataire et analyser l''origine du dysfonctionnement du système', 'La récupération des modifications Remocra par l''API n''a pas pu être réalisée suite à une erreur système (étape PULLWORKER). La liste des changements de données à appliquer n''est pas récupérable (donc injoignable à ce mél) et il convient de consulter dans Remocra les changements réalisés à partir du [LAST_UPDATE_PULLWORKER] et de répercuter ces derniers de manière manuelle jusqu''à ce que le dispositif soit de nouveau opérationnel.'),
('1010', 1, 'EDP_SYSTEME', 'Contacter le prestataire et analyser l''orginie du dysfonctionnement du système', 'La date utilisée par le système pour récupérer les modifications depuis Remocra n''existe pas ou ne respecte pas le format YYYY-MM-DD hh:mm:ss'),
('1101', 1, 'EDP_SYSTEME', 'Modifier la valeur dans WatGIS', 'Le diamètre nominal renseigné par EDP n''est pas accepté dans Remocra pour un PEI de cette nature');

-- Droits user edp
GRANT SELECT ON edp.pull_message TO read_only_edp;
GRANT SELECT ON edp.pull_hydrant_visite TO read_only_edp;
GRANT SELECT ON edp.pull_hydrant_anomalies TO read_only_edp;
GRANT SELECT ON edp.pull_hydrant TO read_only_edp;
