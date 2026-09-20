# Docker Compose and Orchestration Basics — TeleMed IA

> This document describes the Docker Compose implementation used to run the current TeleMed IA MVP as a reproducible multi-container system.
>
> The purpose of this implementation is to start the frontend, backend, and PostgreSQL database together, manage their dependencies, persist database information, externalize runtime configuration, and establish a local orchestration baseline for the future migration toward independent microservices.

---

## 1. Objective

TeleMed IA uses Docker and Docker Compose to execute the current MVP through containers.

The main objective is to avoid manually starting each component independently.

The current system can be started with:

```bash
docker compose up --build
```

The Compose configuration is responsible for:

- Building the Angular/Ionic frontend container.
- Building the Spring Boot backend container.
- Starting PostgreSQL.
- Connecting the containers through the Docker Compose network.
- Waiting for PostgreSQL to become healthy before starting the backend.
- Persisting PostgreSQL data.
- Injecting runtime configuration through environment variables.
- Exposing the required application ports.
- Restarting containers when required.

---

## 2. Current TeleMed IA Container Architecture

The current MVP still uses a modular monolith in the backend.

Docker Compose does not change that architectural model.

Instead, it packages the existing system into independently running containers:

```text
                         User
                          │
                          ▼
                ┌─────────────────┐
                │ Angular / Ionic │
                │    Frontend     │
                │      :4200      │
                └────────┬────────┘
                         │
                         │ HTTP / REST
                         ▼
                ┌─────────────────┐
                │   Spring Boot   │
                │     Backend     │
                │      :8080      │
                └────────┬────────┘
                         │
                         │ JDBC
                         ▼
                ┌─────────────────┐
                │   PostgreSQL    │
                │      :5432      │
                └─────────────────┘
```

The current Docker Compose system contains three services:

```text
postgres
backend
frontend
```

The Intelligent Agent's external AI provider is not another local container.

It is an external dependency consumed by the backend through the Intelligent Agent infrastructure adapter.

---

## 3. Project Docker Structure

The relevant project structure is:

```text
telemed-ai/
│
├── docker-compose.yml
│
├── telemedai-backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
└── telemedai-frontend/
    ├── Dockerfile
    ├── package.json
    └── src/
```

The root `docker-compose.yml` coordinates the three application containers.

Each application has its own Dockerfile because the frontend and backend have different build and runtime requirements.

---

# 4. Docker Compose Implementation

The current `docker-compose.yml` is:

```yaml
services:

  postgres:
    image: postgres:16-alpine
    container_name: telemed-postgres
    restart: unless-stopped

    environment:
      POSTGRES_DB: ${POSTGRES_DB:-telemed}
      POSTGRES_USER: ${POSTGRES_USER:-telemed}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-telemed}

    ports:
      - "5432:5432"

    volumes:
      - telemed_pgdata:/var/lib/postgresql/data

    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-telemed} -d ${POSTGRES_DB:-telemed}"]
      interval: 10s
      timeout: 5s
      retries: 5


  backend:
    build:
      context: ./telemedai-backend
      dockerfile: Dockerfile

    container_name: telemed-backend
    restart: unless-stopped

    depends_on:
      postgres:
        condition: service_healthy

    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-telemed}
      DB_USERNAME: ${POSTGRES_USER:-telemed}
      DB_PASSWORD: ${POSTGRES_PASSWORD:-telemed}
      JWT_SECRET: ${JWT_SECRET}
      CORS_ORIGINS: ${CORS_ORIGINS:-http://localhost:4200}

    ports:
      - "8080:8080"


  frontend:
    build:
      context: ./telemedai-frontend
      dockerfile: Dockerfile

    container_name: telemed-frontend
    restart: unless-stopped

    depends_on:
      - backend

    ports:
      - "4200:4200"


volumes:
  telemed_pgdata:
```

---

# 5. PostgreSQL Container

PostgreSQL is defined as:

```yaml
postgres:
  image: postgres:16-alpine
  container_name: telemed-postgres
  restart: unless-stopped
```

The project uses:

```text
postgres:16-alpine
```

because the application database is PostgreSQL 16.

The Alpine variant provides a smaller base image than a standard Linux-based PostgreSQL image.

---

## 5.1 PostgreSQL Configuration

Database configuration is externalized:

```yaml
environment:
  POSTGRES_DB: ${POSTGRES_DB:-telemed}
  POSTGRES_USER: ${POSTGRES_USER:-telemed}
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-telemed}
```

