# ADR-C3-02: Horizontal Autoscaling of TeleMed IA Microservices with Huawei Cloud CCE

## Status

Proposed

## Context

TeleMed IA currently runs its backend services as Docker containers coordinated through Docker Compose. The platform follows a microservices and hexagonal architecture: each bounded context owns its application logic and persistent data, while the NGINX API Gateway routes external requests to the appropriate service. The Intelligent Agent (`agent-service`) handles patient pre-consultations using MongoDB and an external AI provider, and Appointment Scheduling (`appointment-service`) handles appointment operations using PostgreSQL. These services have different workload patterns and should not need to scale together.

The current deployment does not provide automatic horizontal scaling when traffic increases. This matters because `04-requirements/non-functional.md` defines a minimum of 50 concurrent users, a P95 response-time target below 5 seconds for the Intelligent Agent under 20 concurrent conversations, and P95 targets below 700 milliseconds for appointment availability and creation under the documented evaluation load. The same file requires stateless services to support replication without compromising data integrity. `05-architecture/deployment.md` documents the Docker Compose topology, and `10-devops/ci-cd.md` confirms that there is no hosted environment or automated cloud deployment yet.

This decision supports **Section 2 (Five principles: Performance efficiency)** and **Section 3 (Scalability)** of `architecture-design-note.md`.

## Decision

For a future Huawei Cloud deployment, I propose running the stateless TeleMed IA backend APIs on **Huawei Cloud Container Engine (CCE)** and using Kubernetes **Horizontal Pod Autoscaling (HPA)** to scale selected services independently. As an initial configuration to validate through load testing, `agent-service` would scale between **2 and 6 replicas** when average CPU utilization reaches a target of **70%**, and `appointment-service` would scale between **2 and 4 replicas** with a CPU target of **65%**. Replicas would be distributed across at least two availability zones, and **Elastic Load Balance (ELB)** would direct traffic to the NGINX API Gateway, preserving the gateway's existing routing responsibilities. Each API would continue validating JWTs and authorization locally; the database, RabbitMQ, and external AI provider would remain separate dependencies rather than being duplicated with each API replica. Readiness checks, resource requests and limits, connection-pool limits, and load tests would be required before approving these thresholds for production. HPA would address application replica capacity, not guarantee a response from the external AI provider or prevent duplicate appointments; existing timeouts, degradation controls, idempotency rules, and database constraints must remain in place.

## Alternatives considered

- **Keep Docker Compose with a fixed number of containers:** This would preserve the current simple academic deployment and avoid introducing Kubernetes operations. I rejected it as the target cloud design because it would require manual intervention to increase or reduce capacity and would not automatically react to changes in workload.
- **Deploy and resize each microservice manually on separate Elastic Cloud Servers (ECS):** This would offer infrastructure separation and direct control over virtual machines. I rejected it because managing instance sizes, deployments, health checks, and traffic distribution separately for every service would add operational effort and make fine-grained horizontal scaling more difficult than CCE with HPA.

## Consequences

- **Positive:** `agent-service` and `appointment-service` could adjust their replica counts independently according to measured load, improving elasticity and reducing the need to overprovision every service. Spreading replicas across availability zones would also reduce the effect of losing a single application node. The existing API contracts, hexagonal domain logic, and domain-owned databases could be retained.
- **Negative:** CCE and Kubernetes introduce configuration and operational complexity, including resource sizing, autoscaling policies, networking, monitoring, and deployment management. Running multiple replicas can increase cloud costs and database connections. CPU-based HPA alone may not reflect slow responses from the external AI provider, so its effectiveness would need to be evaluated with load tests and additional application metrics before relying on it.
