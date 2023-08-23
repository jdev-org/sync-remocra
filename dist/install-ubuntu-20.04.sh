#! /bin/bash

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# EDP Syncremocra - Installation with Docker (Ubuntu 20.04)
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Default values (set values before if necessary)
export EDP_DOCKER_IMAGES_TAR=${EDP_DOCKER_IMAGES_TAR:-""}
# Registry. ⚠ username & password has to be set before
# ex : client-docker-registry.atolcd.com
export EDP_REGISTRY_SERVER=${EDP_REGISTRY_SERVER:-""}
export EDP_REGISTRY_USERNAME=${EDP_REGISTRY_USERNAME:-"unkwnown-username"}
export EDP_REGISTRY_PASSWORD=${EDP_REGISTRY_PASSWORD:-"unkwnown-password"}
# User
export EDP_UID=${EDP_UID:-2000}
export EDP_GID=${EDP_GID:-2000}
# Used in config in files
export EDP_VERSION=${EDP_VERSION:-"latest"}
export DATABASE_PASSWORD=${DATABASE_PASSWORD:-"okdqsk45dop4qs"}
# ex : smtp-relay.hosting.priv.atolcd.com
export MAIL_HOST=${MAIL_HOST:-"localhost"}
export MAIL_PORT=${MAIL_PORT:-25}
export MAIL_USERNAME=${MAIL_USERNAME:-""}
export MAIL_PASSWORD=${MAIL_PASSWORD:-""}
export MAIL_FROM=${MAIL_FROM:-"edp+admin@atolcd.com"}

# Error managing
abort() {
  (RED='\033[0;31m' && NC='\033[0m' && printf "${RED}\n\n- ABORTED DUE TO ERROR -\n\n${NC}" >&2)
  exit 1
}
trap 'abort' ERR
set -e

echo && echo "EDP synremocra installation"
if ( ! (whoami | grep root > /dev/null) ); then
  echo && echo "not root : sudo su with preserve-env"
  sudo -E su
fi

echo && echo "Base packages"
apt update && apt -y upgrade

# Utilities
apt -y install wget curl nano htop locales

# Eventually : Sync time, Firewall

# Locale fr_FR
if ( ! (locale | grep "LANG=fr_FR" | grep "8" > /dev/null) ); then
  echo "Passage en fr_FR.UTF-8";
  localedef -i fr_FR -c -f UTF-8 -A /usr/share/locale/locale.alias fr_FR.UTF-8
  locale-gen --purge fr_FR.UTF-8
  dpkg-reconfigure --frontend=noninteractive locales && \
    update-locale fr_FR.UTF-8 && \
  export LANG=fr_FR.UTF-8
else
  echo "Déjà en fr_FR.UTF-8";
fi



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# PostgreSQL
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
apt-get update
apt-get -y install postgresql-13 postgresql-client-13

# PostGIS
PG_MAJOR=13
POSTGIS_MAJOR=3
apt-get update \
      && apt-cache showpkg postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
      && apt-get install -y --no-install-recommends \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR-scripts

# Base de données
su postgres -c "createdb -E UTF8 -l fr_FR.UTF-8 -T template0 edp"

# Utilisateur
su postgres -c "psql -c \"  CREATE ROLE edp PASSWORD '${DATABASE_PASSWORD}' SUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;GRANT CREATE ON DATABASE edp TO edp;  \""

# Exempled de requête pour la suite
#PGPASSWORD=${DATABASE_PASSWORD} psql -U edp -h localhost -c "select version()"

# Disable SSL
sed -i "s/ssl = .*/ssl = off/g" /etc/postgresql/13/main/postgresql.conf
service postgresql reload



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# laurenz/oracle_fdw
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

apt-get update && apt-get install -y --no-install-recommends alien

SRCROOTDIR=/usr/local/src
cd $SRCROOTDIR

# https://www.oracle.com/fr/database/technologies/instant-client/linux-x86-64-downloads.html
curl https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm \
  -o $SRCROOTDIR/oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm
curl https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm \
  -o $SRCROOTDIR/oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm
curl https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm \
  -o $SRCROOTDIR/oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm

alien -i oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm && \
  alien -i oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm && \
  alien -i oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm

