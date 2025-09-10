FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install bash and other dependencies for Gradle
RUN apk add --no-cache bash curl

# Copy entire project structure
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Clean any existing cache and build
RUN ./gradlew clean --no-daemon --no-configuration-cache
RUN ./gradlew bootJar -x validateStructure --no-daemon --no-configuration-cache --stacktrace

FROM eclipse-temurin:21-jre-alpine

# Install bash and curl for health checks
RUN apk add --no-cache bash curl

WORKDIR /app

COPY --from=builder /app/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]