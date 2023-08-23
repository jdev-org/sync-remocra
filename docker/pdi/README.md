# Eau de Paris - Synchro Remocra - Docker app PDI

## Construction de l'image docker `edp/syncremocra-pdi` en local

Exécuter le script :
```sh
./build.sh
```



## Exécution d'un conteneur de tests en local

Exécuter les commandes suivantes :
```sh
# Dépendances
(cd .. && docker-compose up)

# Conteneur de tests
docker run --rm --name edp-syncremocra-pdi \
  --network="host"  \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/example/config/edp.properties":/jobs/edp.properties \
  \
  edp/syncremocra-pdi \
  -file:"/jobs/edp_notification.kjb" -level:Error
```

Il est également possible de monter un répertoire de jobs non packagé avec l'option suivante :
```sh
  -v "$(pwd)/jobs":/jobs \
```



## Version d'un conteneur `edp/syncremocra-pdi`
Exécuter la commande suivante :
```sh
docker run --rm --name edp-syncremocra-pdi \
  --entrypoint=  edp/syncremocra-pdi \
  cat /scripts/version.txt
```