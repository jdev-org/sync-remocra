-- sqlplus EDP_REMOCRA/1234@//127.0.0.1:1521/edpsigdev
--connect EDP_REMOCRA/1234;

CREATE TABLE VUE_PEI_EDP_REMOCRA(
    gid_edp Number(10) NOT NULL,
    reference VARCHAR2(15),
    geometry1 MDSYS.SDO_GEOMETRY,
    type_objet Varchar2(15),
    prive number(1),
    precision_pei_prive varchar2(73),
    adresse varchar2(107),
    notes_localisation varchar2(120),
    diametre number(5),
    modele varchar2(50),
    diametre_canalisation varchar2(4000),
    etat varchar2(12),
    date_maj_p timestamp,
    date_maj_e timestamp
);

INSERT INTO VUE_PEI_EDP_REMOCRA(gid_edp, reference, geometry1, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele, diametre_canalisation, etat, date_maj_p, date_maj_e)
VALUES(680050023, '751030102', SDO_GEOMETRY(3001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1,4,1,0),SDO_ORDINATE_ARRAY(653314,991,6862718,28,0,629984964,77660733,0)), 'BOUCHE_INCENDIE', 0, NULL,
'43  RUE DE SAINTONGE, PARIS 3EME ARRONDISSEMENT', NULL, 100, 'BI_BAYARD', 100, 'Disponible', TIMESTAMP '2019-02-05 12:00:00', NULL);

INSERT INTO VUE_PEI_EDP_REMOCRA(gid_edp, reference, geometry1, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele, diametre_canalisation, etat, date_maj_p, date_maj_e)
VALUES(680050024, '751030103', SDO_GEOMETRY(3001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1,4,1,0),SDO_ORDINATE_ARRAY(653333,498,6862858,8,0,608007863,793931003,0)), 'BOUCHE_INCENDIE', 0, NULL,
'71  RUE CHARLOT, PARIS 3EME ARRONDISSEMENT', 'CHARLOT A 72 M  DE L’ANGLE DE LA RUE DE FRANCHE COMTE,COTE RUE DU FOREZ', 100, NULL, 150, 'Disponible', TIMESTAMP '2019-02-05 13:37:00', NULL);

INSERT INTO VUE_PEI_EDP_REMOCRA(gid_edp, reference, geometry1, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele, diametre_canalisation, etat, date_maj_p, date_maj_e)
VALUES(680050025, '751030104', SDO_GEOMETRY(3001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1,4,1,0),SDO_ORDINATE_ARRAY(652456,026,6862671,28,0,352307337,935884363,0)), 'BOUCHE_INCENDIE', 0, NULL,
'161  RUE SAINT MARTIN, PARIS 3EME ARRONDISSEMENT', 'ST MARTIN', 100, 'BI BAYARD', 150, 'Disponible', TIMESTAMP '2019-09-20 08:34:25', NULL);

INSERT INTO VUE_PEI_EDP_REMOCRA(gid_edp, reference, geometry1, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele, diametre_canalisation, etat, date_maj_p, date_maj_e)
VALUES(680050622, '751170277', SDO_GEOMETRY(3001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1,4,1,0),SDO_ORDINATE_ARRAY(650005,298,6865734,05,0,-76780823,640679729,0)), 'BOUCHE_INCENDIE', 0, NULL,
'105  RUE LEMERCIER, PARIS 17EME ARRONDISSEMENT', 'LEMERCIER', 100, 'VP', 100, 'Disponible', TIMESTAMP '2019-07-08 15:15:25', TIMESTAMP '2014-05-02 21:00:00');

INSERT INTO VUE_PEI_EDP_REMOCRA(gid_edp, reference, geometry1, type_objet, prive, precision_pei_prive, adresse, notes_localisation, diametre, modele, diametre_canalisation, etat, date_maj_p, date_maj_e)
VALUES(680050161, '751040074', SDO_GEOMETRY(3001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1,4,1,0),SDO_ORDINATE_ARRAY(652557,364,6862165,9,0,-86941719,494078684,0)), 'BOUCHE_INCENDIE', 0, NULL,
'40 FACE RUE DE LA VERRERIE, PARIS 4EME ARRONDISSEMENT', 'VERRERIE', 100, 'RUETIL', 100, 'Disponible', TIMESTAMP '2019-09-17 11:00:45', NULL);


CREATE TABLE VUE_PEI_MOTIF_INDISPO(
    gid_edp Number(10) NOT NULL,
    reference VARCHAR2(15),
    motif_indispo VARCHAR(80),
    date_debut date,
    date_fin_prevue date
);
INSERT INTO VUE_PEI_MOTIF_INDISPO(gid_edp, reference, motif_indispo, date_debut, date_fin_prevue)
VALUES(8496261, '751030102', 'PB_OUVERTURE', TO_DATE('2020/03/01 12:00:44', 'yyyy/mm/dd hh24:mi:ss'), NULL);
UPDATE VUE_PEI_EDP_REMOCRA set date_maj_e = TIMESTAMP '2020-03-01 12:00:44' where reference = '751030102';
update VUE_PEI_MOTIF_INDISPO set gid_edp = 680050023;

-- Tests RBO
--insert into test(date_maj) values(TO_DATE('2003/05/03 21:02:44', 'yyyy/mm/dd hh24:mi:ss'));
--TO_DATE('2003/05/03 21:02:44', 'yyyy/mm/dd hh24:mi:ss');