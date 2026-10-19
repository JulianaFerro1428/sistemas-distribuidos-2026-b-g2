<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Group 2
- SPRINT_GOAL: Consolidate TeleMed IA MVP 2 through service-owned persistence, Identity & Access lifecycle verification, HU-06 Intelligent Agent UI/database delivery and observability, shared PostgreSQL and MongoDB infrastructure, the Notifications database foundation, environment-based promotion, architecture alignment, and evidence-based release preparation.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-01 | Patient registration: isolated Identity persistence and durable registration idempotency | done | [Identity DB #45](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/45), [#49 (QA)](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/49) |
| HU-02 | Authentication/session lifecycle: shared PostgreSQL alignment and release readiness | done | [Identity DB #44](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/44), [#48 (QA)](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/48), [PostgreSQL Infra #8](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/8) |
| HU-03 | Password recovery/reset: token lifecycle hardening and real HTTP/PostgreSQL E2E tests | doing | [Identity DB #46](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/46), [API #83](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/83), [API #84](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/84) |
| HU-06 | Intelligent Agent pre-consultation: UI experience, MongoDB persistence, idempotency/outbox storage foundation and component releases | doing | [Agent Portal #38](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/38), [Agent DB #21](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/21) |
| HU-06 | Intelligent Agent pre-consultation observability: trace propagation, structured log contract, instrumentation prototype and golden-signal metrics (W11 workshop, CP0-CP4) | doing | [CP3 instrumentation](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/622f020ee57787f26bd2cfaf8906683a6a286172), [CP4 metrics](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/12ca58e56f754afd933abf4c8166eba63b593311) |
| TECH-NOTIF-DB | Notifications database: Liquibase skeleton, migration executor and CI; notification schema, table, indexes and grants | doing | [Notifications DB #1](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/1), [#2 (approved)](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/2) |
| TECH-INFRA-MONGO | Secure MongoDB replica set, domain runtime user, integration CI and version 2.0.0 | done | [Mongo Infra #5](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/5), [#14 (release)](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/14) |
| TECH-INFRA-POSTGRES | Connect Identity & Access to the shared, service-isolated PostgreSQL platform | done | [PostgreSQL Infra #8](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/8), [Identity DB #52](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/52) |
| TECH-FRONT | TeleMed IA frontend shell, navigation, synthetic dashboard, federation alignment and integration-contract tests | doing | [Front #7](https://github.com/code-corhuila/telemed-ia-front/pull/7), [#12](https://github.com/code-corhuila/telemed-ia-front/pull/12), [#14](https://github.com/code-corhuila/telemed-ia-front/pull/14) |
| TECH-DOCS | Align service ownership, ADR-004, event contracts, API/gateway/security boundaries and runtime documentation | done | [Docs #53](https://github.com/code-corhuila/telemed-ia-docs/pull/53), [#70](https://github.com/code-corhuila/telemed-ia-docs/pull/70), [#73](https://github.com/code-corhuila/telemed-ia-docs/pull/73) |
| MVP2-RELEASE | Component promotion to main and v2.0.0 release preparation; integrated failure/compensation demonstration | doing | [Agent Portal #38](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/38), [Agent DB #21](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/21), [Mongo Infra #14](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/14) |

> **Scope note:** The `done` status above applies to the stated user-story capability or supporting repository task, not a claim that the full distributed MVP 2 has passed an end-to-end failure/compensation demonstration. The Agent API/event transport and system-wide saga remain integration work. The technical rows document supporting work; they are not new product HU identifiers.

## 2. My individual contribution

- **Identity & Access (HU-01, HU-02, HU-03):** Strengthened registration idempotency persistence, password-reset expiration/supersession/concurrency behavior, and the alignment of Identity database schemas and application mappings. Created E2E tests for password recovery, expired and reused tokens, and successful sign-in after reset; replaced a duplicated test schema with official Liquibase migrations and PostgreSQL Testcontainers.
- **PostgreSQL platform:** Integrated the Identity & Access persistence boundary with the shared PostgreSQL runtime, domain roles and migration execution, preserving the Database per Service rule as logical ownership rather than a separate physical server for every service.
- **Intelligent Agent DB (HU-06):** Worked on the MongoDB/Liquibase foundation, conversation and pre-consultation persistence, appointment references, validation/indexes, least-privilege roles, idempotency records, and the `outbox_events` persistence foundation. Promoted QA-validated changes to release `2.0.0`.
- **Intelligent Agent observability (HU-06, W11 workshop):** Mapped the pre-consultation journey (agent portal, api-gateway, intelligent-agent-api, MongoDB `outbox_events`, worker, RabbitMQ, medical-consultation-api) and its W3C trace propagation; defined a structured log contract aligned with the agent-service OpenAPI contract; built a Spring Boot instrumentation prototype (traceparent filter, JSON logger, outbox propagation, Prometheus metrics, passing test); defined the golden signals and metric catalog with bounded labels.
- **Notifications DB:** Set up `telemed-ia-notifications-db` with a Liquibase skeleton, migration executor and CI (PR #1), and created the notification schema, table, indexes and grants (PR #2, approved).
- **MongoDB infrastructure:** Implemented and verified persistent replica-set operation, authentication, keyFile security, application-user provisioning, least-privilege integration and repeatable CI/QA checks; released infrastructure version `2.0.0`.
- **Intelligent Agent Portal (HU-06):** Developed/refined the Angular pre-consultation experience, including consultation-reason capture, conversation flow, synthetic follow-up, patient completion, UI polish and deployment configuration. Promoted the work across QA/release branches and published portal `2.0.0`.
- **Shared frontend:** Worked on native-federation development, shared shell layout/navigation, a synthetic patient dashboard and API client/interceptor characterization tests.
- **Architecture documentation:** Aligned ADR-004, data ownership, service catalog/dependencies, API contracts, Appointment/Consultation/Agent responsibilities, Identity events, Gateway/JWT trust boundaries and operational runbooks with the real mixed migration state of TeleMed IA.
- **Delivery and traceability:** Used feature/fix/chore child branches and PR-based reviews, with `cherry-pick -x` for promotion, and produced release evidence for MongoDB Infrastructure, Agent DB and Agent Portal. Avoided treating documented event contracts, database outbox collections or UI demos as proof of an active broker or completed distributed saga.

## 3. Blockers and risks

- **MVP 2 end-to-end integration is not fully evidenced:** Independent component releases do not yet prove that the entire platform boots, communicates and recovers together.
- **Saga and compensation:** The planned multi-service compensating workflow still requires an implemented, repeatable failure-injection test and persisted-state evidence. It must not be marked complete based only on architecture diagrams.
- **Outbox and consumers:** The Agent DB defines outbox/idempotency persistence structures, but reliable broker relay and consumer-side deduplication have not been verified end to end. The Appointment API documentation still describes a no-op event publisher; a defined event is not proof of publication.
- **HU-06 integration:** The Agent Portal and database releases are not the same as an independently completed Agent API connected to the full medical appointment lifecycle; part of the frontend flow uses synthetic interactions.
- **HU-06 observability:** The instrumentation is a prototype in the workshop fork; intelligent-agent-api, telemed-ia-worker and RabbitMQ (ADR-011) are not implemented yet, the gateway has no agent route, and NGINX does not expose Prometheus metrics.
- **Notifications DB:** PR #1 and PR #2 are still open; the notifications API that will own this schema is not implemented yet.
- **HU-03:** Preserve recovery-token expiry, one-time use, atomic consumption, security and consistent schema/mapping behavior; email transport and remaining lifecycle/CI verification should be tracked explicitly.
- **Shared runtime boundaries:** PostgreSQL schemas/roles and MongoDB domain roles must prevent cross-service writes, with migrations executed only by their owning repositories.
- **CI and environment risk:** GitHub Actions availability, real PostgreSQL/MongoDB integration tests, secret injection, Docker startup order and per-environment configuration need continued verification.
- **Release traceability:** A release PR, an approved merge and a `v2.0.0` tag are different pieces of evidence. The final system-wide release must reference exact component versions and a passing release checklist.

## 4. Plan for next week

- Close any remaining **Corte 2 / MVP 2** release gaps using QA-proven changes, an approved PR to `main`, and verified `v2.0.0` tags per released component.
- Bring up the integrated system with Docker Compose, valid per-environment configuration, service health checks and real API contracts; document exactly which components are live.
- Complete or explicitly track the Agent API, persistent event relay and idempotent consumers; demonstrate that repeated `eventId` values cause no duplicate business side effects.
- Implement or validate **one real saga with a compensating action**, inject a controlled failure and collect before/after state, logs and test evidence.
- Verify HU-03 recovery/reset behavior against official Liquibase migrations, real HTTP requests and PostgreSQL; complete any remaining email/revocation requirements.
- Merge Notifications DB PR #1 and PR #2 through the per-environment flow.
- W11 workshop delivery 2: SLOs and error budget, burn-rate alerts, runbook, incident drill and postmortem for the HU-06 journey.
- Consolidate the MVP 2 release checklist, CHANGELOG/ADR links, component tags, approvals, test reports and demo evidence without claiming unverified success.
- Complete the Corte 2 retrospective and enter integration/startup/contract/consistency issues into the Corte 3 backlog.
- Start **Corte 3** priorities: observability, security hardening, deployment reliability, CI/CD automation and final integrated release readiness.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

> **Self-check qualification:** The boxes refer to the repository contributions evidenced this week. They do not certify full integrated MVP 2 acceptance, a completed saga, or globally passing CI for every service.

## 6. Evidence links

**PR author:** [`JulianaFerro1428`](https://github.com/JulianaFerro1428)  
**Review window:** October 5–10, 2026 (**110 Pull Requests created** across 8 repositories).  
**Evidence type:** Pull Request URLs only (rather than individual commit URLs). Some PRs created in this window were approved/merged after October 10.

### Identity & Access API — HU-03 E2E / official schema

- [#83](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/83) · [#84](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/84)

### Identity & Access DB — persistence, idempotency, QA, release

- [#44](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/44) · [#45](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/45) · [#46](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/46) · [#47](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/47) · [#48](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/48) · [#49](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/49) · [#50](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/50)
- [#51](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/51) · [#52](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/52)

### Intelligent Agent Portal — HU-06 UI, QA, release

- [#3](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/3) · [#4](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/4) · [#5](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/5) · [#6](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/6) · [#7](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/7) · [#8](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/8) · [#9](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/9)
- [#10](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/10) · [#11](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/11) · [#12](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/12) · [#13](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/13) · [#14](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/14) · [#15](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/15) · [#16](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/16)
- [#17](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/17) · [#18](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/18) · [#19](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/19) · [#20](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/20) · [#21](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/21) · [#22](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/22) · [#23](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/23)
- [#24](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/24) · [#25](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/25) · [#26](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/26) · [#27](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/27) · [#28](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/28) · [#29](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/29) · [#30](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/30)
- [#31](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/31) · [#32](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/32) · [#33](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/33) · [#34](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/34) · [#35](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/35) · [#36](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/36) · [#37](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/37)
- [#38](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/38)

### Intelligent Agent DB — HU-06 MongoDB/Liquibase, reliability, QA, release

- [#1](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/1) · [#2](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/2) · [#3](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/3) · [#4](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/4) · [#5](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/5) · [#6](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/6) · [#7](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/7)
- [#8](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/8) · [#9](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/9) · [#10](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/10) · [#11](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/11) · [#12](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/12) · [#13](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/13) · [#14](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/14)
- [#15](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/15) · [#16](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/16) · [#17](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/17) · [#18](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/18) · [#19](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/19) · [#20](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/20) · [#21](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/21)

### MongoDB Infrastructure — runtime, security, CI, QA, release

- [#1](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/1) · [#2](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/2) · [#3](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/3) · [#4](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/4) · [#5](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/5) · [#6](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/6) · [#7](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/7)
- [#8](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/8) · [#9](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/9) · [#10](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/10) · [#11](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/11) · [#12](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/12) · [#13](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/13) · [#14](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/14)

### PostgreSQL Infrastructure — shared Identity integration

- [#8](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/8)

### Frontend Shell — integration foundation, visual flow, tests

- [#6](https://github.com/code-corhuila/telemed-ia-front/pull/6) · [#7](https://github.com/code-corhuila/telemed-ia-front/pull/7) · [#8](https://github.com/code-corhuila/telemed-ia-front/pull/8) · [#9](https://github.com/code-corhuila/telemed-ia-front/pull/9) · [#10](https://github.com/code-corhuila/telemed-ia-front/pull/10) · [#12](https://github.com/code-corhuila/telemed-ia-front/pull/12) · [#14](https://github.com/code-corhuila/telemed-ia-front/pull/14)

### Architecture and System Documentation — 20 PRs

- [#53](https://github.com/code-corhuila/telemed-ia-docs/pull/53) · [#54](https://github.com/code-corhuila/telemed-ia-docs/pull/54) · [#55](https://github.com/code-corhuila/telemed-ia-docs/pull/55) · [#56](https://github.com/code-corhuila/telemed-ia-docs/pull/56) · [#57](https://github.com/code-corhuila/telemed-ia-docs/pull/57) · [#58](https://github.com/code-corhuila/telemed-ia-docs/pull/58) · [#59](https://github.com/code-corhuila/telemed-ia-docs/pull/59)
- [#60](https://github.com/code-corhuila/telemed-ia-docs/pull/60) · [#62](https://github.com/code-corhuila/telemed-ia-docs/pull/62) · [#63](https://github.com/code-corhuila/telemed-ia-docs/pull/63) · [#64](https://github.com/code-corhuila/telemed-ia-docs/pull/64) · [#65](https://github.com/code-corhuila/telemed-ia-docs/pull/65) · [#66](https://github.com/code-corhuila/telemed-ia-docs/pull/66) · [#67](https://github.com/code-corhuila/telemed-ia-docs/pull/67)
- [#68](https://github.com/code-corhuila/telemed-ia-docs/pull/68) · [#69](https://github.com/code-corhuila/telemed-ia-docs/pull/69) · [#70](https://github.com/code-corhuila/telemed-ia-docs/pull/70) · [#71](https://github.com/code-corhuila/telemed-ia-docs/pull/71) · [#72](https://github.com/code-corhuila/telemed-ia-docs/pull/72) · [#73](https://github.com/code-corhuila/telemed-ia-docs/pull/73)

### Notifications DB — Liquibase foundation, schema, CI

- [#1](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/1) · [#2](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/2)

### W11 Observability workshop — HU-06 (personal fork)

- CP0: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/b195ad52c74f92b48ac50e80a053f1ae2cdcdea3
- CP1: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/f7973b13da0d1b92ede0e439816f165a0d573178
- CP2: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/ef1b7111de0d6a6c9068d4b002489ad91e68afbe
- CP3: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/622f020ee57787f26bd2cfaf8906683a6a286172
- CP1/CP2 contract alignment: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/99e853d4507fc92a00937384f2bc6a76ed5728df
- CP4: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/12ca58e56f754afd933abf4c8166eba63b593311
- Workshop folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/11-week/workshop

### Key MVP 2 release Pull Requests

- **Agent Portal 2.0.0:** https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/38
- **Intelligent Agent DB 2.0.0:** https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/21
- **MongoDB Infrastructure 2.0.0:** https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/14
- **Identity DB shared-PostgreSQL release promotion:** https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/52 (created October 10; merged October 11)
- **PostgreSQL shared-runtime integration:** https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/8
- **Official Identity DB E2E alignment:** https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/84 (created October 10; merged October 11)

### Reference architecture and governance

- [TeleMed IA documentation](https://github.com/code-corhuila/telemed-ia-docs)
- [ADR-004: Database per Service](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/decisions/records/ADR-004-database-per-service.md)
- [Data ownership matrix](https://github.com/code-corhuila/telemed-ia-docs/blob/main/09-microservices/data-ownership-matrix.md)
- [Event catalog](https://github.com/code-corhuila/telemed-ia-docs/blob/main/09-microservices/event-catalog.md)
- [Branching and per-environment PR policy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/branching-policy.md)

> **Validation boundary:** Links to PRs are evidence of contributions and promotion work; they must not be represented as proof of a live, end-to-end saga, outbox relay, consumer idempotency, or integrated failure/compensation demonstration unless separate passing runtime evidence is attached.
