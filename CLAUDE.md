# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application implementing Clean Architecture for franchise management. The system manages franchises, branches, and products using reactive programming patterns with WebFlux and MongoDB.

## Architecture Structure

The project follows Clean Architecture principles with these key modules:

- **domain/model** - Core business entities (Product, Franchise, Branch)
- **domain/usecase** - Business logic organized by entity (franchise, branch, product use cases)
- **infrastructure/entry-points/reactive-web** - REST API endpoints using Spring WebFlux
- **infrastructure/driven-adapters/mongo-repository** - MongoDB persistence layer
- **applications/app-service** - Main application assembly and configuration

## Key Technologies

- Java 21
- Spring Boot 3.5.4
- Spring WebFlux (reactive web stack)
- MongoDB with reactive repositories
- Project Reactor
- Lombok for boilerplate reduction
- Clean Architecture Plugin (Bancolombia)

## Development Commands

### Building and Running
```bash
# Build the entire project
./gradlew build

# Run the application (starts on port 8080)
./gradlew :app-service:bootRun

# Create executable JAR
./gradlew bootJar
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :reactive-web:test
./gradlew :usecase:test

# Run tests with coverage
./gradlew jacocoTestReport

# Run mutation testing
./gradlew pitest

# Generate merged coverage report
./gradlew jacocoMergedReport
```

### Code Quality
```bash
# Validate architecture constraints
./gradlew validateStructure

# Run SonarQube analysis
./gradlew sonar
```

## Architecture Patterns

### Clean Architecture Layers
- **Entities/Models**: Pure business objects in `domain/model`
- **Use Cases**: Business logic in `domain/usecase/in` (interfaces) 
- **Interface Adapters**: Controllers in `infrastructure/entry-points`, repositories in `infrastructure/driven-adapters`
- **Frameworks**: Spring Boot configuration in `applications/app-service`

### Reactive Programming
- All endpoints return `Mono<T>` or `Flux<T>`
- Repository operations are reactive using Spring Data MongoDB Reactive
- Handler-based routing instead of controller annotations

### API Structure
Routes are defined in `RouterRest.java` with pattern:
- Base path: `/api/v1/franchise`
- RESTful endpoints for franchise, branch, and product operations
- Nested routes following the franchise → branch → product hierarchy

### Dependency Flow
- Use cases define interfaces (`domain/usecase/in`)
- Implementations are in respective adapter modules
- Dependency injection configured automatically via `@ComponentScan`

## Configuration

- **Application config**: `applications/app-service/src/main/resources/application.yaml`
- **MongoDB URI**: `mongodb://localhost:27017/test`
- **Server port**: 8080
- **CORS**: Configured for localhost:4200 and localhost:8080

## Testing Strategy

- Architecture tests verify Clean Architecture constraints
- Unit tests for use cases and handlers
- Integration tests for web layer
- Mutation testing with Pitest for code quality validation

## Module Dependencies

The project uses Gradle multi-module structure where:
- `app-service` depends on all other modules
- `reactive-web` depends on `usecase` and `model`
- `mongo-repository` depends on `model`
- `usecase` depends on `model`
- No circular dependencies allowed (enforced by architecture tests)