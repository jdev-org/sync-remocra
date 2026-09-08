-- One mapping per EDP motif prevents duplicate attention points on synchronization.
DELETE FROM edp.referentiel_anomalies
WHERE code_edp IN ('FERME DEMANDE SSQ', 'APP PROJET');

INSERT INTO edp.referentiel_anomalies (code_bspp, code_edp)
VALUES
  ('BSPP_APSE', 'FERME DEMANDE SSQ'),
  ('BSPP_APSE', 'APP PROJET');
