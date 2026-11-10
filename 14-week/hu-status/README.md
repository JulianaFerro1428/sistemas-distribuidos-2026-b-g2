<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       14-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 14

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai - Group 2
- SPRINT_GOAL: Close my services for the MVP: run the week 11 observability (trace-aware JSON logs, metrics, spans and SLO alerts) of identity, notifications and the intelligent agent on the shared platform, finish the agent outbox relay and idempotent patient registration, verify every journey end to end through the gateway, and document all of it in telemed-ia-docs.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-03 | Password recovery: week 11 observability of the recovery journey in identity and notifications (trace-aware JSON logs, domain events, metric catalog, fast `503` when the database does not answer, spans to the platform collector, outbox events in the request trace) | done (merged into develop) | [identity-api #135](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/135)–[#140](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/140), [identity-db #60](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/60), [notifications-api #25](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/25)–[#30](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/30) |
| HU-03 | Password recovery: Prometheus scrape and SLO alerts of identity and notifications on the platform | done (merged into develop) | [infra-postgres #33](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/33), [#34](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/34), [docs #157](https://github.com/code-corhuila/telemed-ia-docs/pull/157)–[#160](https://github.com/code-corhuila/telemed-ia-docs/pull/160) |
| HU-06 | Intelligent agent pre-consultation: outbox relay that publishes `PreConsultationSummaryGenerated` to `agent.events` with publisher confirms and the request trace, on a schedule | done (merged into develop) | [agent-api #45](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/45), [#47](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/47), [#48](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/48), [docs #174](https://github.com/code-corhuila/telemed-ia-docs/pull/174) |
| HU-06 | Intelligent agent pre-consultation: SLO alerts evaluated by the platform Prometheus | done (merged into develop) | [infra-postgres #32](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/32), [agent-api #43](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/43), [#44](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/44) |
| HU-01 | Patient registration: idempotent `POST /api/v1/auth/register` with `Idempotency-Key` (replay `200`, reuse with another body `422`, missing key `400`) | done (merged into develop) | [identity-api #141](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/141), [#142](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/142), [docs #169](https://github.com/code-corhuila/telemed-ia-docs/pull/169), [#170](https://github.com/code-corhuila/telemed-ia-docs/pull/170) |
| HU-17 / HU-18 | Appointment notifications and reminders: the OpenAPI contract, data model, decisions and consumed queues in telemed-ia-docs now match the implemented inbox | done (merged into main) | [docs #171](https://github.com/code-corhuila/telemed-ia-docs/pull/171), [#172](https://github.com/code-corhuila/telemed-ia-docs/pull/172), [#173](https://github.com/code-corhuila/telemed-ia-docs/pull/173) |

## 2. My individual contribution
- **Week 11 observability on the platform (HU-03):** identity-api and notifications-api now write the week 11 JSON log contract with `trace.id`/`span.id`, log the domain events of login, recovery and e-mail delivery, expose the metric catalog on the management port, answer `503` quickly when their database does not respond, and export the request, outbox and message-consumption spans to the platform OpenTelemetry collector ([identity-api #135–#140](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/140), [notifications-api #25–#30](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/29)). identity-db stores the W3C `traceparent` with each outbox event ([#60](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/60)), so the recovery e-mail continues the trace of the request that asked for it.
- **SLO alerts on the shared platform:** the platform Prometheus in infra-postgres scrapes identity, notifications and the intelligent agent and evaluates their SLO burn-rate alerts, each with tests of the scrape job, the alerts and their routing ([#32](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/32), [#33](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/33), [#34](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/34)). The agent's local copy of the rules was removed ([agent-api #43](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/43)).
- **Agent outbox relay (HU-06):** the relay reads the pending outbox events from agent-db, publishes them to the `agent.events` exchange with publisher confirms and the stored trace context, and marks them published; it runs on a schedule and is covered by `OutboxRelayE2ETest` ([#45](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/45), [#47](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/47), [#48](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/48)). No agent-db migration was needed: only the event `status` changes.
- **Idempotent patient registration (HU-01):** with red/green TDD commits, first a refactor that shares the account-creation retry ([#141](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/141)), then the required `Idempotency-Key` on `/register` ([#142](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/142)).
- **End-to-end verification through the gateway (2026-11-06):** the API journeys HU-01/02/03 (15 of 15 checks), the HU-17/18 inbox (including an idempotent duplicate event) and HU-06 (6 of 6 checks, with a real appointment) passed from the `develop` branches, and the traces were checked in Jaeger. The run found and fixed three defects:
  - the notifications-api image no longer built, because a Testcontainers test ran inside the Docker build ([notifications-api #32](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/32));
  - the front image did not start and its dev proxy pointed to the wrong port instead of the gateway ([front #45](https://github.com/code-corhuila/telemed-ia-front/pull/45));
  - the agent portal did not publish port 4202, so the shell could not load the remote ([agent-portal #47](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/47)).
- **Governance:** the course pull request template and README links to telemed-ia-docs in identity-db, identity-portal, notifications-api, notifications-portal, agent-api and agent-portal ([identity-db #61](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/61), [identity-portal #25](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/25), [notifications-api #31](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/31), [notifications-portal #10](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/10), [agent-api #46](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/46), [agent-portal #46](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/46)).
- **Documentation (12 PRs in telemed-ia-docs):** identity register idempotency, account administration, outbox and `resetUrl` ([#169](https://github.com/code-corhuila/telemed-ia-docs/pull/169), [#170](https://github.com/code-corhuila/telemed-ia-docs/pull/170)); the notifications contract, data model, decisions and events ([#171](https://github.com/code-corhuila/telemed-ia-docs/pull/171)–[#173](https://github.com/code-corhuila/telemed-ia-docs/pull/173)); the agent outbox relay ([#174](https://github.com/code-corhuila/telemed-ia-docs/pull/174)); stale project-control entries and two new risks ([#175](https://github.com/code-corhuila/telemed-ia-docs/pull/175)); the portal framework allocation ([#176](https://github.com/code-corhuila/telemed-ia-docs/pull/176)); and the week 11 observability decisions and runbooks of identity and notifications ([#157](https://github.com/code-corhuila/telemed-ia-docs/pull/157)–[#160](https://github.com/code-corhuila/telemed-ia-docs/pull/160)).
- **Certification C3:** evidence of chapters 8 to 14 and the updated study log in this fork ([56e1768](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/56e1768), [d7ab259](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/d7ab259), [f524fe7](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/f524fe7)).

## 3. Blockers and risks
- GitHub Actions does not start on organization PRs ("account is locked due to a billing issue"), so every PR of the week was verified with local runs (`mvn verify`, Testcontainers, image builds and the end-to-end checks above), cited in each PR body.
- Building the images from `develop` exposed defects that the unit tests did not catch (notifications-api image, front image, agent portal port). Risk: an image can break without CI; mitigation until Actions runs again is to build the images and run the end-to-end checks before closing a story.
- No service consumes `agent.events` yet, so the agent events are published but not used by another service.
- Branch protection is not enforced in the private repositories (docs, infra-postgres, infra-mongo); recorded as a risk in project control ([#175](https://github.com/code-corhuila/telemed-ia-docs/pull/175)).

## 4. Plan for next week
- HU-05: build the professional onboarding saga in `telemed-ia-workflow` as a custom Java orchestrator (Anexo E): create the account, create the profile and activate the account, with compensations in reverse order, saga state in PostgreSQL and a gateway route.
- Promote my finished repositories to `qa` with `git cherry-pick -x` and stop at `qa` until the team reviews them.
- Close the remaining documentation gaps (gateway route table, navigation map) and add the evidence comments to the HU issues.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Week folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/14-week
- Week 11 observability, identity: https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/140
- Week 11 observability, notifications: https://github.com/code-corhuila/telemed-ia-notifications-api/pull/29
- Platform SLO alerts: https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/32, https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/33, https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/34
- Agent outbox relay: https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/48
- Idempotent registration: https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/142
- Defects found by the end-to-end run: https://github.com/code-corhuila/telemed-ia-notifications-api/pull/32, https://github.com/code-corhuila/telemed-ia-front/pull/45, https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/47
- Documentation: https://github.com/code-corhuila/telemed-ia-docs/pulls?q=is%3Apr+author%3AJulianaFerro1428+created%3A2026-11-02..2026-11-06

## 7. Pull requests in my microservices
The 44 merged pull requests I opened from 2026-11-02 to 2026-11-06 in the repositories I own
(identity, intelligent agent and notifications), the shared repositories and the documentation,
newest first, with the target branch and the creation date.

### Identity & Access

**`telemed-ia-identity-and-access-api`** (8 PRs)

- [#142](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/142) → `develop` · 2026-11-06 · feat(identity-api): make patient registration idempotent with Idempotency-Key
- [#141](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/141) → `develop` · 2026-11-06 · refactor(identity-api): share the account-creation retry for patient registration
- [#140](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/140) → `develop` · 2026-11-03 · feat(api): export the request and outbox publication spans to the platform collector (HU-03)
- [#139](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/139) → `develop` · 2026-11-03 · feat(api): answer 503 quickly when identity-db does not respond (HU-03)
- [#138](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/138) → `develop` · 2026-11-03 · feat(api): expose the week 11 metric catalog on the management port (HU-03)
- [#137](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/137) → `develop` · 2026-11-03 · feat(api): log the week 11 domain events of login and recovery (HU-03)
- [#136](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/136) → `develop` · 2026-11-03 · feat(api): publish each outbox event in the trace of its request (HU-03)
- [#135](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/135) → `develop` · 2026-11-03 · feat: write the week 11 JSON logs with the request trace context (HU-03)

**`telemed-ia-identity-and-access-db`** (2 PRs)

- [#61](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/61) → `develop` · 2026-11-06 · chore(governance): add the course pull request template
- [#60](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/60) → `develop` · 2026-11-03 · feat(db): store the W3C traceparent with outbox events (HU-03)

**`telemed-ia-identity-and-access-portal`** (1 PR)

- [#25](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/25) → `develop` · 2026-11-06 · chore(governance): add the pull request template and point the readme to telemed-ia-docs

### Intelligent agent

**`telemed-ia-intelligent-agent-api`** (6 PRs)

- [#48](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/48) → `develop` · 2026-11-06 · feat(agent): run the outbox relay to agent.events on a schedule (HU-06)
- [#47](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/47) → `develop` · 2026-11-06 · feat(agent): publish outbox events to agent.events with confirms and the request trace (HU-06)
- [#46](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/46) → `develop` · 2026-11-06 · chore(governance): add the course pull request template
- [#45](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/45) → `develop` · 2026-11-06 · feat(agent): relay the pending outbox events and mark them published (HU-06)
- [#44](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/44) → `develop` · 2026-11-03 · docs(observability): say that the agent exports its spans to the platform collector (HU-06)
- [#43](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/43) → `develop` · 2026-11-03 · chore(observability): remove the SLO rules copy now loaded by the platform (HU-06)

**`telemed-ia-intelligent-agent-portal`** (2 PRs)

- [#47](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/47) → `develop` · 2026-11-06 · fix(agent-portal): publish port 4202 so the shell can load the remote
- [#46](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/46) → `develop` · 2026-11-06 · docs(readme): point the governance links to telemed-ia-docs

### Notifications

**`telemed-ia-notifications-api`** (8 PRs)

- [#32](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/32) → `develop` · 2026-11-06 · fix(notifications-api): name the span export test as an e2e test so the image builds
- [#31](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/31) → `develop` · 2026-11-06 · chore(governance): add the pull request template and point the readme to telemed-ia-docs
- [#30](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/30) → `develop` · 2026-11-03 · fix(api): keep the week 11 log contract format in the prod profile (HU-03)
- [#29](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/29) → `develop` · 2026-11-03 · feat(api): export the request and message consumption spans to the platform collector (HU-03)
- [#28](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/28) → `develop` · 2026-11-03 · feat(api): answer 503 quickly when notifications-db does not respond (HU-03)
- [#27](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/27) → `develop` · 2026-11-03 · feat(api): expose the week 11 metrics of requests and consumed messages (HU-03)
- [#26](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/26) → `develop` · 2026-11-03 · feat(api): continue the trace of the message on every listener (HU-03)
- [#25](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/25) → `develop` · 2026-11-03 · feat(api): write the week 11 JSON logs with the request trace context (HU-03)

**`telemed-ia-notifications-portal`** (1 PR)

- [#10](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/10) → `develop` · 2026-11-06 · chore(governance): add the pull request template and point the readme to telemed-ia-docs

### Shared repositories

**`telemed-ia-infra-postgres`** (3 PRs)

- [#34](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/34) → `develop` · 2026-11-03 · feat(infra): scrape notifications and evaluate its SLO alerts on the platform (HU-03)
- [#33](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/33) → `develop` · 2026-11-03 · feat(infra): scrape identity and evaluate its SLO alerts on the platform (HU-03)
- [#32](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/32) → `develop` · 2026-11-03 · feat(infra): scrape the intelligent agent and evaluate its SLO alerts on the platform (HU-06)

**`telemed-ia-front`** (1 PR)

- [#45](https://github.com/code-corhuila/telemed-ia-front/pull/45) → `develop` · 2026-11-06 · fix(front): start the Docker image and send the dev proxy to the gateway

### Documentation

**`telemed-ia-docs`** (12 PRs)

- [#176](https://github.com/code-corhuila/telemed-ia-docs/pull/176) → `main` · 2026-11-06 · docs(architecture): record the portal framework allocation
- [#175](https://github.com/code-corhuila/telemed-ia-docs/pull/175) → `main` · 2026-11-06 · docs(project-control): sync stale status entries and record two risks
- [#174](https://github.com/code-corhuila/telemed-ia-docs/pull/174) → `main` · 2026-11-06 · docs(agent): record the outbox relay in the agent API
- [#173](https://github.com/code-corhuila/telemed-ia-docs/pull/173) → `main` · 2026-11-06 · docs(notifications): document the consumed events, queues and README status
- [#172](https://github.com/code-corhuila/telemed-ia-docs/pull/172) → `main` · 2026-11-06 · docs(notifications): align the data model and record the inbox decisions
- [#171](https://github.com/code-corhuila/telemed-ia-docs/pull/171) → `main` · 2026-11-06 · docs(notifications): replace the contract with the implemented inbox API
- [#170](https://github.com/code-corhuila/telemed-ia-docs/pull/170) → `main` · 2026-11-06 · docs(identity): document the outbox, idempotency keys and resetUrl
- [#169](https://github.com/code-corhuila/telemed-ia-docs/pull/169) → `main` · 2026-11-06 · docs(identity): document register idempotency and account administration
- [#160](https://github.com/code-corhuila/telemed-ia-docs/pull/160) → `main` · 2026-11-03 · docs(notifications): rewrite the runbook with the week 11 alerts and the real deployment (HU-03)
- [#159](https://github.com/code-corhuila/telemed-ia-docs/pull/159) → `main` · 2026-11-03 · docs(notifications): document the week 11 observability of the recovery e-mail journey (HU-03)
- [#158](https://github.com/code-corhuila/telemed-ia-docs/pull/158) → `main` · 2026-11-03 · docs(identity): document the week 11 observability of the password recovery journey (HU-03)
- [#157](https://github.com/code-corhuila/telemed-ia-docs/pull/157) → `main` · 2026-11-03 · docs(notifications): record week 11 trace, log and port decisions
