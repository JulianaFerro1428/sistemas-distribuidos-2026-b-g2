# ADR-C3-01: Multi-AZ High Availability for TeleMed IA PostgreSQL on Huawei Cloud

## Status

Proposed

## Context

TeleMed IA uses microservices with separate data ownership for Identity & Access, Patient Management, Professional Management, Appointment Scheduling, Medical Consultation, Notifications, Document Generation, and the workflow saga state. Most relational data is intended to run on a shared PostgreSQL instance managed by `telemed-ia-infra-postgres`, while each domain retains control over its database objects and migrations.

The current Docker Compose deployment uses a single PostgreSQL runtime, so a database host, storage, or availability-zone failure can interrupt several important capabilities at once. This is a particular risk for authentication, appointment scheduling, consultation records, and persistent saga recovery.

The existing architectural decision `05-architecture/decisions/records/ADR-019-infra-postgres-single-instance.md` explicitly recognizes the shared failure impact; `05-architecture/deployment.md` describes the deployed topology and `06-data/models.md` describes domain ownership.

This proposed cloud redesign addresses the Reliability principle in Section 2 and the PostgreSQL disaster recovery target in Section 4 of `architecture-design-note.md`.

## Decision

For a future Huawei Cloud deployment, I propose replacing the single-container PostgreSQL runtime with **Huawei Cloud RDS for PostgreSQL in a primary/standby configuration across two availability zones in the same region**. This preserves the team's decision to share a PostgreSQL engine while retaining separate domain schemas, users, permissions, and migration ownership; existing exceptions, including Patient Management's incomplete shared-instance migration, must be resolved and tested before cutover.

I would select synchronous replication where supported, enable automated backups and point-in-time recovery, keep the database reachable only through private networking and restrictive security groups, and configure the microservices to retry connections safely after failover.

The design objectives are an **RTO of 5 minutes** and an **RPO of 5 minutes**, to be verified with failover drills and recovery tests rather than assumed from the service name.

Identity & Access remains the owner of authentication data, and individual `-db` repositories remain responsible for Liquibase or Flyway changes. This is a proposed architecture, not a claim that RDS is already deployed for TeleMed IA.

## Alternatives considered

- **Keep PostgreSQL in one Docker Compose container on an ECS virtual machine:** This would require fewer architectural changes and could be inexpensive for the academic environment. I rejected it for the target cloud design because a single host or zone failure would still interrupt multiple services and require more manual recovery.

- **Deploy a separate RDS PostgreSQL instance for every bounded context:** This would reduce the shared database failure impact and provide stronger physical isolation. I rejected it for this stage because it would multiply database instance costs, backup configurations, operational work, and migration complexity, while departing from the shared-engine decision recorded in ADR-019.

## Consequences

- **Positive:** A standby in a different availability zone provides a failover target for the relational data store. Automated backups and tested recovery procedures can improve recoverability. Existing domain-specific permissions, APIs, and migration responsibilities can be preserved, reducing changes to application business logic.

- **Negative:** A primary/standby RDS instance costs more than a single local PostgreSQL container and introduces replication, network, connection-retry, and migration considerations. Failover may briefly interrupt requests and requires validation. A two-AZ deployment does not by itself provide protection against a full-region outage; the RTO and RPO remain unproven until tested.