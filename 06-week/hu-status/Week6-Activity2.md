# Planning — Environments, Configuration Strategy and Orchestration

> This document defines how TeleMed IA will run across development, QA, and production environments, how runtime configuration and secrets are managed, how Git branches map to deployment environments, and how the orchestration work is divided into testable MVP 2 technical stories.
>
> The objective is to preserve the same application artifact across environments while changing only environment-specific configuration.

---

## 1. Objective

Corte 2 evolves TeleMed IA from a locally integrated MVP into a system prepared for controlled environment promotion and future distributed deployment.

The environment and orchestration strategy must guarantee that:

- The same application artifact can move between environments.
- Environment-specific values are not hard-coded in source code.
- Secrets never enter Git.
- Configuration names remain consistent across environments.
- QA validates the same artifact that will later reach production.
- Production does not require rebuilding application code.
- Docker Compose remains the reproducible local integration environment.
- The architecture remains compatible with the planned migration toward microservices.
- The project remains compatible with its future AWS deployment strategy.

The guiding principle is:

```text
Build once
    ↓
Configure per environment
    ↓
Validate
    ↓
Promote the same artifact
```

---

# 2. Current TeleMed IA Runtime

The current TeleMed IA MVP consists of:

```text
Angular / Ionic Frontend
          │
          ▼
Spring Boot Backend
Modular Monolith
          │
          ▼
PostgreSQL
```

The backend also integrates with an external AI provider through the Intelligent Agent infrastructure adapter.

The current local container topology is:

```text
Docker Compose
│
├── frontend
├── backend
└── postgres
```

Docker Compose provides the current local integration environment.

The future distributed architecture will progressively extract bounded contexts into independent microservices.

---

# 3. Environment Strategy

TeleMed IA defines three main runtime environments:

```text
develop
qa
prod
```

Each environment runs the same application code but uses different runtime configuration.

```text
                    SAME ARTIFACT

                         │
           ┌─────────────┼─────────────┐
           │             │             │
           ▼             ▼             ▼
       DEVELOP           QA           PROD
           │             │             │
           ▼             ▼             ▼
      dev config     qa config     prod config
```

Environment promotion must not require changing application source code.

---

# 4. Environment Definitions

## 4.1 Develop Environment

The `develop` environment is used for active development and early integration.

Its objectives are:

- Fast developer feedback.
- Local or development infrastructure.
- Feature integration.
- Backend/frontend integration.
- Docker Compose validation.
- Database migration validation.
- Intelligent Agent development.
- Early API testing.

Typical characteristics:

```text
Environment: develop
Git branch: dev
Data: disposable or synthetic
Logs: verbose enough for development
AI Provider: fake or real provider depending on test
Database: development PostgreSQL
Frontend origin: development URL
```

Development must not depend on production credentials.

Synthetic patient data should be preferred during development and testing.

---

## 4.2 QA Environment

The `qa` environment validates the integrated system before production promotion.

Its objectives are:

- Integration testing.
- Regression testing.
- API validation.
- Database migration validation.
- Authentication and authorization verification.
- Intelligent Agent flow validation.
- Environment configuration validation.
- Release candidate verification.

Typical characteristics:

```text
Environment: qa
Git branch: qa
Data: controlled test data
Logs: production-like with sufficient diagnostics
AI Provider: real provider when integration validation requires it
Database: isolated QA PostgreSQL
Frontend origin: QA URL
```

QA should behave as similarly as possible to production.

The main difference should be configuration and infrastructure resources, not application code.

---

## 4.3 Production Environment

The `prod` environment represents the stable system exposed to real users.

Its objectives are:

- Stable system execution.
- Controlled releases.
- Secure configuration.
- Production database persistence.
- Production authentication.
- Production AI integration.
- Monitoring and operational control.

Typical characteristics:

```text
Environment: prod
Git branch: main
Data: production data
Logs: controlled structured logs
AI Provider: approved production provider
Database: production PostgreSQL
Frontend origin: production domain
```

