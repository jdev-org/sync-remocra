#!/bin/bash

ROOT_DIR=$(realpath "$(dirname $0)/..")
cd ${ROOT_DIR}

# Nettoyage
rm -rf ${ROOT_DIR}/dist/edp-syncremocra-docker-images-*.tar

#APP_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout)
APP_VERSION=$(git describe --match 'v*' |cut -c2-)
DOCKER_UID=2000
DOCKER_GID=2000

# Construction de edp/syncremocra
echo && echo && echo "Construction de edp/syncremocra:${APP_VERSION}"
docker build ${ROOT_DIR} -f ${ROOT_DIR}/docker/app/Dockerfile \
  --network=host --force-rm \
  -t edp/syncremocra:${APP_VERSION} \
  --build-arg APP_VERSION=${APP_VERSION} --build-arg UID=${DOCKER_UID} --build-arg GID=${DOCKER_GID} \
  \
  || { echo 'Image build failed' ; exit 1; }
docker tag edp/syncremocra:${APP_VERSION} edp/syncremocra:latest

# Construction de edp/syncremocra-pdi
echo && echo && echo "Construction de edp/syncremocra-pdi:${APP_VERSION}"
docker build ${ROOT_DIR}/docker/pdi -f ${ROOT_DIR}/docker/pdi/Dockerfile \
  --network=host --force-rm \
  -t edp/syncremocra-pdi:${APP_VERSION} \
  --build-arg APP_VERSION=${APP_VERSION} --build-arg UID=${DOCKER_UID} --build-arg GID=${DOCKER_GID} \
  \
  || { echo 'Image build failed' ; exit 1; }
docker tag edp/syncremocra-pdi:${APP_VERSION} edp/syncremocra-pdi:latest

# Sauvegarde dans une archive tar
echo && echo && echo "Création de l'archive tar avec edp/syncremocra(-pdi):${APP_VERSION}"
docker save --output ${ROOT_DIR}/dist/edp-syncremocra-docker-images-${APP_VERSION}.tar \
  edp/syncremocra:${APP_VERSION} edp/syncremocra:latest \
  edp/syncremocra-pdi:${APP_VERSION} edp/syncremocra-pdi:latest

# Charger les images (exemple)
#docker load < edp-syncremocra-docker-images-1.0.tar