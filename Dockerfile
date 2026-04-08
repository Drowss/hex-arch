FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradle-cache /root/.gradle/wrapper/dists
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY main.gradle .
COPY domain domain
COPY infrastructure infrastructure

RUN chmod +x gradlew
RUN ./gradlew :app-service:bootJar --no-daemon -x test

FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app

COPY --from=builder /app/infrastructure/app-service/build/libs/app-service-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=America/Bogota
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70"

USER nonroot

ENTRYPOINT ["java","-XX:MaxRAMPercentage=70","-jar","app.jar"]