Docker Compose reads the values from the execution environment.

If a value is not provided, the development defaults are:

```text
POSTGRES_DB=telemed
POSTGRES_USER=telemed
POSTGRES_PASSWORD=telemed
```

The syntax:

```text
${VARIABLE:-default}
```

means:

```text
Environment variable exists
        ↓
Use environment value

Environment variable does not exist
        ↓
Use default value
```

This avoids hard-coding environment-specific configuration directly into the application source code.

---

# 6. Persistent Database Volume

The PostgreSQL container uses:

```yaml
volumes:
  - telemed_pgdata:/var/lib/postgresql/data
```

and the named volume is declared at the end of the Compose file:

```yaml
volumes:
  telemed_pgdata:
```

Without this volume, database information could be lost when the PostgreSQL container is recreated.

The resulting behavior is:

```text
PostgreSQL container
       │
       ▼
/var/lib/postgresql/data
       │
       ▼
telemed_pgdata
       │
       ▼
Persistent Docker volume
```

Therefore:

```bash
docker compose down
```

stops and removes the containers but preserves the database volume.

However:

```bash
docker compose down -v
```

also deletes the volume.

The `-v` option should only be used when a complete local database reset is intentionally required.

---

# 7. PostgreSQL Health Check

A key orchestration improvement implemented in TeleMed IA is the PostgreSQL health check.

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-telemed} -d ${POSTGRES_DB:-telemed}"]
  interval: 10s
  timeout: 5s
  retries: 5
```

The command:

```bash
pg_isready
```

checks whether PostgreSQL is actually ready to accept connections.

This is different from only checking whether the container has started.

The container may exist in this state:

```text
Container started
      │
      ▼
PostgreSQL initializing
      │
      ▼
Not ready yet
```

The health check changes this into:

```text
PostgreSQL container starts
        │
        ▼
pg_isready
        │
        ├── not ready
        │      ↓
        │     retry
        │
        ▼
PostgreSQL accepts connections
        │
        ▼
     HEALTHY
```

The configured values are:

```text
Check interval: 10 seconds
Timeout:         5 seconds
Retries:         5
```

---

# 8. Why `depends_on` Was Improved with a Health Condition

A simple:

```yaml
depends_on:
  - postgres
```

would only control container startup order.

It would mean:

```text
Start PostgreSQL container
        ↓
Start backend container
```

but it would not guarantee:

```text
PostgreSQL is ready to accept connections
```

TeleMed IA instead uses:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

Therefore the backend waits for the PostgreSQL health check.

The sequence becomes:

```text
docker compose up
       │
       ▼
Start PostgreSQL
       │
       ▼
Run pg_isready
       │
       ├── UNHEALTHY
       │       ↓
       │      wait
       │
       ▼
     HEALTHY
       │
       ▼
