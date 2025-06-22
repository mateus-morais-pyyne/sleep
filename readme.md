# Sleep Service Application

The Sleep Service Application is a robust Jakarta EE-based project designed to provide a RESTful API for managing and tracking sleep data. Built with Spring Data JPA, Spring MVC, Java 11, and Kotlin 1.6, the application is container-ready and leverages best practices for modern backend development.

## Features

- RESTful API for managing sleep records and related entities
- Persistence layer using Spring Data JPA
- Structured, modular codebase using Jakarta EE standards
- Kotlin-based implementation
- Docker support for easy deployment
- Configurable via standard Spring and Jakarta mechanisms
- Example OpenAPI specification for integration: `SleepServiceAPI.json`

## Getting Started

### Prerequisites

- Java 11
- Docker (optional, for container-based deployment)
- Gradle

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd sleep
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API:**
   By default, the API will be available at `http://localhost:8080/`.

### Using Docker

1. **Build and run the containerized application:**
   ```bash
   docker-compose up --build
   ```

2. **Stop the services:**
   ```bash
   docker-compose down
   ```

## API Documentation

- An OpenAPI/Swagger specification is provided in `SleepServiceAPI.json`.
- Import the JSON file into [Postman](https://www.postman.com/) for interactive testing.

---

**Tip:** For optimal development, use IntelliJ IDEA Ultimate Edition and sync Gradle dependencies.