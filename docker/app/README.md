# Eau de Paris - Synchro Remocra - Docker app

## Construction de l'image docker `edp/syncremocra` en local

Exécuter le script :
```sh
./build.sh
```



## Exécution d'un conteneur de tests en local

Exécuter les commandes suivantes :
```sh
# Dépendances
(cd .. && docker-compose up)

# Conteneur de tests (ex : validate)
docker run --rm --name edp-syncremocra \
  --network="host" \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/example/config":/app/config -v "$(pwd)/example/log":/app/log \
  -e JAVA_OPTS="-Dedp.mail.from=edp+admin.sysprop@atolcd.com" \
  \
  edp/syncremocra \
  db-validate
```



## Aide fournie par le conteneur `edp/syncremocra`
Exécuter la commande suivante :
```sh
docker run --rm --name edp-syncremocra \
  --network="host" \
  -u $(id -u):$(id -g) \
  -v "$(pwd)/example/config":/app/config -v "$(pwd)/example/log":/app/log \
  \
  edp/syncremocra \
  help
```