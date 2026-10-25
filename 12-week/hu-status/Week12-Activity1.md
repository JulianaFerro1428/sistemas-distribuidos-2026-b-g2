
# Week 12 — Activity 1: Cloud Deployment

## TeleMed IA — Cloud Deployment, Zero Downtime and Health Checks

### 1. Introduction

TeleMed IA is a healthcare platform developed using microservices. One of its main components is the Intelligent Agent API, which supports AI-powered medical pre-consultations.

During this activity, we focus on deploying the application to a cloud environment or a production-like server using Docker containers.

The objective is to deploy a tested, versioned image, use health checks, and apply a deployment strategy that reduces service interruptions.

This activity builds on the work already completed in the Intelligent Agent API, including structured logging, request tracing, Prometheus metrics, and Spring Boot Actuator.

---

## 2. Activity Objective

Deploy TeleMed IA using a tagged Docker image and a zero-downtime deployment strategy.

The deployment must support:

- A versioned Docker image.
- A container registry.
- Environment-specific configuration.
- Application health checks.
- Rolling or blue-green deployment.
- Service monitoring.
- A documented rollback procedure.

The main goal is to make deployments repeatable, safer, and easier to recover when a problem occurs.

---

## 3. Current Implementation in TeleMed IA

The Intelligent Agent API already includes several features that support cloud deployment.

| Component | Current Implementation |
|---|---|
| Backend | Java and Spring Boot |
| Architecture | Microservices with separated application modules |
| Application port | 8086 by default |
| Management port | 9086 by default |
| Health monitoring | Spring Boot Actuator |
| Metrics | Micrometer and Prometheus |
| Structured logs | Custom JSON log formatter |
| Request tracing | Trace ID, Span ID and Correlation ID |
| Database | MongoDB |
| Application shutdown | Graceful shutdown enabled |

These components provide a foundation for deploying and monitoring the service.

However, a successful zero-downtime deployment must still be demonstrated in the selected environment.

---

## 4. Container Deployment

### 4.1 Build a Versioned Docker Image

For this activity, we use a versioned Docker image instead of relying only on the `latest` tag.

Example image name:

```text
telemed-ia-intelligent-agent-api:v3.0.0
```

The version is an example for this activity and should be replaced with the actual release tag.

A versioned image makes it easier to identify the application version running in each environment.

### 4.2 Build Once, Deploy the Same Image

We follow the build, release, and run approach.

```text
Source Code
    |
    v
CI Pipeline
    |
    v
Build Docker Image
    |
    v
Run Automated Tests
    |
    v
Tag Image
    |
    v
Push to Container Registry
    |
    v
Deploy Tested Image
    |
    v
Verify Application Health
```

The same image should be promoted across environments without rebuilding it.

This helps ensure that the version tested in QA is the same version deployed to production.

---

## 5. Cloud or Production-like Environment

TeleMed IA can be deployed to a cloud platform or a production-like host that supports Docker containers.

For this activity, the deployment environment must provide:

1. A host or container platform.
2. Docker image access.
3. Network configuration.
4. Environment variables.
5. Database connectivity.
6. Health monitoring.
7. A reverse proxy or load balancer for zero-downtime deployment.

A possible deployment architecture is:

```text
                Users
                  |
                  v
           API Gateway
                  |
                  v
          Load Balancer
             /       \
            v         v
       Agent API   Agent API
       Instance 1  Instance 2
            \         /
             v       v
               MongoDB
```

Multiple instances allow traffic to continue while one instance is being replaced.

Each instance must use the same application image version and the appropriate environment configuration.

---

## 6. Application Configuration

The Intelligent Agent API already defines environment-based configuration in its Spring Boot application settings.

Relevant properties include:

```yaml
server:
  port: ${PORT:8086}
  shutdown: graceful

management:
  server:
    port: ${MANAGEMENT_PORT:9086}

  endpoints:
    web:
      exposure:
        include: health,info,prometheus

  endpoint:
    health:
      show-details: never
```

This configuration provides:

- Application traffic on port 8086 by default.
- Management endpoints on port 9086 by default.
- Graceful shutdown support.
- Health and metrics endpoints.

The deployment must also provide required configuration such as MongoDB connectivity and authentication settings.

Secrets must be supplied securely through environment variables or a secret-management system.

They must not be committed to GitHub.

---

## 7. Health Checks

Health checks help determine whether an application instance can continue serving requests.

The Intelligent Agent API exposes Spring Boot Actuator health information through its management port.

Example:

```text
http://localhost:9086/actuator/health
```

This address is intended for access from the host or authorized monitoring environment.

The management endpoint should not be publicly exposed.

### Health Check Process

```text
Container Starts
      |
      v
Application Initializes
      |
      v
Health Check
      |
      +---- Healthy ----> Receive Traffic
      |
      +---- Unhealthy --> Do Not Route Traffic
```

