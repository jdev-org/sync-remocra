#! /bin/sh

readonly ROOT_DIR=$(realpath "$(dirname "$0")/../..")
cd $ROOT_DIR

APP_VERSION=$(git describe --match 'v*' |cut -c2-)

# Build artefacts and runnable image
docker build . -f docker/app/Dockerfile \
  --network=host --force-rm \
  -t edp/syncremocra \
  --build-arg APP_VERSION=${APP_VERSION} \
  \
  || { echo 'Image build failed' ; exit 1; }