# TODO voir si besoin d'ajouter les variables dans le profil
export ORACLE_HOME=/usr/lib/oracle/21/client64
export PATH=$PATH:$ORACLE_HOME/bin
export OCI_LIB_DIR=$ORACLE_HOME/lib
export OCI_INC_DIR=/usr/include/oracle/21/client64
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ORACLE_HOME/lib

apt-get -y install git libaio1 postgresql-server-dev-all &&  \
  git clone https://github.com/laurenz/oracle_fdw.git && \
  cd oracle_fdw && make && make install && \
  cd .. && rm -R oracle_fdw

echo $OCI_LIB_DIR | tee /etc/ld.so.conf.d/oracle.conf && \
  ldconfig



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Docker
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Remove old version ?
#apt-get remove docker docker-engine docker.io || echo " Docker was not installed" 

# SET UP THE REPOSITORY
apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

# INSTALL DOCKER ENGINE
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io

docker --version

# Auto start
systemctl start docker && systemctl enable docker

# Group
groupadd docker || echo "groupe docker existant"



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# edp user and group
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Utilisateur et groupe
export EDP_HOME=/home/edp
id -g ${EDP_GID} || sudo groupadd -g ${EDP_GID} edp
id -u ${EDP_UID} || sudo useradd edp --uid ${EDP_UID} --gid ${EDP_GID} --groups ${EDP_GID} --shell=/bin/bash --home-dir ${EDP_HOME} --create-home
# Dans le groupe docker
usermod -aG docker edp



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Docker images access
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Paramétrage du registre docker
if [ ! -z "${EDP_REGISTRY_SERVER}" ] ; then
  echo "EDP_REGISTRY_SERVER : paramétrage du registre docker"
  export USERNAMEPASSWORD_B64=$(echo -n "${EDP_REGISTRY_USERNAME}:${EDP_REGISTRY_PASSWORD}" | base64)
  mkdir -p ${EDP_HOME}/.docker && envsubst << "EOF" > ${EDP_HOME}/.docker/config.json
  {
    "auths": {
      "${EDP_REGISTRY_SERVER}": {
        "auth": "${USERNAMEPASSWORD_B64}"
      }
    }
  }
EOF
  export USERNAMEPASSWORD_B64=
  chown -R edp:edp ${EDP_HOME}/.docker
  su edp -c "docker login ${EDP_REGISTRY_SERVER}"
else
  echo "EDP_REGISTRY_SERVER : pas de paramétrage du registre docker"
fi


# Chargement des images docker
if [ ! -z "${EDP_DOCKER_IMAGES_TAR}" ] ; then
  echo "EDP_DOCKER_IMAGES_TAR : chargement des images docker"
  docker load < ${EDP_DOCKER_IMAGES_TAR}
else
  echo "EDP_DOCKER_IMAGES_TAR : pas de chargement des images docker"
fi



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Application
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

mkdir -p /var/syncremocra/{config,log}
envsubst << "EOF" > /var/syncremocra/common.env
# -- VERSION
EDP_VERSION=${EDP_VERSION}
# -- APP
#DATABASE_SERVERNAME=${DATABASE_SERVERNAME}
#DATABASE_PORTNUMBER=${DATABASE_PORTNUMBER}
#DATABASE_DATABASENAME=${DATABASE_DATABASENAME}
#DATABASE_USER=${DATABASE_USER}
DATABASE_PASSWORD=${DATABASE_PASSWORD}
MAIL_USERNAME=${MAIL_USERNAME}
MAIL_PASSWORD=${MAIL_PASSWORD}
MAIL_FROM=${MAIL_FROM}
MAIL_HOST=${MAIL_HOST}
MAIL_PORT=${MAIL_PORT}
EOF

# Création du lien symbolique vers /var/log
ln -s /var/syncremocra/log /var/log/syncremocra