The deployment platform should check application health before sending traffic to a new instance.

A successful HTTP health response is useful, but readiness to serve user traffic must also be validated, especially when the service depends on MongoDB or other APIs.

---

## 8. Zero-Downtime Deployment Strategy

For this activity, we propose using a rolling deployment strategy for the Intelligent Agent API.

Rolling deployment is appropriate for applications that can run multiple independent instances behind a load balancer.

### How Rolling Deployment Works

```text
Initial State:

Instance A -> Version 1
Instance B -> Version 1

Step 1:

Instance A -> Version 2
Instance B -> Version 1

Step 2:

Instance A -> Version 2
Instance B -> Version 2

Final State:

All Instances -> Version 2
```

During the deployment:

1. The old application version continues serving requests.
2. A new instance starts with the tagged image.
3. The deployment platform checks its health.
4. Traffic is routed to the healthy new instance.
5. An old instance is removed from service.
6. The process repeats until the new version is deployed.

The objective is to keep the application available during the release.

### Important Considerations

Rolling deployment requires:

- More than one available instance during replacement.
- Correct load-balancer configuration.
- Health and readiness checks.
- Graceful shutdown.
- Compatible application versions.
- Safe database changes.

Zero downtime must be validated with real requests during deployment.

It cannot be guaranteed only by configuring graceful shutdown.

---

## 9. Scaling and Failure Recovery

Horizontal scaling means running multiple instances of the same service.

For example:

```text
              Load Balancer
               /    |    \
              v     v     v
          Agent 1 Agent 2 Agent 3
```

If one instance fails, the platform should stop routing traffic to it and continue using healthy instances.

For TeleMed IA, scaling can improve availability during periods of high request volume.

The Intelligent Agent API should avoid storing important user session state only in the memory of a single container.

Persistent information should remain in the appropriate database or shared storage system.

---

## 10. Monitoring the Deployment

The Intelligent Agent API already includes observability features that can help monitor deployments.

### Available Monitoring Components

- Structured JSON logs.
- Trace IDs.
- Correlation IDs.
- Request metrics.
- Request duration metrics.
- Prometheus endpoint.
- Application health endpoint.

During deployment, we should monitor:

| Metric | Purpose |
|---|---|
| Request rate | Confirm the service continues receiving traffic |
| Error rate | Detect failures after the release |
| Response latency | Identify performance degradation |
| Health status | Detect unhealthy instances |
| Container availability | Confirm the expected instances are running |

The existing metrics endpoint is:

```text
/actuator/prometheus
```

It is exposed through the management port.

The monitoring system should collect these metrics before, during, and after deployment.

---

## 11. Deployment Verification

After deploying the tagged image, the following checks should be performed.

### Verification Checklist

- [ ] The Docker image has a specific version tag.
- [ ] The image is available in the selected registry.
- [ ] The deployment uses the tested image.
- [ ] The container starts successfully.
- [ ] Required environment variables are configured.
- [ ] The application connects to its dependencies.
- [ ] The health endpoint responds successfully.
- [ ] The metrics endpoint is accessible to authorized monitoring.
- [ ] The API responds to an authorized test request.
- [ ] The deployment strategy maintains healthy instances during replacement.
- [ ] Requests continue succeeding during the deployment.
- [ ] The deployed version is confirmed.

These checks must be completed using the actual deployment environment.

---

## 12. Rollback Preparation

Even when a deployment uses a zero-downtime strategy, the team must be ready to return to the previous version.

For example:

```text
Current Version: v3.0.0

Problem Detected
       |
       v
Stop Promoting v3.0.0
       |
       v
Restore Previous Image
       |
       v
Verify Health Checks
       |
       v
Confirm API Recovery
```

The previous image must remain available in the registry.

The exact rollback procedure depends on the deployment platform.

The detailed deployment and rollback plan will be formalized in Week 12, Activity 2.

---

## 13. Expected Evidence

To demonstrate this activity, we should collect:

1. Screenshot of the versioned Docker image.
2. Evidence of the image stored in the container registry.
3. Screenshot or logs of the running cloud or production-like deployment.
4. Health check response.
5. Prometheus metrics response.
6. Evidence of the rolling or blue-green deployment.
7. Request results collected during the deployment.
8. Verification that the new application version is running.

Screenshots and test results must come from the real deployment.

---

## 14. Conclusion

In this activity, we prepared the cloud deployment process for TeleMed IA using versioned Docker images, health checks, and a zero-downtime strategy.

The Intelligent Agent API already includes important features such as Spring Boot Actuator, Prometheus metrics, structured logging, request tracing, and graceful shutdown.

These features support deployment monitoring and troubleshooting.

The next step is to execute and validate the deployment using a tagged image, verify that requests remain available during the release, and collect evidence of the result.

This activity also prepares the project for MVP 3, where the deployment plan, Infrastructure as Code, and rollback process will be documented in more detail.