Production secrets must never exist in source control.

---

# 5. Environment Comparison

| Concern | Develop | QA | Production |
|---|---|---|---|
| Git branch | `dev` | `qa` | `main` |
| Purpose | Development and integration | Validation | Stable release |
| Application artifact | Same | Same artifact promoted from develop | Same artifact promoted from QA |
| Database | Development | Isolated QA | Production |
| Data | Synthetic / disposable | Controlled test data | Real production data |
| AI Provider | Fake or Groq | Groq for integration validation | Approved production provider |
| Secrets | Local environment injection | QA secret injection | Production secret management |
| Logging | Development-friendly | Production-like | Controlled structured logging |
| CORS | Development frontend | QA frontend | Production frontend |
| Debug behavior | Allowed when required | Restricted | Disabled |
| Destructive test data | Allowed | Controlled | Prohibited |
| Deployment target | Local / development environment | QA infrastructure | Production infrastructure |

---

# 6. Same Artifact, Different Configuration

TeleMed IA follows the principle:

```text
One commit
     ↓
One application artifact
     ↓
Develop
     ↓
QA
     ↓
Production
```

The application must not be rebuilt with different application logic for each environment.

Incorrect:

```text
Code
 ↓
Build develop image

Code changed
 ↓
Build QA image

Code changed again
 ↓
Build production image
```

Correct:

```text
Source commit
      ↓
Build image
      ↓
Image: telemed-backend:<commit-sha>
      │
      ├── develop configuration
      │
      ├── qa configuration
      │
      └── production configuration
```

The container image remains immutable.

Only runtime configuration changes.

---

# 7. Artifact Version Strategy

A future CI/CD pipeline should identify Docker images using an immutable version.

Example:

```text
telemed-backend:a84c519
telemed-frontend:a84c519
```

where:

```text
a84c519
```

represents the Git commit used to generate the artifact.

The same image digest should move through:

```text
develop
   ↓
qa
   ↓
prod
```

Production must not rebuild a different image from the same source version.

This prevents environment-specific build drift.

---

# 8. 12-Factor Configuration Strategy

TeleMed IA follows the 12-factor configuration principle:

> Configuration belongs to the runtime environment, not to application source code.

The application must not contain environment-specific credentials or infrastructure addresses directly in the code.

Instead of:

```java
String databaseUrl = "jdbc:postgresql://localhost:5432/telemed";
```

the backend reads:

```text
DB_URL
```

from its environment.

The application code therefore remains environment-independent.

---

# 9. Current Backend Configuration Pattern

The Spring Boot configuration follows the environment-variable pattern.

Example:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/telemed}
    username: ${DB_USERNAME:telemed}
    password: ${DB_PASSWORD:telemed}
```

JWT configuration uses the same approach:

```yaml
telemed:
  jwt:
    secret: ${JWT_SECRET}
