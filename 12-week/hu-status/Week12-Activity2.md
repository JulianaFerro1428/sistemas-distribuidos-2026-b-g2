
# Week 12 — Activity 2: Deployment Planning for MVP 3

## TeleMed IA — Deployment Plan, Infrastructure as Code and Rollback

### 1. Introduction

TeleMed IA is a healthcare platform developed using a microservices architecture. One of its main components is the Intelligent Agent API, which supports AI-powered medical pre-consultations.

For MVP 3, we need a deployment process that is repeatable, safe and easy to recover if something goes wrong.

In this activity, we define how Docker images will move between environments, how infrastructure will be managed using code, which zero-downtime strategy we will use, and how rollback will be tested.

The objective is to avoid manual deployments and reduce the risk of service interruptions.

---

## 2. Current Project Context

The Intelligent Agent API already includes several features that support deployment and monitoring.

| Component | Current Implementation |
|---|---|
| Backend | Java and Spring Boot |
| Architecture | Modular microservice |
| Database | MongoDB |
| Application port | 8086 by default |
| Management port | 9086 by default |
| Health checks | Spring Boot Actuator |
| Metrics | Micrometer and Prometheus |
| Structured logging | JSON log formatter |
| Request tracing | Trace ID, Span ID and Correlation ID |
| Shutdown | Graceful shutdown enabled |
| Configuration | Environment variables |

These components are useful for MVP 3 because they allow the application to be monitored during deployment.

However, the complete deployment pipeline, infrastructure reproducibility, zero-downtime execution and rollback rehearsal must be verified separately.

---

## 3. Deployment Objectives

For MVP 3, we want to achieve the following objectives:

1. Build and tag a Docker image only once.
2. Push the tested image to a container registry.
3. Promote the same image through development, QA and production.
4. Define infrastructure using version-controlled configuration files.
5. Deploy without interrupting active requests.
6. Monitor the application during each release.
7. Restore the previous version if a deployment fails.
8. Test and document the rollback process.

These objectives will guide the deployment implementation.

---

## 4. Artifact Promotion Plan

### 4.1 Build Once, Deploy Many Times

We will follow the build, release and run approach.

The CI pipeline will build the application, execute automated tests, create a versioned Docker image and push it to a container registry.

After the image is created, the same artifact will be used in every environment.

We will not rebuild the application separately for QA or production.

### Proposed Pipeline

```text
Developer Pushes Code
          |
          v
     Git Repository
          |
          v
     CI Pipeline
          |
          v
   Automated Tests
          |
          v
   Build Docker Image
          |
          v
   Security Scanning
          |
          v
   Tag and Push Image
          |
          v
   Container Registry
          |
          v
   Development
          |
          v
   QA Environment
          |
          v
   Production
```

### 4.2 Image Versioning

Each release will use a specific image tag.

Example:

```text
telemed-ia-intelligent-agent-api:v3.0.0
```

The version is illustrative and must be replaced with the actual release version.

For stronger artifact identity, deployments should also record the immutable image digest.

Example:

```text
image@sha256:<actual-image-digest>
```

### 4.3 Environment Promotion

| Environment | Purpose | Promotion Requirement |
|---|---|---|
| Development | Integration testing | Successful build and automated tests |
| QA | Functional and deployment validation | Development verification completed |
| Production | User-facing environment | QA approval and deployment checks passed |

Each environment will use the same container image but different configuration.

For example:

```text
Development:
  Image: v3.0.0
  Config: development

QA:
  Image: v3.0.0
  Config: qa

Production:
  Image: v3.0.0
  Config: production
```

Secrets, database connections and external service settings must be managed separately for each environment.

---

## 5. Infrastructure as Code (IaC)

Infrastructure as Code means defining infrastructure using configuration files instead of creating and configuring servers manually.

For TeleMed IA, we propose using Docker Compose for a production-like environment and version-controlled deployment configuration.

Terraform or Kubernetes manifests can be introduced if the selected cloud infrastructure requires them.

### 5.1 Proposed Infrastructure

The deployment environment will include:

- Intelligent Agent API containers.
- A reverse proxy or load balancer.
- Network configuration.
- Environment variables and secrets.
- MongoDB connectivity.
- Health checks.
- Metrics collection.

