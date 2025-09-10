FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install bash and other dependencies for Gradle
RUN apk add --no-cache bash

COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY main.gradle .

COPY domain/ domain/
COPY infrastructure/ infrastructure/
COPY applications/ applications/

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew bootJar --no-daemon --stacktrace

FROM eclipse-temurin:21-jre-alpine

# Install bash for potential startup scripts
RUN apk add --no-cache bash

WORKDIR /app

COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]