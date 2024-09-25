FROM postgres:13

# PostGIS
ENV POSTGIS_MAJOR 3
RUN apt-get update \
      && apt-cache showpkg postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
      && apt-get install -y --no-install-recommends \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
           postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR-scripts \
      && rm -rf /var/lib/apt/lists/*

# Locale fr_FR
RUN localedef -i fr_FR -c -f UTF-8 -A /usr/share/locale/locale.alias fr_FR.UTF-8
ENV LANG fr_FR.utf8

# laurenz/oracle_fdw
RUN apt-get update \
      && apt-get install -y --no-install-recommends \
           alien wget \
      && rm -rf /var/lib/apt/lists/*

ENV ROOTDIR /usr/local/src

WORKDIR $ROOTDIR/

# https://www.oracle.com/database/technologies/instant-client/linux-x86-64-downloads.html
COPY Dockerfile ext/oracle-instantclient-*.x86_64.rpm $ROOTDIR/
RUN set -x \
  && ([ -f ${ROOTDIR}/oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm ] \
    || wget -P ${ROOTDIR} -q https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm) \
  && ([ -f ${ROOTDIR}/oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm ] \
    || wget -P ${ROOTDIR} -q https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm) \
  && ([ -f ${ROOTDIR}/oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm ] \
    || wget -P ${ROOTDIR} -q https://download.oracle.com/otn_software/linux/instantclient/211000/oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm) \
  \
  && rm -f Dockerfile \
  && alien -i oracle-instantclient-sqlplus-21.1.0.0.0-1.x86_64.rpm \
  && alien -i oracle-instantclient-basic-21.1.0.0.0-1.x86_64.rpm \
  && alien -i oracle-instantclient-devel-21.1.0.0.0-1.x86_64.rpm

ENV ORACLE_HOME /usr/lib/oracle/21/client64
ENV PATH $PATH:$ORACLE_HOME/bin
ENV OCI_LIB_DIR $ORACLE_HOME/lib
ENV OCI_INC_DIR /usr/include/oracle/21/client64
ENV ENV LD_LIBRARY_PATH $LD_LIBRARY_PATH:$ORACLE_HOME/lib

RUN apt-get -y update && \
  apt-get -y install git libaio1 postgresql-server-dev-all &&  \
  rm -rf /var/lib/apt/lists/* && \
  git clone https://github.com/laurenz/oracle_fdw.git && \
  cd oracle_fdw && make && make install && \
  cd .. && rm -R oracle_fdw

RUN echo $OCI_LIB_DIR | tee /etc/ld.so.conf.d/oracle.conf && \
  ldconfig

WORKDIR /

#EXPOSE 1571