Start Spring Boot backend
```

This solves a common distributed-system startup problem where the application tries to connect to the database before PostgreSQL has finished initializing.

---

# 9. Backend Container

The backend Compose configuration is:

```yaml
backend:
  build:
    context: ./telemedai-backend
    dockerfile: Dockerfile

  container_name: telemed-backend
  restart: unless-stopped

  depends_on:
    postgres:
      condition: service_healthy

  environment:
    SPRING_PROFILES_ACTIVE: prod
    DB_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-telemed}
    DB_USERNAME: ${POSTGRES_USER:-telemed}
    DB_PASSWORD: ${POSTGRES_PASSWORD:-telemed}
    JWT_SECRET: ${JWT_SECRET}
    CORS_ORIGINS: ${CORS_ORIGINS:-http://localhost:4200}

  ports:
    - "8080:8080"
```

The backend is built from:

```text
./telemedai-backend/Dockerfile
```

and exposes:

```text
8080
```

to the host machine.

Therefore the backend can be accessed locally through:

```text
http://localhost:8080
```

---

# 10. Backend Dockerfile

The Spring Boot backend uses a multi-stage Docker build:

```dockerfile
# Imagen multi-etapa para compilar y ejecutar el backend.
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -q -DskipTests package


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/telemed-backend-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

## 10.1 Build Stage

The first stage uses:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build
```

This image contains:

```text
Maven
+
Java 17 JDK
```

and is only used to compile the application.

The working directory is:

```dockerfile
WORKDIR /app
```

Then the Maven configuration is copied:

```dockerfile
COPY pom.xml .
```

Dependencies are downloaded before copying the source code:

```dockerfile
RUN mvn -q -DskipTests dependency:go-offline
```

This helps Docker reuse its build cache when dependencies have not changed.

Then the source code is copied:

```dockerfile
COPY src ./src
```

and packaged:

```dockerfile
RUN mvn -q -DskipTests package
```

The result is the Spring Boot JAR:

```text
telemed-backend-1.0.0.jar
```

---

## 10.2 Runtime Stage

The second stage starts with:

```dockerfile
FROM eclipse-temurin:17-jre
```

This stage does not contain Maven.

It only contains the Java runtime required to execute the already compiled application.

The generated JAR is copied from the build stage:

```dockerfile
COPY --from=build /app/target/telemed-backend-1.0.0.jar app.jar
```

The application exposes:

```dockerfile
EXPOSE 8080
```

and runs through:

```dockerfile
ENTRYPOINT ["java","-jar","app.jar"]
```

The complete process is:

```text
Maven build image
        │
        ▼
Compile project
        │
        ▼
Generate JAR
        │
        ▼
Java runtime image
        │
        ▼
Copy only JAR
        │
        ▼
Run application
```

This is preferable to running Maven inside the production container because the final runtime image does not need the complete build toolchain.

---

# 11. Container-to-Container Database Communication

One of the most important details in the Compose configuration is:

```yaml
DB_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-telemed}
```

The hostname is:

```text
postgres
```

not:

```text
localhost
```

This is because `localhost` inside the backend container refers to the backend container itself.

The desired communication is:

```text
telemed-backend
       │
       │ jdbc:postgresql://postgres:5432/telemed
       ▼
telemed-postgres
```

Docker Compose automatically creates a default shared network for the services in the project.

Inside that network, Docker provides DNS resolution using the Compose service names.

Therefore:

```text
postgres
```

is resolved to the PostgreSQL container.

No hard-coded container IP address is required.

---

# 12. Shared Docker Network

The current Compose file does not manually declare a custom network.

Docker Compose automatically creates a project-scoped default network.

Conceptually:

```text
Docker Compose default network
│
├── postgres
├── backend
└── frontend
```

This allows the containers to discover each other using service names.

For example:

```text
backend → postgres
```

is resolved by Docker's internal service discovery.

This satisfies the local requirement for a shared service network without manually assigning container IP addresses.

---

# 13. Backend Runtime Configuration

The backend receives configuration through environment variables:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: prod
  DB_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-telemed}
  DB_USERNAME: ${POSTGRES_USER:-telemed}
  DB_PASSWORD: ${POSTGRES_PASSWORD:-telemed}
  JWT_SECRET: ${JWT_SECRET}
  CORS_ORIGINS: ${CORS_ORIGINS:-http://localhost:4200}
```

These variables configure different responsibilities.

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Select the Spring Boot configuration profile |
| `DB_URL` | PostgreSQL JDBC connection |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `CORS_ORIGINS` | Allowed frontend origin |

The backend currently runs the:

```text
prod
```

Spring profile inside Docker.

---

# 14. Secret Externalization

The JWT signing secret is not written directly into the Compose file.

Instead:

```yaml
JWT_SECRET: ${JWT_SECRET}
```

Docker Compose expects the value to be supplied by the execution environment.

This follows the security rule:

```text
Secret
   ↓
Environment
   ↓
Docker Compose
   ↓
Backend container
```

rather than:

```text
Secret
   ↓
Git repository
```

Sensitive values must not be committed to the repository.

Examples include:

```text
JWT_SECRET
Database production password
External AI API keys
Email provider credentials
Access tokens
Refresh tokens
```

---

# 15. Spring Profiles

The project already separates Spring configuration using profiles.

The backend Compose configuration activates:

```yaml
SPRING_PROFILES_ACTIVE: prod
```

This causes Spring Boot to load the production profile configuration together with the base application configuration.

Conceptually:

```text
application.yml
      │
      ├── common configuration
      │
      ▼
application-prod.yml
      │
      └── production-specific configuration
```

During normal local development outside Docker, the development profile can still be used.

This separates environment-specific application behavior from source code.

---

# 16. Frontend Container

The frontend Compose configuration is:

```yaml
frontend:
  build:
    context: ./telemedai-frontend
    dockerfile: Dockerfile

  container_name: telemed-frontend
  restart: unless-stopped

  depends_on:
    - backend

  ports:
    - "4200:4200"
```

