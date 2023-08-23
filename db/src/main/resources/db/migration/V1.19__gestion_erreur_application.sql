INSERT INTO edp.parametres(code, valeur) VALUES
('FLAG_STATUS_PUSHWORKER', 'TERMINE'),
('FLAG_STATUS_CHANGEDATACAPTURE', 'TERMINE'),
('DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE', null);

INSERT INTO edp.type_erreur(code, iterations, contexte, message_action, message_erreur) VALUES
('I1004', 1, 'EDP_METIER', 'Procéder à la mise à jour manuelle du PEI', 'Suite à l''erreur système localisée à l''étape PUSH WORKER et comme mentionné dans un mél préalable,  les changements de disponibilité et/ou de données patrimoniales n''ont pas pu être transmis à Remocra pour ce PEI'),
('I0001', 1, 'EDP_METIER', 'Contacter le prestataire pour analyser l''origine du dysfonctionnement du système', 'La synchronisation des données vers REMOCRA na pas pu être réalisée suite à une erreur système (étape PUSH WORKER). La liste des messages des changements de données vous est transmise ci-joint pour permettre répercuter ces derniers de manière manuelle'),
('I0002', 1, 'EDP_METIER', 'Contacter le prestataire pour analyser l''origine du dysfonctionnement du système', 'La récupération des données de WatGIS par lecture de la base de données Oracle n''a pas pu être réalisée suite à une erreur système (étape CHANGE DATA CAPTURE). La liste des changements de données à appliquer n''est pas récupérable (donc injoignable à ce mél) et il convient de consulter dans WatGIS les changements réalisés à partir du [DERNIERE_REMONTEE_OK_CHANGE_DATA_CAPTURE] et de répercuter ces derniers de manière manuelle jusqu''à ce que le dispositif soit de nouveau opérationnel.');