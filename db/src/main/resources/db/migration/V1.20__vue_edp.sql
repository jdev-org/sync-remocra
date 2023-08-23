CREATE VIEW edp.vue_erreurs_edp AS
SELECT e.date, e.notifie, te.message_erreur, te.message_action, te.contexte, m.type, tp.reference, m.json
FROM edp.erreur e
JOIN edp.type_erreur te on te.id = e.type_erreur
LEFT JOIN edp.message m on e.message = m.id
LEFT JOIN edp.tracabilite_pei tp on tp.id = m.id_traca_pei;

CREATE ROLE read_only_edp WITH LOGIN PASSWORD 'Edp2021*'
NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION VALID UNTIL 'infinity';
GRANT CONNECT ON DATABASE edp TO read_only_edp;
GRANT USAGE ON SCHEMA edp TO read_only_edp;
GRANT SELECT ON edp.vue_erreurs_edp TO read_only_edp;