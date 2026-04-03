FROM eclipse-temurin:21-jre-jammy

VOLUME /tmp

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y curl && \
    apt-get clean && \
    groupadd --system appgroup && \
    useradd --system appuser && \
    usermod -a -G appgroup appuser && \
    chown -R appuser:appgroup /tmp

COPY infrastructure/app-service/build/libs/app-service-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'

ENV TZ=America/Bogota
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar app.jar" ]