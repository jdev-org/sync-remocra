CREATE TABLE edp.referentiel_marques_modeles (
    code_modele_edp CHARACTER VARYING,
    code_marque_remocra CHARACTER VARYING,
    code_modele_remocra CHARACTER VARYING,
    CONSTRAINT referentiel_marques_modeles_pkey PRIMARY KEY (code_modele_edp, code_marque_remocra, code_modele_remocra)
);
ALTER TABLE edp.referentiel_marques_modeles OWNER TO edp;

INSERT INTO edp.referentiel_marques_modeles(code_modele_edp, code_marque_remocra, code_modele_remocra) VALUES
('BI BAYARD', 'BAYARD', 'BI BAYARD'),
('PI BAYARD', 'BAYARD', 'PI BAYARD'),
('BI PAM', 'PAM', 'BI PAM'),
('PI PAM', 'PAM', 'PI PAM'),
('BAYARD SECURE', 'BAYARD', 'BAYARD SECURE'),
('BAYARD LUTECE', 'BAYARD', 'BAYARD LUTECE'),
('MANGEON', 'MANGEON', 'MANGEON'),
('RUETIL', 'RUETIL', 'RUETIL'),
('VP', 'VP', 'VP'),
('RUETIL SECURE', 'RUETIL', 'RUETIL SECURE'),
('VP SECURE', 'VP', 'VP SECURE');

INSERT INTO edp.type_erreur(code, iterations, contexte, message_action, message_erreur) VALUES
('I1000', 1, 'EDP_METIER', 'Vérifier que le modele est bien renseigné, le cas échéant demander à ce que le modèle soit ajouté dans la base REMOcRA', 'Le modèle renseigné ne possède aucune correspondance dans la base REMOcRA');