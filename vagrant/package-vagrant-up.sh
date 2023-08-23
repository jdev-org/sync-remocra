#! /bin/bash

readonly ROOT_DIR=$(realpath "$(dirname "$0")/..")

${ROOT_DIR}/dist/package-tar.sh && \
  cp ${ROOT_DIR}/dist/edp-syncremocra-docker-images-*.tar ${ROOT_DIR}/vagrant/scripts/ && \
  (cd ${ROOT_DIR}/vagrant && vagrant destroy -f && vagrant up 2>&1 | tee vagrant-provision.log)
