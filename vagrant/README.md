# Eau de Paris - Synchro Remocra - Tests Vagrant

L'exécution de la VM nécessite l'installation de vagrant et virtualbox.

## Exécution de la VM

```sh
# Production des artefacts
../dist/package-tar.sh && \
  rm -rf scripts/edp-syncremocra-docker-images-*.tar && \
  cp ../dist/edp-syncremocra-docker-images-*.tar scripts/

# Démarrage et provisionning de la VM
vagrant up
```

## Nettoyage

```sh
vagrant destroy -f
```
