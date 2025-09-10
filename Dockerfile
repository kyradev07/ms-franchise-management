FROM openjdk:21-jdk-slim

WORKDIR /app

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

COPY domain/ domain/
COPY infrastructure/ infrastructure/
COPY applications/ applications/

RUN chmod +x gradlew

RUN ./gradlew bootJar --no-daemon

FROM openjdk:21-jre-slim

WORKDIR /app

COPY --from=0 /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]