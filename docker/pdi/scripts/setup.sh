#!/bin/sh
set -e

# PENTAHO_DI_JAVA_OPTIONS
export PENTAHO_DI_JAVA_OPTIONS="-Xms${PDI_INITIAL_MEMORY:-1G} -Xmx${PDI_MAXIMUM_MEMORY:-2G} -XX:MaxPermSize=${PDI_MAXIMUM_PERMSIZE:-256m} -DPENTAHO_METASTORE_FOLDER=${KETTLE_HOME}/.pentaho"

# Redéfition des paramètres dans ${KETTLE_HOME}/.kettle/kettle.properties
# TODO voir si nécessaire

exit 0