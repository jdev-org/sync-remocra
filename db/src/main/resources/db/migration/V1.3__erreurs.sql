CREATE TABLE edp.type_erreur (
	id serial PRIMARY KEY,
	code CHARACTER VARYING NOT NULL UNIQUE,
	iterations INT NOT NULL DEFAULT 1,
	contexte CHARACTER VARYING NOT NULL,
	message_action CHARACTER VARYING
);
ALTER TABLE edp.type_erreur OWNER TO edp;

COMMENT ON TABLE edp.type_erreur IS 'Typologie des erreurs pouvant être rencontrées lors du processus global de synchronisation entre REMOCRA et EDP';
COMMENT ON COLUMN edp.type_erreur.id IS 'Identifiant interne';
COMMENT ON COLUMN edp.type_erreur.code IS 'Code du type d''erreur. Code identique à celui retourné par l''API REMOCRA dans le cas d''une erreur lié à un appel API';
COMMENT ON COLUMN edp.type_erreur.iterations IS 'Nombre d''itérations autorisé pour ce type d''erreur. Valable dans le cas des erreurs associés à des messages à traiter ou dans le cas des erreurs système';
COMMENT ON COLUMN edp.type_erreur.contexte IS 'Contexte permettant d''adresser les erreurs lors de la phase de notification';
COMMENT ON COLUMN edp.type_erreur.message_action IS 'Message type permettant d''indiquer au destinataire des erreurs le type d''action à engager';

INSERT INTO edp.type_erreur(code, iterations, contexte, message_action) VALUES
('0001', 3, 'EDP_SYSTEME', 'Vérifier que le serveur est opérationnel et vérifier le paramétrage dans le fichier de configuration "nomDuFichier"'),
('0002', 3, 'EDP_SYSTEME', 'Vérifier que le serveur est opérationnel et vérifier le paramétrage dans le fichier de configuration "nomDuFichier"'),
('0003', 3, 'EDP_SYSTEME', 'Vérifier que le serveur est opérationnel et son accès est autorisé'),
('1000', 1, 'EDP_METIER', 'Supprimer le PEI dans la base EDP ou procéder à la création du PEI via l''interface applicative REMOCRA'),
('1300', 1, 'REMOCRA_METIER', 'Procéder à l''attribution des droits via l''interface applicative REMOCRA'),
('1001', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1002', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1003', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1004', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1005', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1301', 1, 'REMOCRA_SYSTEME', 'Procéder à l''analyse des logs applicatifs'),
('1006', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA et attendre la prochaine synchronisaton'),
('1100', 1, 'REMOCRA_METIER', 'Procéder au rapprochement des deux PEI  via l''interface applicative REMOCRA'),
('1007', 1, 'REMOCRA_METIER', 'Supprimer le PEI dans WatGIS ou procéder à la création du PEI via l''interface applicative REMOCRA'),
('2000', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et procéder à la vérification du formatage des dates '),
('2001', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et procéder à la vérification du type de visite dans la base EDP'),
('2100', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA si nécessaire'),
('2101', 1, 'REMOCRA_METIER', 'Créer la visite de réception via l''interface applicative REMOCRA'),
('2102', 1, 'REMOCRA_METIER', 'Créer la visite une visite de reconnaissance opérationnelle initiale  via l''interface applicative REMOCRA'),
('2103', 1, 'EDP_METIER', 'Créer la visite éventuellement une visite d''un autre type via l''interface applicative REMOCRA'),
('2002', 1, 'EDP_METIER', 'Mettre à jour la table de correspondance au sein de la base de synchronisation EDP - REMOCRA'),
('2104', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA'),
('2105', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et corriger éventuellement la valeur dans WatGIS'),
('2106', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et corriger éventuellement la valeur dans WatGIS'),
('2107', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et corriger éventuellement la valeur dans WatGIS'),
('2108', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et corriger éventuellement la valeur dans WatGIS'),
('2109', 1, 'EDP_METIER', 'Créer la visite via l''interface applicative REMOCRA et corriger éventuellement la valeur dans WatGIS'),
('2300', 1, 'REMOCRA_SYSTEME', 'Procéder à l''analyse des logs applicatifs'),
('2003', 1, 'EDP_METIER', 'Aucune action'),
('0200', 1, 'EDP_SYSTEME', 'Vérifier le paramétrage de la clef d''API et procéder à l''attribution des droits via l''interface applicative REMOCRA'),
('2200', 1, 'EDP_METIER', 'Demander une évolution du dispositif'),
('2201', 1, 'EDP_METIER', 'Demander une évolution du dispositif'),
('2110', 1, 'EDP_METIER', 'Aucune action');

DROP TABLE edp.erreur;
CREATE TABLE edp.erreur (
	id serial PRIMARY KEY,
	date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
	description CHARACTER VARYING,
	notifie BOOLEAN NOT NULL DEFAULT FALSE,
	type_erreur BIGINT NOT NULL,
	message BIGINT,
	CONSTRAINT fk_erreur_type_erreur
		FOREIGN KEY (type_erreur)
		REFERENCES edp.type_erreur (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT fk_erreur_message
		FOREIGN KEY (message)
		REFERENCES edp.message (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE
);
ALTER TABLE edp.erreur OWNER TO edp;

CREATE INDEX fk_erreur_type_erreur_idx  ON edp.erreur USING btree(type_erreur);
CREATE INDEX fk_erreur_message_idx  ON edp.erreur USING btree(message);

COMMENT ON TABLE edp.erreur IS 'Erreur rencontrée lors du processus global de synchronisation entre REMOCRA et EDP et pouvant donner lieu à notification';
COMMENT ON COLUMN edp.erreur.id IS 'Identifiant interne';
COMMENT ON COLUMN edp.erreur.date IS 'Date et heure de l''erreur';
COMMENT ON COLUMN edp.erreur.description IS 'Description ou message d''erreur retourné par le processus ou l''API';
COMMENT ON COLUMN edp.erreur.type_erreur IS 'Référence au type d''erreur';
COMMENT ON COLUMN edp.erreur.message IS 'Référence au message impliqué dans le processus de PUSH vers Remocra. Permet notament de retrouver le PEI impacté et les informations à synchroniser';

ALTER TABLE edp.message ADD COLUMN json CHARACTER VARYING DEFAULT NULL;