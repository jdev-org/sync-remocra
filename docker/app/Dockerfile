# --------------------
# Build artefacts
# --------------------
FROM maven:3-jdk-11 as app-builder
ARG APP_VERSION=
WORKDIR /app
USER root
ENV HOME /var/maven
ENV MAVEN_CONFIG /var/maven/.m2
ENV MAVEN_OPTS -Duser.home=/var/maven
COPY app /app/app
COPY db  /app/db
COPY doc /app/doc
COPY pom.xml /app
RUN mvn -DnewVersion=${APP_VERSION} versions:set -DgenerateBackupPoms=true \
  && mvn -DskipTests spotless:check clean package versions:revert
# --------------------
# Build runnable image
# --------------------
FROM openjdk:11-jre
RUN apt-get update -y && apt-get upgrade -y && apt-get install locales htop nano -y && rm -rf /var/lib/apt/lists/*

ENV LANG fr_FR.UTF-8
ENV TZ Europe/Paris

RUN echo "fr_FR.UTF-8 UTF-8" >> /etc/locale.gen && locale-gen fr_FR.UTF-8

WORKDIR /app
COPY docker/app/scripts/entrypoint.sh .
COPY --from=app-builder /app/app/target/lib/*.jar lib/
COPY --from=app-builder /app/app/target/syncremocra-app*.jar lib/

# Non root user
ARG UID=1000
ARG GID=1000
RUN groupadd -g ${GID} edp \
    && useradd -u ${UID} -g edp edp \
    && chown -R edp /app
USER edp

ENV CONFIG_FILE=/app/config/syncremocra.conf
ENV LOG_CONFIG_FILE=/app/config/log4j2.xml
# LOG_DIR et LOG_FILENAME peuvent être utilisés dans log4j2.xml.
ENV LOG_DIR=/app/log
ENV LOG_FILENAME=syncremocra.log

ENV GC_OPTS="-XX:+DisableExplicitGC"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["/app/entrypoint.sh"]
CMD [ "help" ]