The frontend image is built from:

```text
./telemedai-frontend/Dockerfile
```

and exposes:

```text
4200
```

to the host machine.

The application becomes accessible at:

```text
http://localhost:4200
```

---

# 17. Frontend Dockerfile

The Angular/Ionic frontend uses:

```dockerfile
FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

EXPOSE 4200

CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
```

---

## 17.1 Node Runtime

The image starts from:

```dockerfile
FROM node:20-alpine
```

The container therefore uses Node.js 20.

The Alpine variant reduces the base image size.

---

## 17.2 Working Directory

The frontend is executed inside:

```dockerfile
WORKDIR /app
```

---

## 17.3 Dependency Installation

The dependency descriptor files are copied first:

```dockerfile
COPY package*.json ./
```

Then:

```dockerfile
RUN npm ci
```

is executed.

`npm ci` installs the dependency versions defined by the project lock file and is appropriate for reproducible container builds.

After dependencies are installed, the application source is copied:

```dockerfile
COPY . .
```

---

## 17.4 Frontend Port

The container exposes:

```dockerfile
EXPOSE 4200
```

The Angular development server is started with:

```dockerfile
CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
```

The important option is:

```text
--host 0.0.0.0
```

Without it, the development server could listen only on the container's local interface and not be reachable through Docker's port mapping.

The final flow is:

```text
Host browser
      │
      │ localhost:4200
      ▼
Docker port mapping
      │
      ▼
telemed-frontend
      │
      ▼
Angular development server
0.0.0.0:4200
```

---

# 18. Frontend Dependency on Backend

The frontend contains:

```yaml
depends_on:
  - backend
```

This means Docker Compose starts the backend container before the frontend container.

However, there is an important distinction:

```text
depends_on
    ≠
application readiness
```

The current configuration controls startup order for the frontend but does not currently wait for a backend health status.

The stronger readiness mechanism is currently implemented specifically between:

```text
postgres
    ↓
backend
```

through:

```yaml
condition: service_healthy
```

This distinction is important when reasoning about distributed startup.

---

# 19. Restart Policy

All three services use:

```yaml
restart: unless-stopped
```

This means Docker will attempt to restart a container when it terminates unexpectedly, except when it was intentionally stopped.

It is configured for:

```text
telemed-postgres
telemed-backend
telemed-frontend
```

This provides basic single-host container recovery.

It should not be confused with full distributed orchestration or multi-host self-healing.

---

# 20. Current Startup Sequence

The current Compose behavior is approximately:

```text
docker compose up
        │
        ▼
Build frontend image
        │
Build backend image
        │
        ▼
Start PostgreSQL
        │
        ▼
PostgreSQL healthcheck
        │
        ├── not ready
        │      ↓
        │     retry
        │
        ▼
      healthy
        │
        ▼
Start backend
        │
        ▼
Start frontend after backend container startup
        │
        ▼
TeleMed IA available
```

The most important readiness guarantee is:

```text
PostgreSQL HEALTHY
        ↓
Backend starts
```

---

# 21. Running TeleMed IA

From the project root:

```bash
docker compose up --build
```

This command:

1. Reads `docker-compose.yml`.
2. Builds the backend image.
3. Builds the frontend image.
4. Starts PostgreSQL.
5. Creates the persistent volume when required.
6. Creates the default Compose network.
7. Waits for PostgreSQL health.
8. Starts the backend.
9. Starts the frontend.
10. Connects the services.

---

## Run in Detached Mode

```bash
docker compose up --build -d
```

The `-d` option runs the containers in the background.

---

# 22. Verifying the Containers

Use:

```bash
docker compose ps
```

The expected services are:

```text
telemed-postgres
telemed-backend
telemed-frontend
```

The database should eventually report a healthy state.

---

# 23. Viewing Logs

All services:

```bash
docker compose logs
```

Follow logs continuously:

```bash
docker compose logs -f
```

Backend:

```bash
docker compose logs backend
```

PostgreSQL:

```bash
docker compose logs postgres
```

Frontend:

```bash
docker compose logs frontend
```

These commands are useful for detecting startup problems and communication failures.

---

# 24. Stopping the System

To stop and remove the containers:

```bash
docker compose down
```

The database volume remains available.

To also remove persistent data:

```bash
docker compose down -v
```

The second command should only be executed when a complete database reset is intended.

---

# 25. Validating the Compose Configuration

