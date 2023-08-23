DROP FOREIGN TABLE edp.vue_pei_edp_remocra;
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
  materiau_canalisation CHARACTER VARYING(4000),
  date_essai TIMESTAMP WITHOUT TIME ZONE,
  essai_pression_statique NUMERIC(8,2),
  essai_pression_dynamique NUMERIC(8,2),
  essai_debit NUMERIC(8,2),
  etat CHARACTER VARYING(12),
  date_derniere_visite TIMESTAMP WITHOUT TIME ZONE,
  type_derniere_visite CHARACTER VARYING(53),
  date_maj_p TIMESTAMP WITHOUT TIME ZONE,
  date_maj_e TIMESTAMP WITHOUT TIME ZONE
) SERVER edp OPTIONS (schema 'READ_ONLY', table 'VUE_PEI_EDP_REMOCRA');

ALTER TABLE edp.VUE_PEI_EDP_REMOCRA OWNER TO edp;

UPDATE edp.type_erreur
SET contexte = 'EDP_SYSTEME'
WHERE code = 'I0002' OR code = 'I0001';