### Architecture

```text
                 Users
                   |
                   v
              API Gateway
                   |
                   v
             Load Balancer
                /      \
               v        v
          Agent API   Agent API
          Instance A  Instance B
               \        /
                v      v
                 MongoDB
```

### 5.2 Reproducible Environments

We want development, QA and production to follow the same infrastructure structure.

The environments may use different resource sizes and configuration values, but their deployment definitions should remain consistent.

Example repository structure:

```text
deploy/
  compose/
    compose.yml
  environments/
    dev.env.example
    qa.env.example
    prod.env.example
  scripts/
    deploy.sh
    rollback.sh
  runbooks/
    deployment.md
    rollback.md
```

This is a proposed structure for MVP 3, not a claim that these files already exist.

### 5.3 Infrastructure Rules

- Infrastructure definitions must be stored in Git.
- Changes must be reviewed before deployment.
- Secrets must not be committed.
- The same infrastructure definitions should be reusable.
- Manual configuration changes should be avoided.
- Differences between QA and production must be documented.

This reduces configuration drift and makes environments easier to recreate.

---

## 6. Zero-Downtime Deployment Strategy

For the Intelligent Agent API, we choose a **rolling deployment strategy**.

Rolling deployment replaces application instances gradually instead of stopping all instances at the same time.

We selected this strategy because the Intelligent Agent API is a Spring Boot service that can be deployed using multiple container instances.

The strategy requires the application instances to be safe to run concurrently and to avoid relying on instance-local state.

### 6.1 Deployment Process

```text
Initial State

Instance A: Version 1
Instance B: Version 1

       |
       v

Start New Instance

Instance A: Version 1
Instance B: Version 1
Instance C: Version 2

       |
       v

Check Version 2 Health

       |
       v

Route Traffic to Version 2

       |
       v

Remove One Old Instance

       |
       v

Repeat Until All Instances
Use Version 2
```

### 6.2 Deployment Requirements

The rolling deployment must:

1. Keep healthy instances available.
2. Start the new version before removing all old instances.
3. Verify application readiness.
4. Route traffic only to ready instances.
5. Allow active requests to finish during shutdown.
6. Monitor errors and latency.
7. Stop the rollout if the new version becomes unhealthy.

### 6.3 Existing Support

The Intelligent Agent API already uses graceful shutdown.

Its Spring Boot configuration includes:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 20s
```

The application also exposes Actuator health endpoints through its management port.

These features support a safer deployment, but they do not replace load-balancer readiness checks or an actual rolling deployment controller.

---

## 7. Health Checks and Release Verification

Health checks are necessary before sending traffic to new instances.

The Intelligent Agent API exposes:

```text
/actuator/health
```

The default management port is:

```text
9086
```

The management endpoint must remain restricted to authorized infrastructure.

### Release Verification Checklist

- [ ] The new container starts successfully.
- [ ] Required configuration is available.
- [ ] MongoDB connectivity is verified.
- [ ] The health endpoint responds correctly.
- [ ] An authorized API request succeeds.
- [ ] New instances receive traffic only after readiness verification.
- [ ] Old instances remain available during replacement.
- [ ] Error rates do not increase unexpectedly.
- [ ] Request latency remains within acceptable limits.
- [ ] The expected image version is running.

The deployment should be considered successful only after these checks pass.

---

## 8. Rollback Plan

A rollback allows the team to restore a previous stable version when a deployment causes problems.

For MVP 3, we propose keeping the previously verified Docker image available in the registry.

### 8.1 Rollback Triggers

A rollback may be required when:

- The new application version fails health checks.
- Patients cannot complete pre-consultations.
- HTTP 5xx errors increase significantly.
- Request latency becomes unacceptable.
- An important dependency stops working.
- The deployment causes unexpected behavior.

### 8.2 Rollback Procedure

1. Identify the failing release.
2. Stop the deployment or promotion process.
3. Identify the last known healthy image digest.
4. Update the deployment to use the previous image.
5. Wait for the previous instances to become ready.
6. Route traffic to healthy instances.
7. Verify API requests and health checks.
8. Confirm that errors and latency return to normal.
9. Record the incident and recovery time.

### Rollback Flow

```text
Deploy New Version
        |
        v
