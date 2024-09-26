# Eau de Paris - Synchro Remocra - Docker services

Cette documentation concerne les services. Pour les applications, se référer à la documentation dédiée : [app/README.md](app/README.md) et [pdi/README.md](pdi/README.md)

Les commandes sont exécutées dans le répertoire `docker`.

## Démarrage rapide

Démarrage des conteneurs (l'initialisation du service `ora` est longue...) :
```sh
# Démarrage des conteneurs
docker-compose up
# docker-compose up --force-recreate db mailtrap ora
```

## Nettoyage

```sh
# Arrêt des conteneurs, Nettoyage des fichiers sur disque (sauf .gitignore/.dockerignore), Suppression des conteneurs
docker-compose stop \
  && find .docker ! -name '.*ignore' -type f -exec rm -f {} + \
  && find .docker -empty -type d -delete \
  && docker rm edp-db edp-mailtrap edp-ora
```

## Dépendances instanciées

### Re-configuration des accès à Oracle par PostgreSQL

A réaliser après la migration.  Exemple de reprise de la configuration du FDW dans le cadre du développement :
 ```sh
docker exec -it edp-db sh -c "psql -U edp  <<EOF

ALTER SERVER edp  OPTIONS (SET dbserver '//ora.syncremocra.eaudeparis.fr:1521/EDPSIGDEV');

ALTER USER MAPPING FOR edp SERVER edp OPTIONS (SET user 'EDP_REMOCRA', SET password '1234');

-- Si besoin (exemple pour dev) :
ALTER FOREIGN TABLE edp.VUE_PEI_EDP_REMOCRA OPTIONS (drop schema);

EOF"
```


### Serveur PostgreSQL
Le conteneur edp-db met à disposition un **serveur PostgreSQL** sur le port 5432.

```sh
# Exemple de requête SQL
docker exec -it edp-db psql -U edp -c "select version()"

# Autre exemple (multiligne)
docker exec -it edp-db sh -c 'psql -U edp  <<EOF
select version() 
EOF'
```


### Serveur Oracle
Le conteneur edp-ora met à disposition un **serveur Oracle** sur le port 1554.

```sh
# Exemple de requête SQL (credentials : system/oracle ou EDP_REMOCRA/1234)
docker exec -it edp-ora sh -c 'echo "describe VUE_PEI_EDP_REMOCRA" | sqlplus EDP_REMOCRA/1234@//127.0.0.1:1521/edpsigdev'

# Autre exemple (multiligne)
docker exec -it edp-ora sh -c 'sqlplus EDP_REMOCRA/1234@//127.0.0.1:1521/edpsigdev <<EOF
describe VUE_PEI_EDP_REMOCRA
EOF'
```


### Serveur de mails
Le conteneur edp-mailtrap met à disposition un **serveur SMTP** qui est utilisé par défaut pour l'envoi des mails et qui conserve tous les mails en local. Les mails sont consultables via le **webmail** Roundcube :
* http://localhost:8090 (edp/edp)