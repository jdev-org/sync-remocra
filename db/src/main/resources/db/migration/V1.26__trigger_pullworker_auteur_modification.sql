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
     IF(COALESCE(NEW.auteur_modification, '') != nomOrganisme AND COALESCE(NEW.auteur_modification, '') NOT LIKE CONCAT(nomOrganisme, '_%')) THEN
        -- Si un des champs observés par EDP est modifié, on enregistre le changement
        IF (NEW.marque != OLD.marque
			OR NEW.modele != OLD.modele
			OR NEW.diametre != OLD.diametre
			OR NEW.diametre_canalisation != OLD.diametre_canalisation) THEN

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

GRANT SELECT ON edp.referentiel_anomalies TO read_only_edp;
GRANT SELECT ON edp.referentiel_hydrant_diametres TO read_only_edp;
GRANT SELECT ON edp.referentiel_marques_modeles TO read_only_edp;

UPDATE edp.referentiel_anomalies SET code_bspp = 'STRTPOOL' WHERE code_bspp = 'BSPP_STPO';