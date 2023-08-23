FROM alpine:3.12

LABEL maintainer="ATOL Conseils & Développements"

# Trick copy or download (Docker must exists). Pattern :
# COPY Dockerfile <resource>* <dest>/
# RUN ([ -f <dest>/<resource> ] || wget -P <dest> -q <>/<resource-part-url><resource>) && rm -f Dockerfile

# ---------------
# Non root user
# ---------------
ARG UID=1000
ARG GID=1000
RUN addgroup -g ${GID} -S pdi && \
    adduser -u ${UID} -S pdi -G pdi

COPY scripts/* /scripts/
COPY jobs/* /jobs/

#Variable d'environnement tz
ENV JAVA_DISTRIBUTION openjdk
ENV JAVA_VERSION 8
RUN apk update && apk upgrade && apk add tzdata ca-certificates openssl curl gettext gdal-tools "$JAVA_DISTRIBUTION$JAVA_VERSION" \
  && ln -s "/usr/lib/jvm/java-1.$JAVA_VERSION-openjdk/bin/javac" /usr/bin/javac

# ENV
ENV LANG                        fr_FR.UTF-8
ENV ENCODING                    UTF-8
ENV TZ                          Europe/Paris

ENV KETTLE_HOME                 /home/pdi
ENV PENTAHO_METASTORE_FOLDER    ${KETTLE_HOME}/.pentaho/metastore
ENV KETTLE_JNDI_ROOT            ${KETTLE_HOME}/simple-jndi

ENV \
    PDI_INITIAL_MEMORY="1G" \
    PDI_MAXIMUM_MEMORY="2G" \
    PDI_MAXIMUM_PERMSIZE="256m"

# ---------------
# Pentaho Data Integration
# ---------------

WORKDIR ${KETTLE_HOME}

# PDI
COPY Dockerfile ext/pdi-ce-9.1.0.0-324* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pdi-ce-9.1.0.0-324.zip ] \
    || wget -P ${KETTLE_HOME} -q https://sourceforge.net/projects/pentaho/files/Pentaho%209.1/client-tools/pdi-ce-9.1.0.0-324.zip) \
  && unzip -qo ${KETTLE_HOME}/pdi-ce-9.1.0.0-324.zip -d ${KETTLE_HOME} \
  && mv ${KETTLE_HOME}/data-integration/* ${KETTLE_HOME} \
  && rmdir ${KETTLE_HOME}/data-integration \
  && rm -f ${KETTLE_HOME}/pdi-ce-9.1.0.0-324.zip \
  && chmod +x ${KETTLE_HOME}/*.sh \
  && rm -f Dockerfile # cleaning \
  && mkdir -p ${KETTLE_HOME}/.kettle \
  && chown -R pdi:pdi ${KETTLE_HOME}

# Plugin GIS
COPY Dockerfile ext/pentaho-gis-plugins-1.4.0-bin-9.zip* ${KETTLE_HOME}/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/pentaho-gis-plugins-1.4.0-bin-9.zip ] \
    || wget -P ${KETTLE_HOME} -q https://github.com/atolcd/pentaho-gis-plugins/releases/download/v1.4.0/pentaho-gis-plugins-1.4.0-bin-9.zip) \
  && unzip ${KETTLE_HOME}/pentaho-gis-plugins-1.4.0-bin-9.zip -d ${KETTLE_HOME}/plugins \
  && rm -f ${KETTLE_HOME}/pentaho-gis-plugins-1.4.0-bin-9.zip \
  && rm -f Dockerfile # cleaning

# Mise à jour de JDBC driver
RUN rm -f ${KETTLE_HOME}/lib/postgresql-*
COPY Dockerfile ext/postgresql-42.2.19.jar* ${KETTLE_HOME}/lib/
RUN set -x \
  && ([ -f ${KETTLE_HOME}/lib/postgresql-42.2.19.jar ] \
    || wget -P ${KETTLE_HOME}/lib -q https://jdbc.postgresql.org/download/postgresql-42.2.19.jar) \
  && rm -f Dockerfile # cleaning

ENV PATH ${KETTLE_HOME}/kitchen.sh:$PATH

# Version des traitements dans le fichier /scripts/version.txt
ARG APP_VERSION=dev
RUN echo "${APP_VERSION}" > /scripts/version.txt

# Droits
RUN chown -R pdi:pdi /scripts \
  && chown -R pdi:pdi /jobs

USER pdi

ENV SHELL /bin/sh
ENTRYPOINT ["/scripts/entrypoint.sh"]
