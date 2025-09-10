FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install bash and other dependencies for Gradle
RUN apk add --no-cache bash

# Copy Gradle configuration files
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY main.gradle .
COPY gradle.properties .

# Copy all source code and build files
COPY domain/ domain/
COPY infrastructure/ infrastructure/
COPY applications/ applications/

# Ensure all build.gradle files are present
COPY domain/model/build.gradle domain/model/
COPY domain/usecase/build.gradle domain/usecase/
COPY applications/app-service/build.gradle applications/app-service/
COPY infrastructure/entry-points/reactive-web/build.gradle infrastructure/entry-points/reactive-web/
COPY infrastructure/driven-adapters/mongo-repository/build.gradle infrastructure/driven-adapters/mongo-repository/

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