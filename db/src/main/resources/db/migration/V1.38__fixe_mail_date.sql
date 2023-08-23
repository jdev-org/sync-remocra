CREATE OR REPLACE VIEW edp.vue_erreur_to_notify AS
SELECT
      contexte,
    COUNT(*) AS erreurs_nombre,
    array_to_string(ARRAY_AGG(err.id),',') AS erreurs_ids,
  xmlelement(name "rapport",
    xmlelement(name "erreurs",
          xmlagg(
            xmlelement(
              name erreur,
              xmlelement(name "dateHeureSynchronisation", to_char(err.date,'dd/mm/yyyy hh24:mi:ss')),
              xmlelement(name "refPEI", coalesce(tpei.reference,'-')),
              xmlelement(name "messageErreur", err.description),
              xmlelement(name "messageAction", terr.message_action),
              xmlelement(name "donnees",
              xmlelement(name "contexteVisite", COALESCE(
                replace(mess.json_creation_visite::json ->> 'contexte'::text, 'NP'::text, 'Visite non programmée'::text),
                replace(mess."json"::json ->> 'contexte'::text, 'NP'::text, 'Visite non programmée'::text),
                '--')
              ),
              xmlelement(name "dateHeureVisite",COALESCE(
                  to_char((mess.json_creation_visite::json ->> 'date'::text)::timestamp without time zone, 'dd/mm/yyyy à hh24h mi'::text),
                  to_char((mess."json"::json ->> 'date'::text)::timestamp without time zone, 'dd/mm/yyyy à hh24h mi'::text),
                  '--'
                )
                ),
xmlelement(name "motifs", array_to_string(ARRAY(SELECT json_array_elements_text(mess.json_creation_visite::json->'anomaliesConstatees')),', '))
              )
            )
          )
        )
  ) AS xml_notification
FROM
edp.erreur err
JOIN edp.type_erreur terr ON (terr.id = err.type_erreur)
LEFT JOIN edp.message mess ON (mess.id = err.message)
LEFT JOIN edp.tracabilite_pei tpei ON (tpei.id = mess.id_traca_pei)
WHERE NOT notifie
GROUP BY contexte;