#! /bin/sh

cd $(dirname $0)

# Fichier de configuration de log éventuel
[ -f "${LOG_CONFIG_FILE}" ] && export LOG_OPTS="-Dlog4j.configurationFile=${LOG_CONFIG_FILE}"

exec java $GC_OPTS $JAVA_OPTS ${LOG_OPTS} \
  -cp '/app/lib/*' fr.eaudeparis.syncremocra.App \
  -c ${CONFIG_FILE} \
  "$@"
