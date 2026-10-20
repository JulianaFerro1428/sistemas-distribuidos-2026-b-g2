<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Group 2
- SPRINT_GOAL: Make the HU-06 AI pre-consultation journey observable end to end (traces, logs, metrics, SLOs, alerts, runbook and incident drill) and lay the Notifications database foundation.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-06 | Intelligent Agent pre-consultation observability: trace propagation, structured log contract, instrumentation prototype and golden-signal metrics, SLOs, burn-rate alerts, runbook and incident drill (W11 workshop, CP0-CP9) | doing | [CP3 instrumentation](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/622f020ee57787f26bd2cfaf8906683a6a286172), [CP4 metrics](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/12ca58e56f754afd933abf4c8166eba63b593311), [CP7 alerts](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/ea4ed6cae941685ba5da8208ac33363bd556e2e2), [CP9 postmortem](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/a618acaf968b060b46a9e2fa863b36545fd77b72) |
| TECH-NOTIF-DB | Notifications database: Liquibase skeleton, migration executor and CI; notification schema, table, indexes and grants | doing | [Notifications DB #1](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/1), [#2 (approved)](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/2) |

> **Scope note:** The technical row documents supporting work; it is not a new product HU identifier.

## 2. My individual contribution

- **Intelligent Agent observability (HU-06, W11 workshop):** Mapped the pre-consultation journey (agent portal, api-gateway, intelligent-agent-api, MongoDB `outbox_events`, worker, RabbitMQ, medical-consultation-api) and its W3C trace propagation; defined a structured log contract aligned with the agent-service OpenAPI contract; built a Spring Boot instrumentation prototype (traceparent filter, JSON logger, outbox propagation, Prometheus metrics, passing test); defined the golden signals and metric catalog with bounded labels.
- **Reliability for HU-06 (W11 workshop, delivery 2):** Defined three SLOs (99.0 % availability, 95 % of message turns in 4 s, 99 % of summaries published in 60 s) with a 600-request error budget and a team budget policy; wrote multi-window burn-rate alerts (14.4x/1 h and 6x/6 h); wrote the fast-burn runbook; ran an incident drill (slow MongoDB `intelligent_agent`) with a blameless postmortem and three follow-up user stories.
- **Notifications DB:** Set up `telemed-ia-notifications-db` with a Liquibase skeleton, migration executor and CI (PR #1), and created the notification schema, table, indexes and grants (PR #2, approved).

## 3. Blockers and risks

- **HU-06 observability:** The instrumentation is a prototype in the workshop fork; intelligent-agent-api, telemed-ia-worker and RabbitMQ (ADR-011) are not implemented yet, the gateway has no agent route, and NGINX does not expose Prometheus metrics.
- **Alerting gap:** the incident drill showed that the availability alert fires only at minute 40 for a slow dependency; a latency burn-rate alert is needed.
- **Planned controls:** the feature flag and circuit breaker used in the runbook do not exist yet in intelligent-agent-api.
- **Notifications DB:** PR #1 and PR #2 are still open; the notifications API that will own this schema is not implemented yet.

## 4. Plan for next week

- Move the W11 instrumentation (traceparent filter, JSON logs, metrics) into the real intelligent-agent-api once the service exists.
- Add the postmortem user stories to the next sprint: latency burn-rate alert, feature flag to skip the pre-consultation, off-peak index builds.
- Merge Notifications DB PR #1 and PR #2 through the per-environment flow.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

### Notifications DB

- [#1](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/1) · [#2](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/2)

### W11 Observability workshop — HU-06 (personal fork)

- CP0: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/b195ad52c74f92b48ac50e80a053f1ae2cdcdea3
- CP1: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/f7973b13da0d1b92ede0e439816f165a0d573178
- CP2: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/ef1b7111de0d6a6c9068d4b002489ad91e68afbe
- CP3: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/622f020ee57787f26bd2cfaf8906683a6a286172
- CP1/CP2 contract alignment: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/99e853d4507fc92a00937384f2bc6a76ed5728df
- CP4: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/12ca58e56f754afd933abf4c8166eba63b593311
- CP6: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/072ebd2eacb5478f3efcedb602daad96ccae4cba
- CP7: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/ea4ed6cae941685ba5da8208ac33363bd556e2e2
- CP8: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/922f5bd03596543085c445fb7581bdd2e693c9ea
- CP9: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/a618acaf968b060b46a9e2fa863b36545fd77b72
- Workshop folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/11-week/workshop
