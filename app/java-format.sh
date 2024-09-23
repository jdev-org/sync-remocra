#!/bin/bash
set -e

ROOT_DIR=$(realpath "$(dirname $0)/..")

java_files=$1

GJF_V=1.8
mkdir -p ${ROOT_DIR}/.cache
if [ ! -f ${ROOT_DIR}/.cache/google-java-format-${GJF_V}-all-deps.jar ]
then
    wget "https://github.com/google/google-java-format/releases/download/google-java-format-${GJF_V}/google-java-format-${GJF_V}-all-deps.jar" -O .cache/google-java-format-${GJF_V}-all-deps.jar
    chmod 755 ${ROOT_DIR}/.cache/google-java-format-${GJF_V}-all-deps.jar
fi

if [ $# -eq 0 ]
  then
    echo "All files"
    java_files=$(find ${ROOT_DIR}/app/src ${ROOT_DIR}/db/src -name "*.java")
  else
    echo "Files : ${java_files}"
fi
[ -z "$java_files" ] && exit 0

# Format java files
java -jar ${ROOT_DIR}/.cache/google-java-format-${GJF_V}-all-deps.jar --replace $java_files