Before starting the containers, the final Compose configuration can be validated with:

```bash
docker compose config
```

This command is useful for checking:

- YAML syntax.
- Environment variable interpolation.
- Service definitions.
- Volume definitions.
- Build configuration.

---

# 26. Service Discovery

Docker Compose provides service discovery inside its default network.

The backend does not need to know:

```text
PostgreSQL container IP
```

Instead it uses:

```text
postgres
```

because that is the Compose service name.

This:

```yaml
DB_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-telemed}
```

implements the communication:

```text
backend
   │
   │ DNS lookup: postgres
   ▼
postgres container
```

This is more portable than using fixed container IP addresses.

---

# 27. Activity Requirement: Multi-Service System

The activity requires composing a multi-service system.

TeleMed IA implements this with:

```text
docker-compose.yml
│
├── postgres
├── backend
└── frontend
```

Therefore a single Compose configuration represents the complete current local system.

---

# 28. Activity Requirement: Shared Network

No custom network is currently declared manually.

Docker Compose automatically creates a default network for the project and connects all services to it.

Therefore:

```text
frontend
backend
postgres
```

share a Docker Compose network.

Service-name DNS is used for internal communication.

The clearest example is:

```text
backend → postgres:5432
```

---

# 29. Activity Requirement: Health Checks

PostgreSQL implements a real readiness health check:

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-telemed} -d ${POSTGRES_DB:-telemed}"]
  interval: 10s
  timeout: 5s
  retries: 5
