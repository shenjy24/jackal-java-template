FROM eclipse-temurin:21-jre

ARG APP_UID=1000
ARG APP_GID=1000
ARG JAR_FILE=app.jar

WORKDIR /app

RUN if ! getent group ${APP_GID} >/dev/null; then groupadd -g ${APP_GID} app; fi \
    && if ! getent passwd ${APP_UID} >/dev/null; then useradd --no-log-init -u ${APP_UID} -g ${APP_GID} -d /app -s /usr/sbin/nologin app; fi \
    && mkdir -p /app/logs \
    && chown -R ${APP_UID}:${APP_GID} /app

COPY --chown=${APP_UID}:${APP_GID} ${JAR_FILE} app.jar

USER ${APP_UID}:${APP_GID}

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+UseContainerSupport", "-XX:InitialRAMPercentage=50.0", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/app/logs", "-jar", "app.jar"]