Run Health Checks
        |
        v
Is the Version Healthy?
       / \
     Yes  No
      |    |
      v    v
 Continue Stop Rollout
           |
           v
   Restore Previous Image
           |
           v
     Verify Recovery
```

### 8.3 Rollback Time Objective

For MVP 3, we propose the following target:

**Restore the previous stable application version within 5 minutes.**

This is a target, not a measured result.

The time must be measured during an actual rollback rehearsal.

The target covers the application rollback and does not guarantee recovery from incompatible database changes or external service failures.

---

## 9. Rollback Rehearsal

A rollback must be tested before it is needed in a real incident.

### Proposed Test Scenario

We will simulate a failed deployment in the QA or production-like environment.

The test will follow these steps:

1. Deploy a stable version of the Intelligent Agent API.
2. Confirm that health checks pass.
3. Deploy a new test version.
4. Introduce a controlled failure in the test environment.
5. Detect the failure through health checks or monitoring.
6. Start the rollback procedure.
7. Restore the previous stable image.
8. Verify that the application works correctly.
9. Measure the recovery time.

### Rehearsal Record

| Field | Value |
|---|---|
| Environment | QA / Production-like |
| Stable version | To be recorded |
| Failed version | To be recorded |
| Failure introduced | To be recorded |
| Rollback start time | To be recorded |
| Recovery confirmation time | To be recorded |
| Total recovery time | To be calculated |
| Target | Less than 5 minutes |
| Result | Pending execution |

The rehearsal will be considered successful when the previous version is restored, health checks pass, and authorized API requests work correctly.

A documented procedure without an executed test is not sufficient evidence of a rehearsed rollback.

---

# 10. MVP 3 Deployment User Stories

## DEP-01 — Build and Publish a Versioned Image

**As a** TeleMed IA developer,

**I want** the CI pipeline to build and publish a versioned Docker image,

**So that** the same tested artifact can be deployed to different environments.

### Acceptance Criteria

- [ ] The pipeline builds the application.
- [ ] Automated tests run before publishing.
- [ ] The Docker image receives a version tag.
- [ ] The image is pushed to a container registry.
- [ ] The image digest is recorded.
- [ ] A failed test prevents publication.

**Expected Evidence:** CI execution, image tag and registry record.

---

## DEP-02 — Promote the Same Artifact

**As a** TeleMed IA developer,

**I want** to promote the same Docker image through development, QA and production,

**So that** the deployed artifact is identical across environments.

### Acceptance Criteria

- [ ] The same image digest is used in each environment.
- [ ] Environment configuration is managed separately.
- [ ] The application is not rebuilt during promotion.
- [ ] QA verification is required before production promotion.
- [ ] The deployed version can be identified.

**Expected Evidence:** Deployment records showing the same image digest.

---

## DEP-03 — Create Reproducible Infrastructure

**As a** TeleMed IA developer,

**I want** infrastructure to be defined using code,

**So that** environments can be recreated without manual configuration.

### Acceptance Criteria

- [ ] Infrastructure configuration is stored in Git.
- [ ] A documented command creates or updates the environment.
- [ ] Required services and network configuration are defined.
- [ ] Secrets are excluded from version control.
- [ ] A clean QA environment can be recreated from the definitions.
- [ ] Infrastructure changes can be reviewed.

**Expected Evidence:** IaC files and successful environment recreation.

---

## DEP-04 — Deploy Without Service Interruption

**As a** TeleMed IA developer,

**I want** the Intelligent Agent API to use rolling deployment,

**So that** users can continue accessing the service during releases.

### Acceptance Criteria

- [ ] At least one healthy instance remains available during replacement.
- [ ] New instances pass readiness checks before receiving traffic.
- [ ] Old instances finish active requests before shutdown.
- [ ] The deployment stops if the new version becomes unhealthy.
- [ ] Requests are tested continuously during deployment.
- [ ] The test records whether any service interruption occurred.

**Expected Evidence:** Deployment logs, health checks and request test results.

---

## DEP-05 — Monitor Deployment Health

**As a** TeleMed IA developer,

**I want** to monitor application behavior during deployment,

**So that** I can detect failures before considering a release successful.

### Acceptance Criteria

- [x] The Intelligent Agent API has a structured JSON log formatter.
- [x] HTTP requests include trace and correlation information.
- [x] The application exposes a Prometheus metrics endpoint.
- [x] The application includes automated tests for metrics exposure.
- [ ] Deployment monitoring is connected to the selected environment.
- [ ] Error rate and latency are checked during rollout.
- [ ] A failed health check prevents traffic from reaching an unready instance.

**Expected Evidence:** Existing source code and tests, plus deployment monitoring results.

---

## DEP-06 — Rehearse and Document Rollback

**As a** TeleMed IA developer,

**I want** to test the rollback procedure,

**So that** the team can recover quickly when a deployment fails.

### Acceptance Criteria

- [ ] The previous stable image is available.
- [ ] The rollback command or procedure is documented.
- [ ] A controlled deployment failure is simulated.
- [ ] The previous version is restored successfully.
- [ ] Health checks pass after rollback.
- [ ] An authorized API request succeeds after recovery.
- [ ] The rollback duration is measured.
- [ ] The result is compared with the 5-minute target.
- [ ] Evidence of the rehearsal is documented.

**Expected Evidence:** Rollback logs, timestamps and test results.

---

## 11. MVP 3 Deployment Backlog

| Story | Priority | Deliverable | Status |
|---|---|---|---|
| DEP-01 | High | Versioned image pipeline | To verify |
| DEP-02 | High | Artifact promotion process | Planned |
| DEP-03 | High | Reproducible IaC environments | To verify |
| DEP-04 | High | Rolling deployment | Planned |
| DEP-05 | High | Deployment monitoring | Partially implemented |
| DEP-06 | High | Tested rollback procedure | Pending rehearsal |

The statuses describe what has been verified for this activity.

They should be updated after checking the infrastructure repository, CI/CD configuration and deployment evidence.

---

## 12. Risks and Mitigation

| Risk | Mitigation |
|---|---|
| New version fails during deployment | Health checks and automatic rollout stop |
| Different configuration between environments | Version-controlled IaC |
| Wrong image deployed | Immutable image digest |
| Service interruption | Rolling deployment with healthy instances |
| Database incompatibility | Backward-compatible migrations and separate recovery planning |
| Rollback takes too long | Rehearsed and timed procedure |
| Secrets exposed in repository | Environment-based secret management |
| Monitoring unavailable | Verify metrics and health checks before deployment |

---

## 13. Expected Evidence

To complete this activity, we should provide:

1. Deployment pipeline diagram.
2. Versioned Docker image and registry evidence.
3. Artifact promotion records.
4. Infrastructure as Code configuration.
5. Evidence of a reproducible QA environment.
6. Rolling deployment configuration and test results.
7. Health check and monitoring evidence.
8. Documented rollback procedure.
9. Rollback rehearsal logs.
10. Measured recovery time.
11. MVP 3 user stories with acceptance criteria.

The evidence must reflect actual execution rather than only planned behavior.

---

## 14. Next Steps

After deployment planning, the next activities will focus on:

### Security

- Secure configuration and secret management.
- Access control.
- Container and dependency vulnerability scanning.
- Network security.
- Secure communication between services.

### CI/CD

- Automated builds.
- Automated tests.
- Image publishing.
- Environment promotion.
- Deployment automation.
- Release verification.
- Rollback integration.

These activities will help complete the MVP 3 deployment process.

---

## 15. Conclusion

In this activity, we created a deployment plan for TeleMed IA MVP 3.

We defined how Docker images should be built once and promoted through different environments without rebuilding them.

We also proposed Infrastructure as Code to make environments reproducible and selected rolling deployment as the zero-downtime strategy for the Intelligent Agent API.

Finally, we documented a rollback procedure, defined a five-minute recovery target and created six deployment user stories with testable acceptance criteria.

The Intelligent Agent API already provides useful features such as health checks, Prometheus metrics, structured logging, request tracing and graceful shutdown.

The remaining deployment requirements must be validated through real execution and documented evidence.

The main goal is to make TeleMed IA deployments repeatable, observable and reversible.