```

The backend depends on that result:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

This demonstrates the difference between:

```text
container started
```

and:

```text
dependency ready
```

---

# 30. Activity Requirement: Configuration Externalization

The Compose configuration uses environment variable interpolation.

Examples:

```yaml
POSTGRES_DB: ${POSTGRES_DB:-telemed}
POSTGRES_USER: ${POSTGRES_USER:-telemed}
POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-telemed}
```

and:

```yaml
JWT_SECRET: ${JWT_SECRET}
CORS_ORIGINS: ${CORS_ORIGINS:-http://localhost:4200}
```

This separates runtime configuration from application source code.

---

# 31. Activity Requirement: Persistent Data

Persistence is implemented through:

```yaml
volumes:
  telemed_pgdata:
```

and:

```yaml
volumes:
  - telemed_pgdata:/var/lib/postgresql/data
```

This ensures that PostgreSQL information survives normal container recreation.

---

# 32. Activity Requirement: Startup Dependency Management

The current dependency chain is:

```text
postgres
   │
   │ condition: service_healthy
   ▼
backend
   │
   │ depends_on
   ▼
frontend
```

The database/backend relationship uses readiness-aware startup.

The backend/frontend relationship currently controls only container startup order.

This distinction is intentionally documented because startup order and service readiness are not equivalent concepts.

---

# 33. Compose Is Local Orchestration, Not Full Cluster Orchestration

Docker Compose solves the current development need for:

- Multiple containers.
- Shared networking.
- Environment configuration.
- Persistent storage.
- Startup dependencies.
- Database readiness.
- Container restart policy.
- Reproducible local execution.

However, the system still runs on one Docker host.

```text
Docker Host
│
├── telemed-postgres
├── telemed-backend
└── telemed-frontend
```

Docker Compose is therefore appropriate for:

```text
Local development
Integration testing
Team environments
Single-host execution
```

---

# 34. What an Orchestrator Adds

When TeleMed IA evolves toward independently deployed microservices, the system will require stronger orchestration capabilities.

An orchestrator can provide:

- Multi-host execution.
- Service scheduling.
- Replica management.
- Self-healing.
- Rolling deployments.
- Autoscaling.
- Load balancing.
- Centralized health management.
- Controlled service deployment.

The project plans to use AWS infrastructure in its later deployment stage.

The target direction is based on container orchestration using Amazon ECS and AWS Fargate.

Conceptually:

```text
Docker Compose
      │
      │ local development
      ▼
Containers
      │
      │ future production evolution
      ▼
Amazon ECS
      │
      ▼
AWS Fargate
```

The Docker images created today can therefore become part of the future cloud deployment workflow.

---

# 35. Relationship with the Microservices Migration

The current backend is a modular monolith.

```text
telemed-backend
│
├── auth
├── patient
├── professional
├── agent
├── appointment
├── consultation
└── other modules
```

The future architecture progressively extracts these capabilities into services.

The Docker concepts implemented now remain applicable.

For example:

```text
CURRENT

Docker Compose
│
├── frontend
├── backend
└── postgres
```

may later evolve toward:

```text
FUTURE LOCAL DISTRIBUTED TOPOLOGY

Docker Compose
│
├── frontend
├── api-gateway
├── auth-service
├── patient-service
├── professional-service
├── agent-service
├── appointment-service
├── consultation-service
├── notification-service
├── document-service
└── service-owned databases
```

Therefore the current Docker Compose implementation is not disposable work.

It establishes the local container and orchestration practices that will later be applied to the microservices architecture.

---

# 36. Intelligent Agent Consideration

The Intelligent Agent currently communicates with an external AI provider.

The expected architecture is:

```text
Frontend
   │
   ▼
Backend
   │
   ▼
AgentService
   │
   ▼
AgentModelPort
   │
   ▼
AI Provider Adapter
   │
   ▼
External AI Provider
```

The AI provider does not need to run inside Docker Compose because it is an external API.

The backend remains the only component allowed to hold the private provider credential.

The frontend must never receive the AI API key.

---

# 37. Current Configuration Limitation for the AI Provider

The current `docker-compose.yml` explicitly injects:

```text
SPRING_PROFILES_ACTIVE
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
CORS_ORIGINS
```

but it does not currently inject AI-specific variables such as:

```text
AI_PROVIDER
GROQ_API_KEY
GROQ_MODEL
```

Therefore, if the real AI provider must also work when the backend is started entirely through Docker Compose, these variables must be explicitly passed to the backend container.

For example, a future configuration improvement may include:

```yaml
environment:
  AI_PROVIDER: ${AI_PROVIDER:-fake}
  GROQ_API_KEY: ${GROQ_API_KEY:-}
  GROQ_MODEL: ${GROQ_MODEL:-openai/gpt-oss-20b}
```

This improvement would preserve the existing security principle:

```text
API key
   ↓
environment variable
   ↓
backend container
   ↓
external AI provider
```

without committing the real credential.

---

# 38. Current Readiness Limitation

The PostgreSQL-to-backend dependency is readiness-aware.

The frontend-to-backend dependency currently is:

```yaml
depends_on:
  - backend
```

This controls startup order but does not verify that Spring Boot is fully ready.

A future improvement can add a backend health check and change the frontend dependency to:

```yaml
depends_on:
  backend:
    condition: service_healthy
```

This would produce the stronger sequence:

```text
PostgreSQL HEALTHY
        ↓
Backend starts
        ↓
Backend HEALTHY
        ↓
Frontend starts
```

The current implementation already demonstrates readiness-aware dependency management for the critical database dependency, while this remains a possible improvement for the complete startup chain.

---

# 39. Environment Strategy

The backend currently runs in Compose using:

```text
SPRING_PROFILES_ACTIVE=prod
```

The project also maintains separate Spring configuration profiles.

This provides a foundation for environment-specific behavior.

The architecture can later evolve toward:

```text
application.yml
application-dev.yml
application-prod.yml
```

combined with environment variables and environment-specific deployment configuration.

The principle is:

```text
Same application code
       +
Different environment configuration
```

rather than embedding environment-specific credentials or addresses in the application source.

---

# 40. Common Problems Solved by This Implementation

## Problem 1 — Backend starts before PostgreSQL

Without readiness validation:

```text
Backend starts
      ↓
Database still initializing
      ↓
Connection refused
      ↓
Application fails
```

Implemented solution:

```text
PostgreSQL healthcheck
      ↓
service_healthy
      ↓
Backend starts
```

---

## Problem 2 — Hard-coded database IP

Incorrect:

```text
jdbc:postgresql://172.18.0.3:5432/telemed
```

Implemented:

```text
jdbc:postgresql://postgres:5432/telemed
```

Docker service discovery resolves the database service automatically.

---

## Problem 3 — Database lost after container recreation

Without a volume:

```text
Container removed
      ↓
Database filesystem removed
```

Implemented:

```text
PostgreSQL
      ↓
telemed_pgdata
      ↓
Persistent data
```

---

## Problem 4 — Application-specific configuration embedded in images

Instead of embedding credentials in the Docker image, runtime values are passed through Compose environment configuration.

Example:

```yaml
JWT_SECRET: ${JWT_SECRET}
```

---

# 41. Implementation Verification

The implementation can be verified with the following sequence.

### Validate Docker Compose

```bash
docker compose config
```

### Build the images

```bash
docker compose build
```

### Start the system

```bash
docker compose up
```

or:

```bash
docker compose up -d
```

### Verify containers

```bash
docker compose ps
```

### Verify PostgreSQL health

```bash
docker inspect telemed-postgres
```

### Review backend startup

```bash
docker compose logs backend
```

### Review frontend startup

```bash
docker compose logs frontend
```

### Review database startup

```bash
docker compose logs postgres
```

### Stop the environment

```bash
docker compose down
```

---

# 42. Expected Local Endpoints

After successful startup:

| Component | Local Address |
|---|---|
| Angular/Ionic frontend | `http://localhost:4200` |
| Spring Boot backend | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |

If Springdoc is enabled, Swagger is available through the backend configuration at:

```text
http://localhost:8080/swagger-ui.html
```

---

# 43. Activity Coverage

| Activity Requirement | TeleMed IA Implementation | Status |
|---|---|---|
| Multi-service system | `frontend`, `backend`, `postgres` | ✅ Implemented |
| Shared network | Docker Compose default project network | ✅ Implemented |
| Service-name DNS | Backend connects to `postgres` | ✅ Implemented |
| Startup order | `depends_on` | ✅ Implemented |
| Readiness validation | PostgreSQL `pg_isready` health check | ✅ Implemented |
| Health-gated dependency | Backend waits for `postgres: service_healthy` | ✅ Implemented |
| Environment configuration | Compose environment variables | ✅ Implemented |
| Persistent database | `telemed_pgdata` named volume | ✅ Implemented |
| Container restart strategy | `restart: unless-stopped` | ✅ Implemented |
| Reproducible backend image | Multi-stage Maven/Java Dockerfile | ✅ Implemented |
| Reproducible frontend image | Node 20 Dockerfile | ✅ Implemented |
| Hard-coded IP avoidance | Docker service names | ✅ Implemented |
| Full backend readiness gating for frontend | Backend currently has no Compose health condition | ⚠️ Improvement |
| AI variables passed through Compose | Not present in current Compose file | ⚠️ Improvement |
| Multiple Compose override files | Not present in current implementation | ⚠️ Future improvement |
| Multi-host orchestration | Future AWS orchestration stage | 🔜 Planned |

---

# 44. Main Technical Learning

The most important lesson from this implementation is that:

```text
Container started
        ≠
Service ready
```

TeleMed IA solves this for its critical database dependency through:

```text
PostgreSQL
   │
   ▼
healthcheck
   │
   ▼
service_healthy
   │
   ▼
backend
```

Another important principle is:

```text
Container IP
     ✗

Service name
     ✓
```

The backend therefore connects to:

```text
postgres:5432
```

rather than relying on a fixed IP address.

Finally:

```text
Application data
     ↓
Named volume
```

and:

```text
Runtime configuration
     ↓
Environment variables
```

keep the system reproducible and configurable.

---

# 45. Final Result

The TeleMed IA MVP can currently be represented as a complete local container system:

```text
                       Docker Compose
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                 │
           ▼                 ▼                 ▼
     PostgreSQL          Spring Boot      Angular/Ionic
        :5432               :8080             :4200
           │                 │
           │                 │
     named volume      external config
           │                 │
           └──── HEALTHY ────┘
```

The implementation provides:

- Reproducible container builds.
- Multi-container execution.
- Database readiness validation.
- Controlled startup dependency.
- Persistent database storage.
- Environment-based configuration.
- Docker service discovery.
- Basic container restart behavior.
- A foundation for future distributed microservices.
- A foundation for the planned AWS container orchestration stage.

The current Docker Compose topology corresponds to the modular-monolith MVP, while the same containerization principles will be reused as TeleMed IA progressively evolves toward independent microservices.

---

## Related Documentation

- Security rules → `00-governance/security-rules.md`
- Domain Map → `02-domain/domain-map.md`
- Domain Events → `02-domain/domain-events.md`
- User Stories → `04-requirements/user-stories.md`
- Architecture Overview → `05-architecture/overview.md`
- Architecture Decisions → `05-architecture/decisions/records/`
- Data Architecture → `06-data/`
- API Contracts → `07-api/contracts/openapi/`
- Microservices Catalog → `09-microservices/service-catalog.md`
- Microservices Documentation → `09-microservices/`
- DevOps Documentation → `10-devops/`
- Operations → `13-operations/`