# Application log configuration
envsubst << "EOF" > /var/syncremocra/config/log4j2.xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration monitorInterval="60" shutdownHook="disable">
  <Appenders>
    <Async name="AsyncLogFile">
      <AppenderRef ref="LogFile" />
    </Async>
    <RollingFile name="LogFile"
      fileName="${env:LOG_DIR}/${env:LOG_FILENAME}"
      filePattern="${env:LOG_DIR}/${env:LOG_FILENAME}-%d{yyyy-MM-dd}-%i.gz">
      <PatternLayout>
        <Pattern>%d %p %c{1.} [%t] %m%n</Pattern>
      </PatternLayout>
      <Policies>
        <TimeBasedTriggeringPolicy />
        <SizeBasedTriggeringPolicy size="5 GB" />
      </Policies>
      <DefaultRolloverStrategy>
        <Delete basePath="${env:LOG_DIR}" maxDepth="1">
          <IfFileName glob="${env:LOG_FILENAME}-*.gz" />
          <IfLastModified age="6d" />
        </Delete>
      </DefaultRolloverStrategy>
    </RollingFile>

    <Async name="AsyncSTDOUT">
      <AppenderRef ref="STDOUT" />
    </Async>
    <Console name="STDOUT" target="SYSTEM_OUT">
      <PatternLayout pattern="%d %p %c{1.} [%t] %m%n"/>
    </Console>
  </Appenders>

  <Loggers>
    <Root level="info">
      <AppenderRef ref="AsyncLogFile" />
      <AppenderRef ref="AsyncSTDOUT" />
    </Root>
  </Loggers>
</Configuration>
EOF

# Application configuration
envsubst << "EOF" > /var/syncremocra/config/syncremocra.conf
edp = {
}
EOF

# Files owner
[ $(find /var/syncremocra ! -user ${EDP_UID} 2>/dev/null | wc -l) -gt 0 ] && \
  echo "Change owner to ${EDP_UID}:${EDP_GID}" && \
  chown -R ${EDP_UID}:${EDP_GID} /var/syncremocra



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Scripts d'exécution
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Création des scripts d'exécution (à adapter)

addcontext() {
  export APP_CTX=$1
  export APP_EDP_REGISTRY_PREFIX=$([ -z "${EDP_REGISTRY_SERVER}" ] || echo "${EDP_REGISTRY_SERVER}" | sed "s/\/\{0,1\}$/\//")
  envsubst << "EOF" > /var/syncremocra/run-${APP_CTX}.sh
#! /bin/sh

# On vérifie qu'un conteneur de même nom n'est pas en cours d'exécution
if [ "$(docker ps -q -f name=edp-syncremocra-${APP_CTX})" ]; then
  echo "Conteneur edp-syncremocra-${APP_CTX} déjà en cours d'exécution"
else
  docker run --network="host" --rm --name edp-syncremocra-${APP_CTX} \
    --env-file /var/syncremocra/common.env \
    --env LOG_FILENAME=syncremocra-${APP_CTX}.log \
    --user ${EDP_UID}:${EDP_GID} \
    -v "/var/syncremocra/config":/app/config -v "/var/syncremocra/log":/app/log \
    ${APP_EDP_REGISTRY_PREFIX}edp/syncremocra:$(cat /var/syncremocra/common.env | grep EDP_VERSION | cut -c13-) \
    \
    ${APP_CTX}
fi
EOF
  chmod u+x /var/syncremocra/run-${APP_CTX}.sh
}

addcontext db-validate
addcontext job-test-all

# Files owner
[ $(find /var/syncremocra ! -user ${EDP_UID} 2>/dev/null | wc -l) -gt 0 ] && \
  echo "Change owner to ${EDP_UID}:${EDP_GID}" && \
  chown -R ${EDP_UID}:${EDP_GID} /var/syncremocra



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Tâches planifiées
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# A adapter
addcrontab() {
  if ( ! (crontab -u edp -l | grep "/var/syncremocra/run"  > /dev/null) ); then
    echo && echo "Installation des tâches planifiées de l'utilisateur edp"
    envsubst << "EOF" > /tmp/edpcrontab.txt
* * * * * /var/syncremocra/run-db-validate.sh
EOF
    crontab -u edp /tmp/edpcrontab.txt
    rm -f /tmp/edp.txt
    #service crond restart
  fi
}

# Appeler addcrontab pour planifier les tâches (ou le faire manuellement)
# addcrontab



# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Désactivation de l'utilisateur edp
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# A voir selon le besoin car :
# - permet l'exécution des tâches planifiées
# - ne permet pas de s'identifier ou d'exécuter par exemple la commande `su edp -c /var/syncremocra/run-db-validate.sh`
#usermod --shell /usr/sbin/nologin edp