```

CORS configuration is also externalized:

```yaml
telemed:
  cors:
    origins: ${CORS_ORIGINS:http://localhost:4200}
```

The Intelligent Agent follows the same configuration strategy.

Example:

```yaml
telemed:
  ai:
    provider: ${AI_PROVIDER:fake}
```

External AI credentials must be injected through the environment.

---

# 10. Standard Configuration Variables

TeleMed IA defines a consistent variable vocabulary.

A variable must keep the same name across environments.

The environment changes its value, not its identifier.

---

## 10.1 Backend Configuration Matrix

| Variable | Purpose | Develop | QA | Production | Secret |
|---|---|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Spring runtime profile | `dev` | `qa` or approved QA profile | `prod` | No |
| `DB_URL` | PostgreSQL JDBC URL | Development DB | QA DB | Production DB | No |
| `DB_USERNAME` | Database user | Development user | QA user | Production user | Sensitive |
| `DB_PASSWORD` | Database password | Injected | Injected | Secret store | Yes |
| `JWT_SECRET` | JWT signing secret | Development secret | QA secret | Production secret | Yes |
| `CORS_ORIGINS` | Allowed frontend origin | Local/dev frontend | QA URL | Production URL | No |
| `AI_PROVIDER` | Intelligent Agent provider | `fake` or `groq` | `groq` | `groq` or approved provider | No |
| `GROQ_API_KEY` | External AI credential | Optional when using Groq | Injected | Secret store | Yes |
| `GROQ_MODEL` | AI model configuration | Approved model | Same validated model | Same validated model | No |
| `PORT` | Backend HTTP port | `8080` | Environment-defined | Environment-defined | No |

---

## 10.2 Docker PostgreSQL Variables

For local Docker Compose execution:

| Variable | Purpose | Secret |
|---|---|---|
| `POSTGRES_DB` | Database name | No |
| `POSTGRES_USER` | PostgreSQL user | Sensitive |
| `POSTGRES_PASSWORD` | PostgreSQL password | Yes |

Docker Compose currently maps these values to the backend configuration:

```text
POSTGRES_DB
      ↓
DB_URL

POSTGRES_USER
      ↓
DB_USERNAME

POSTGRES_PASSWORD
      ↓
DB_PASSWORD
```

---

# 11. Configuration Naming Rule

The following must never occur:

```text
develop:
DB_URL

qa:
DATABASE_URL

prod:
POSTGRES_CONNECTION
```

This produces configuration drift.

TeleMed IA defines one canonical name:

```text
DB_URL
```

and only the value changes.

Correct:

```text
develop
DB_URL=jdbc:postgresql://postgres:5432/telemed

qa
DB_URL=<qa-database-url>

prod
DB_URL=<production-database-url>
```

The same applies to all environment variables.

---

# 12. `.env.example`

The repository should include a non-sensitive configuration reference:

```text
.env.example
```

Example:

```env
# Database
POSTGRES_DB=telemed
POSTGRES_USER=telemed
POSTGRES_PASSWORD=change-me

DB_URL=jdbc:postgresql://postgres:5432/telemed
DB_USERNAME=telemed
DB_PASSWORD=change-me

# Spring
SPRING_PROFILES_ACTIVE=dev
PORT=8080

# Security
JWT_SECRET=replace-with-a-secure-local-secret

# Frontend
CORS_ORIGINS=http://localhost:4200

# Intelligent Agent
AI_PROVIDER=fake
GROQ_API_KEY=
GROQ_MODEL=openai/gpt-oss-20b
```

This file documents required configuration names without exposing actual credentials.

---

# 13. `.env` Security Rule

The real local file:

```text
.env
```

must not be committed.

The repository `.gitignore` must contain:

```gitignore
.env
.env.*
!.env.example
```

The expected relationship is:

```text
.env.example
      │
      │ copy locally
      ▼
.env
      │
      │ real local values
      ▼
Docker Compose / Application
```

Only `.env.example` belongs in Git.

---

# 14. Secrets Strategy

Secrets are configuration values that would create a security risk if exposed.

Examples include:

```text
JWT_SECRET
DB_PASSWORD
GROQ_API_KEY
future email provider API key
future cloud credentials
```

Secrets must never appear in:

```text
Java source code
Angular source code
Dockerfiles
Committed Compose files
Git history
README examples with real values
Logs
Domain Events
JWT payloads
```

---

# 15. Secret Flow

## Develop

Local development uses environment injection.

```text
Developer machine
       │
       ▼
.env / OS environment
       │
       ▼
Docker Compose
       │
       ▼
Backend container
```

---

## QA

QA secrets must be injected by the deployment environment.

```text
QA secret configuration
        │
        ▼
QA runtime
        │
        ▼
Application container
```

They must not be copied from the developer's local `.env`.

---

## Production

The target production flow is:

```text
Secure secret manager
        │
        ▼
Deployment platform
        │
        ▼
Container environment
        │
        ▼
TeleMed IA service
```

For the planned AWS deployment, production secrets should eventually be managed using an approved AWS secret-management mechanism instead of being stored in Git.

---

# 16. Intelligent Agent Secret Strategy

The Groq API key belongs only to the backend environment.

Incorrect:

```text
Angular
   ↓
GROQ_API_KEY
   ↓
Groq
```

Correct:

```text
Angular
   ↓
TeleMed Backend
   ↓
AgentModelPort
   ↓
GroqAgentAdapter
   ↓
GROQ_API_KEY
   ↓
Groq
```

The frontend must never contain or receive the private AI provider API key.

---

# 17. Configuration Validation

Required configuration should be validated as early as possible.

The objective is to fail with a clear configuration error rather than failing later with an ambiguous infrastructure exception.

Example:

```text
Application startup
       │
       ▼
Read configuration
       │
       ▼
Required variable missing?
       │
     YES
       │
       ▼
Fail fast with clear error
```

instead of:

```text
Application starts
       ↓
Business request arrives
       ↓
Dependency fails
       ↓
Unknown configuration error
```

---

# 18. Required Configuration Rules

The following values are required for a normal backend deployment:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
CORS_ORIGINS
```

When:

```text
AI_PROVIDER=groq
```

the following must also exist:

```text
GROQ_API_KEY
GROQ_MODEL
```

When:

```text
AI_PROVIDER=fake
```

a Groq credential is not required.

---

# 19. Spring Profile Strategy

TeleMed IA currently uses Spring profiles to activate environment-specific behavior.

The intended configuration structure is:

```text
application.yml
application-dev.yml
application-qa.yml
application-prod.yml
```

However, profiles must not become a place for storing environment credentials.

Profiles may define safe behavior such as:

```text
logging levels
environment-specific feature behavior
non-secret defaults
```

Secrets and infrastructure-specific values remain environment variables.

---

# 20. Git Branch to Environment Mapping

The course activity describes environment-specific promotion.

TeleMed IA adapts that concept to the Git strategy already defined by the project.

The repository uses:

```text
dev
qa
main
```

Therefore the environment mapping is:

| Git Branch | Environment | Purpose |
|---|---|---|
| `dev` | Develop | Feature integration |
| `qa` | QA | Integrated validation |
| `main` | Production | Stable release |

---

# 21. Development Branch Flow

New application work starts in a task branch created according to the project Git conventions.

Examples:

```text
feat/hu-06-ai-preconsultation
fix/appointment-availability
refactor/auth-token-validation
```

The development integration flow is:

```text
Task branch
     │
     │ Pull Request
     ▼
    dev
     │
     ▼
Develop environment
```

A change integrated into `dev` becomes eligible for development-environment integration testing.

---

# 22. QA Promotion

After development integration is stable:

```text
dev
 │
 │ Pull Request
 ▼
qa
 │
 ▼
QA environment
```

QA validates:

- Functional behavior.
- Integration.
- Regression risk.
- Database migrations.
- Environment configuration.
- Security behavior.
- Intelligent Agent behavior when applicable.
- API compatibility.
- Documentation consistency.

A change that fails QA must not reach `main`.

---

# 23. Production Promotion

After successful QA validation:

```text
qa
 │
 │ Pull Request
 ▼
main
 │
 ▼
Production
```

The expected complete flow is:

```text
Task branch
      ↓
     PR
      ↓
     dev
      ↓
Develop environment
      ↓
     PR
      ↓
      qa
      ↓
QA environment
      ↓
Validation
      ↓
     PR
      ↓
     main
      ↓
Production
```

A normal product change must never bypass QA.

---

# 24. Branch and Environment Discipline

Incorrect:

```text
Feature branch
      ↓
main
      ↓
production
```

Correct:

```text
Feature branch
      ↓
dev
      ↓
qa
      ↓
main
```

This provides traceability between:

```text
Requirement
    ↓
User Story
    ↓
Task branch
    ↓
Commit
    ↓
Pull Request
    ↓
Develop
    ↓
QA
    ↓
Production
```

---

# 25. Artifact Promotion Strategy

Branch promotion and artifact promotion represent two related processes.

The target process is:

```text
Commit
  │
  ▼
CI Build
  │
  ▼
Immutable Docker Image
  │
  ├── deploy develop
  │
  ▼
QA approval
  │
  ├── deploy SAME image to QA
  │
  ▼
Production approval
  │
  └── deploy SAME image to Production
```

The image must not be rebuilt between QA and production.

Only environment configuration changes.

---

# 26. Example Artifact Promotion

Suppose the CI pipeline creates:

```text
telemed-backend:5334e63
```

Develop:

```text
image:
telemed-backend:5334e63

config:
develop values
```

QA:

```text
image:
telemed-backend:5334e63

config:
qa values
```

Production:

```text
image:
telemed-backend:5334e63

config:
production values
```

The image remains identical.

---

# 27. Preventing Configuration Drift

Configuration drift occurs when environments accidentally use different configuration rules.

Example:

```text
Develop:
DB_URL

QA:
DATABASE_URL

Production:
POSTGRES_URL
```

Another example:

```text
Develop:
postgres:5432

QA:
localhost:5432
```

These inconsistencies can cause:

```text
Works in develop
      ↓
Fails in QA
```

even though the application code is identical.

---

# 28. Configuration Drift Prevention Strategy

TeleMed IA prevents configuration drift through:

1. A canonical environment variable vocabulary.
2. `.env.example`.
3. Environment variables instead of hard-coded values.
4. Startup validation for required configuration.
5. Same artifact promotion.
6. QA before production.
7. Documented environment matrix.
8. Pull Request review.
9. Docker-based integration validation.
10. Future CI checks.

---

# 29. Docker Compose Role

Docker Compose remains the current reproducible local integration environment.

Current topology:

```text
Docker Compose
│
├── postgres
├── backend
└── frontend
```

The objective is:

```bash
docker compose up --build
```

and obtain a complete local TeleMed IA environment.

Docker Compose currently provides:

- Container builds.
- Shared networking.
- PostgreSQL readiness checking.
- Backend dependency gating.
- Environment injection.
- Persistent volume management.
- Restart policies.
- Reproducible local execution.

---

# 30. Orchestration Evolution

Docker Compose is appropriate for local development and single-host integration.

The future target requires orchestration capabilities such as:

```text
Multiple services
Multiple execution nodes
Self-healing
Rolling deployments
Replica management
Autoscaling
Health-based task replacement
Centralized deployment
```

TeleMed IA plans to evolve its deployment architecture toward AWS.

The target direction is:

```text
Local
Docker Compose
      │
      ▼
Container images
      │
      ▼
Container registry
      │
      ▼
AWS orchestration
      │
      ▼
Amazon ECS / Fargate
```

This allows the containerization work already completed to remain useful during the future deployment stage.

---

# 31. Relationship with Microservices

The current backend is still a modular monolith.

The environment strategy must work both now and after service extraction.

Current:

```text
frontend
    ↓
backend
    ↓
postgres
```

Intermediate migration:

```text
frontend
    ↓
API Gateway
    │
    ├── modular monolith
    └── agent-service
```

Target:

```text
frontend
    ↓
API Gateway
    │
    ├── auth-service
    ├── patient-service
    ├── professional-service
    ├── agent-service
    ├── appointment-service
    ├── consultation-service
    ├── notification-service
    └── document-service
```

Every extracted service must follow the same configuration principles.

---

# 32. Per-Service Configuration Rule

Each future service may have service-specific configuration, but project-wide naming must remain consistent.

Example:

```text
agent-service

DB_URL
DB_USERNAME
DB_PASSWORD
AI_PROVIDER
GROQ_API_KEY
GROQ_MODEL
```

Example:

```text
appointment-service

DB_URL
DB_USERNAME
DB_PASSWORD
```

The exact values may differ because each service owns its environment and database.

The variable names should remain predictable and documented.

---

# 33. Database per Service and Environment Configuration

The target microservice architecture follows Database per Service.

Therefore:

```text
agent-service
      ↓
agent database

appointment-service
      ↓
appointment database

consultation-service
      ↓
consultation database
```

The environment strategy must not introduce hard-coded cross-service database access.

A service receives only the credentials for the database it owns.

This also supports least privilege.

---

# 34. MVP 2 Orchestration Technical Stories

The following items are **technical orchestration stories** for MVP 2.

They do not replace the product User Stories `HU-01` through `HU-18`.

Their identifiers are used only to organize technical infrastructure work.

---

## MVP2-ORCH-01 — Reproducible Local System Startup

### Story

As a development team,  
we want the complete current TeleMed IA system to start using a single Docker Compose command,  
so that every developer can reproduce the same integrated environment.

### Acceptance Criteria

- `docker compose up --build` starts PostgreSQL, backend, and frontend.
- PostgreSQL uses a persistent named volume.
- Backend and PostgreSQL communicate using Docker service-name DNS.
- No container depends on a hard-coded container IP.
- PostgreSQL exposes a working health check.
- Backend startup waits for PostgreSQL to become healthy.
- `docker compose ps` shows the expected services.
- The frontend is accessible through its configured local port.
- The backend is accessible through its configured local port.

### Status

```text
Implemented / validation in progress
```

---

## MVP2-ORCH-02 — Standardized Runtime Configuration

### Story

As a development team,  
we want all environment-specific configuration to be injected through environment variables,  
so that the same application artifact can run in develop, QA, and production.

### Acceptance Criteria

- Database configuration is externalized.
- JWT configuration is externalized.
- CORS configuration is externalized.
- Intelligent Agent provider configuration is externalized.
- No production infrastructure URL is hard-coded in application logic.
- Canonical variable names are documented.
- The same variable names are used in all environments.

### Status

```text
Partially implemented
```

---

## MVP2-ORCH-03 — Secure Secret Management Baseline

### Story

As a development team,  
we want secrets to remain outside source control,  
so that credentials cannot be exposed through Git history or container images.

### Acceptance Criteria

- `.env` is excluded from Git.
- `.env.example` contains variable names but no real secrets.
- `JWT_SECRET` is injected at runtime.
- Database passwords are injected at runtime.
- AI provider API keys are injected at runtime.
- No API key is present in Angular source code.
- No production credential is included in a Dockerfile.
- Pull Requests are reviewed for accidental secret exposure.

### Status

```text
Partially implemented / documentation baseline defined
```

---

## MVP2-ORCH-04 — Develop, QA and Production Environment Definition

### Story

As a development team,  
we want explicit develop, QA, and production environment definitions,  
so that deployment behavior is predictable and configuration drift is reduced.

### Acceptance Criteria

- Develop environment responsibilities are documented.
- QA environment responsibilities are documented.
- Production environment responsibilities are documented.
- Configuration differences are documented in a matrix.
- Secrets are different between environments.
- Databases are isolated by environment.
- Production credentials are not reused for development.
- QA remains production-like where practical.

### Status

```text
Planned
```

---

## MVP2-ORCH-05 — Branch-to-Environment Promotion

### Story

As a development team,  
we want Git branch promotion to correspond to runtime environment promotion,  
so that unvalidated changes cannot reach production.

### Acceptance Criteria

- `dev` maps to the develop environment.
- `qa` maps to the QA environment.
- `main` maps to the production environment.
- Product changes enter `dev` through Pull Requests.
- Promotion from `dev` to `qa` occurs through a Pull Request.
- Promotion from `qa` to `main` occurs through a Pull Request.
- A normal product change cannot bypass QA.
- Failed QA validation blocks production promotion.

### Status

```text
Git workflow defined; automated deployment mapping planned
```

---

## MVP2-ORCH-06 — Immutable Artifact Promotion

### Story

As a development team,  
we want the same Docker image to be promoted across environments,  
so that production runs the exact artifact validated in QA.

### Acceptance Criteria

- CI creates one immutable image per source version.
- The image is tagged with an immutable identifier such as a commit SHA.
- Develop deploys that image with develop configuration.
- QA deploys the same image with QA configuration.
- Production deploys the same image with production configuration.
- Production deployment does not rebuild application code.
- Artifact digest can be traced to a Git commit.

### Status

```text
Planned for CI/CD implementation
```

---

## MVP2-ORCH-07 — Required Configuration Validation

### Story

As a development team,  
we want required configuration to be validated during application startup,  
so that missing variables fail immediately with clear diagnostic information.

### Acceptance Criteria

- Missing required database configuration produces a clear startup error.
- Missing `JWT_SECRET` produces a clear startup error.
- When `AI_PROVIDER=groq`, missing `GROQ_API_KEY` fails configuration validation.
- Validation errors do not expose secret values.
- QA deployment fails before accepting traffic when required configuration is incomplete.

### Status

```text
Planned
```

---

## MVP2-ORCH-08 — QA Production-Parity Validation

### Story

As a development team,  
we want QA to execute the production candidate using production-like configuration,  
so that environment-specific failures are detected before release.

### Acceptance Criteria

- QA uses the same application image intended for production.
- QA uses an isolated database.
- QA uses environment injection for configuration.
- QA does not use development credentials.
- Database migrations execute successfully.
- Core APIs pass smoke validation.
- Authentication is validated.
- Intelligent Agent integration is validated when applicable.
- Deployment failure blocks promotion to `main`.

### Status

```text
Planned
```

---

## MVP2-ORCH-09 — Cloud Orchestration Preparation

### Story

As a development team,  
we want current containers to be compatible with the planned AWS orchestration environment,  
so that the local Docker implementation can evolve without rewriting the application.

### Acceptance Criteria

- Services run from container images.
- Runtime configuration is externalized.
- Services do not depend on fixed container IP addresses.
- Health endpoints are available where required.
- Persistent state is separated from application containers.
- Secrets can be injected by the deployment platform.
- The future AWS architecture can deploy the same service images.
- Local Docker Compose remains available for development.

### Status

```text
Planned for the final deployment stage
```

---

# 35. MVP 2 Orchestration Backlog Summary

| ID | Technical Story | Priority | Current State |
|---|---|---|---|
| `MVP2-ORCH-01` | Reproducible Local System Startup | P1 | Implemented / validation |
| `MVP2-ORCH-02` | Standardized Runtime Configuration | P1 | Partial |
| `MVP2-ORCH-03` | Secure Secret Management Baseline | P1 | Partial |
| `MVP2-ORCH-04` | Develop, QA and Production Environment Definition | P1 | Planned |
| `MVP2-ORCH-05` | Branch-to-Environment Promotion | P1 | Workflow defined |
| `MVP2-ORCH-06` | Immutable Artifact Promotion | P1 | Planned |
| `MVP2-ORCH-07` | Required Configuration Validation | P2 | Planned |
| `MVP2-ORCH-08` | QA Production-Parity Validation | P1 | Planned |
| `MVP2-ORCH-09` | Cloud Orchestration Preparation | P2 | Planned |

---

# 36. Suggested Sprint Order

The orchestration work should be implemented incrementally.

```text
MVP2-ORCH-01
Reproducible Compose startup
        ↓
MVP2-ORCH-02
Standard configuration
        ↓
MVP2-ORCH-03
Secret handling
        ↓
MVP2-ORCH-04
Environment definitions
        ↓
MVP2-ORCH-05
Branch/environment mapping
        ↓
MVP2-ORCH-07
Fail-fast configuration
        ↓
MVP2-ORCH-08
QA parity
        ↓
MVP2-ORCH-06
Immutable image promotion
        ↓
MVP2-ORCH-09
AWS orchestration preparation
```

---

# 37. Configuration Drift Scenario

A configuration drift failure may look like:

```text
Develop
DB_URL=jdbc:postgresql://postgres:5432/telemed
        │
        ▼
Works
```

while QA accidentally contains:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/telemed
```

The application expects:

```text
DB_URL
```

Therefore:

```text
QA deployment
      ↓
DB_URL missing
      ↓
Database connection failure
```

The source code did not fail.

The environment configuration failed.

---

# 38. TeleMed IA Prevention Strategy

The project prevents this scenario through:

```text
One documented variable name
        ↓
.env.example
        ↓
Startup validation
        ↓
Environment injection
        ↓
QA verification
        ↓
Production promotion
```

The correct variable remains:

```text
DB_URL
```

in every environment.

Only the value changes.

---

# 39. Environment Promotion Example

Example release:

```text
Commit:
5334e63
```

CI produces:

```text
telemed-backend:5334e63
```

Develop:

```text
telemed-backend:5334e63
+
develop config
```

After successful integration:

```text
telemed-backend:5334e63
+
QA config
```

After QA approval:

```text
telemed-backend:5334e63
+
production config
```

No application rebuild occurs between these promotions.

---

# 40. Environment and Branch Overview

```text
Developer
    │
    ▼
Task branch
    │
    │ Pull Request
    ▼
   dev
    │
    ▼
DEVELOP ENVIRONMENT
    │
    │ validation
    ▼
   dev
    │
    │ Pull Request
    ▼
    qa
    │
    ▼
QA ENVIRONMENT
    │
    │ tests + approval
    ▼
    qa
    │
    │ Pull Request
    ▼
   main
    │
    ▼
PRODUCTION ENVIRONMENT
```

---

# 41. Final Environment Architecture

The environment model can be summarized as:

```text
                       SOURCE CODE
                           │
                           ▼
                         BUILD
                           │
                           ▼
                   IMMUTABLE IMAGE
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
          DEVELOP          QA          PROD
              │            │            │
       develop config   qa config   prod config
              │            │            │
              └────────────┴────────────┘
                           │
                           ▼
                   SAME APPLICATION
```

The environment is therefore determined by configuration, not by modifying the application.

---

# 42. Main Decisions

TeleMed IA adopts the following environment and orchestration principles:

1. Three primary environments are defined: develop, QA, and production.
2. The same application artifact must be promoted between environments.
3. Runtime configuration is externalized.
4. Secrets never belong in Git.
5. Environment variable names must remain consistent.
6. `.env.example` documents the configuration contract.
7. QA must validate changes before production.
8. `dev`, `qa`, and `main` map to develop, QA, and production respectively.
9. Docker Compose remains the local integration mechanism.
10. Container images are the deployment artifact.
11. Microservices will inherit the same configuration discipline.
12. AWS orchestration is the planned production evolution.
13. Production promotion must be traceable to the tested Git commit and container image.

---

# 43. Expected Result

At the end of MVP 2 orchestration work, the intended flow is:

```text
Developer pushes change
        │
        ▼
Pull Request
        │
        ▼
dev
        │
        ▼
Build immutable image
        │
        ▼
Develop environment
        │
        ▼
Integration validation
        │
        ▼
qa
        │
        ▼
Same image + QA config
        │
        ▼
QA validation
        │
        ▼
main
        │
        ▼
Same image + production config
        │
        ▼
Production
```

This strategy reduces configuration drift, improves deployment traceability, prevents unvalidated code from reaching production, and prepares TeleMed IA for its progressive migration toward independent microservices and AWS orchestration.

---

## Related Documentation

- Git conventions → `00-governance/git-conventions.md`
- Security rules → `00-governance/security-rules.md`
- Documentation rules → `00-governance/documentation-rules.md`
- Domain Map → `02-domain/domain-map.md`
- Product Roadmap → `03-product/roadmap.md`
- User Stories → `04-requirements/user-stories.md`
- Architecture Overview → `05-architecture/overview.md`
- Architecture Decisions → `05-architecture/decisions/records/`
- Data Architecture → `06-data/`
- API Contracts → `07-api/contracts/openapi/`
- Microservices Catalog → `09-microservices/service-catalog.md`
- DevOps Documentation → `10-devops/`
- Operations → `13-operations/`
- Technical Backlog → `15-project-control/technical-backlog.md`