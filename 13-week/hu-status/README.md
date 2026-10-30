<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       13-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 13

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai - Group 2
- SPRINT_GOAL: Threat-model the public login boundary of TeleMed IA (Internet → api-gateway → auth-service on /api/v1/auth) and turn its top risks into security backlog stories and CI security gates for identity-and-access-api.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-02 | Authentication and access: STRIDE of `/api/v1/auth`, account-lock story and negative login test | doing | [Docs PR #143](https://github.com/code-corhuila/telemed-ia-docs/pull/143), [#148](https://github.com/code-corhuila/telemed-ia-docs/pull/148), [issue #152](https://github.com/code-corhuila/telemed-ia-docs/issues/152), [commit 3276bcd](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/3276bcd) |
| HU-02 | Per-account login lock (#152): `login_attempts` table and `429` with `Retry-After` in `auth-service` | done (merged into develop) | [identity-db PR #59](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/59), [identity-api PR #134](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/134) |
| HU-01 | Patient registration: account enumeration on `POST /api/v1/auth/register` | todo | [issue #153](https://github.com/code-corhuila/telemed-ia-docs/issues/153) |
| HU-02 | Gateway global rate limit for `/api/v1/auth` | todo | [issue #154](https://github.com/code-corhuila/telemed-ia-docs/issues/154) |

## 2. My individual contribution
- Claimed the trust boundary Internet → `api-gateway` → `auth-service` (`/api/v1/auth`) in [#139](https://github.com/code-corhuila/telemed-ia-docs/issues/139).
- Mapped the attack surface of TeleMed IA (TB1–TB6, 9 identity assets, 8 entry points): [d8b9402](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/d8b9402).
- Wrote the STRIDE table, risk ranking, residual risk and library references in `05-architecture/security/stride-JulianaFerro1428.md` ([#143](https://github.com/code-corhuila/telemed-ia-docs/pull/143), [#148](https://github.com/code-corhuila/telemed-ia-docs/pull/148)).
- Opened three security stories with labels and priority on the team board: [#152](https://github.com/code-corhuila/telemed-ia-docs/issues/152) (S, P0), [#153](https://github.com/code-corhuila/telemed-ia-docs/issues/153) (I, P1), [#154](https://github.com/code-corhuila/telemed-ia-docs/issues/154) (D, P1).
- Proposed `security.yml` (gitleaks, OWASP dependency-check, authorization tests) and the negative test `LoginAccountLockE2ETest`: [365a7df](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/365a7df).
- Built #152 with red/green TDD commits: migration and grants for `login_attempts` ([identity-db#59](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/59)) and the lock in `LoginService`, JPA adapter and `429` handler ([identity-api#134](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/134)). `LoginAccountLockE2ETest` went from `200 OK` (red) to `429` (green).
- Peer review of [#144](https://github.com/code-corhuila/telemed-ia-docs/pull/144): the S row misses brute force on `POST /internal/service-token` ([review](https://github.com/code-corhuila/telemed-ia-docs/pull/144#pullrequestreview-5699376616)).

## 3. Blockers and risks
- PR #143 was merged by a teammate after the first push, so the second half of the STRIDE is in PR #148.
- GitHub Actions does not start on organization PRs ("account is locked due to a billing issue"), so #59 and #134 were verified only with local runs.

## 4. Plan for next week
- Promote #152 (identity-db#59, then identity-api#134) to `qa` with `git cherry-pick -x`.
- Add `security.yml` to `identity-and-access-api` once Actions runs again.
- Start #153 (neutral answer on `/register`) and #154 (global rate limit in the gateway).

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Workshop folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop
- STRIDE PRs: https://github.com/code-corhuila/telemed-ia-docs/pull/143, https://github.com/code-corhuila/telemed-ia-docs/pull/148
- Security stories: https://github.com/code-corhuila/telemed-ia-docs/issues/152, https://github.com/code-corhuila/telemed-ia-docs/issues/153, https://github.com/code-corhuila/telemed-ia-docs/issues/154
- Review comment: https://github.com/code-corhuila/telemed-ia-docs/pull/144#pullrequestreview-5699376616

## 7. Pull requests in my microservices
The 30 merged pull requests I opened from 2026-10-26 to 2026-10-30 in the repositories I own
(identity, intelligent agent and notifications), newest first, with the target branch and the
creation date.

### Identity & Access

**`telemed-ia-identity-and-access-api`** (1 PR)

- [#134](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/134) → `develop` · 2026-10-29 · feat(auth): lock login per account after repeated failures (HU-02)

**`telemed-ia-identity-and-access-db`** (1 PR)

- [#59](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/59) → `develop` · 2026-10-29 · feat(db): add login_attempts table for per-account login lock (HU-02)

**`telemed-ia-identity-and-access-portal`** (1 PR)

- [#23](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/23) → `develop` · 2026-10-26 · fix(deploy): probe the container health over IPv4

### Intelligent agent

**`telemed-ia-intelligent-agent-api`** (12 PRs)

- [#42](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/42) → `develop` · 2026-10-28 · feat(agent): open child spans for appointments, AI attempts and agent-db (HU-06)
- [#41](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/41) → `develop` · 2026-10-28 · feat(agent): export the request spans to the platform collector (HU-06)
- [#40](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/40) → `develop` · 2026-10-27 · docs(agent): add the SLOs, alert policy, runbook and incident drill (HU-06)
- [#39](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/39) → `develop` · 2026-10-27 · docs(agent): add the observability journey, log contract and metric catalog (HU-06)
- [#38](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/38) → `develop` · 2026-10-27 · feat(agent): bound agent-db waits and answer 503 when it does not respond (HU-06)
- [#37](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/37) → `develop` · 2026-10-27 · feat(agent): open a circuit breaker after repeated AI provider failures (HU-06)
- [#36](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/36) → `develop` · 2026-10-27 · feat: let operators pause new pre-consultations
- [#35](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/35) → `develop` · 2026-10-27 · feat: add the SLO recording rules and burn-rate alerts
- [#34](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/34) → `develop` · 2026-10-27 · feat: store the request trace context with the outbox event
- [#33](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/33) → `develop` · 2026-10-27 · feat: expose the saturation of the request threads
- [#32](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/32) → `develop` · 2026-10-27 · feat: log the pre-consultation milestones as week 11 contract events
- [#31](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/31) → `develop` · 2026-10-27 · feat: continue the request trace in the appointment service call

**`telemed-ia-intelligent-agent-db`** (1 PR)

- [#22](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/22) → `develop` · 2026-10-27 · feat(agent-db): accept the W3C trace context on outbox events

**`telemed-ia-intelligent-agent-portal`** (1 PR)

- [#45](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/45) → `develop` · 2026-10-27 · feat(agent-portal): tell the patient the appointment goes on while pre-consultations are paused (HU-06)

### Notifications

**`telemed-ia-notifications-api`** (4 PRs)

- [#24](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/24) → `develop` · 2026-10-26 · feat(api): consume the appointment reminders from appointments.events
- [#23](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/23) → `develop` · 2026-10-26 · feat(api): write the appointment reminder e-mail
- [#22](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/22) → `develop` · 2026-10-26 · feat(api): notify appointment reminders unless the appointment was cancelled
- [#21](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/21) → `develop` · 2026-10-26 · fix(api): reach identity as identity-api on the platform network

**`telemed-ia-notifications-portal`** (9 PRs)

- [#9](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/9) → `develop` · 2026-10-26 · fix(inbox): hide the type chips scrollbar on narrow screens
- [#8](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/8) → `develop` · 2026-10-26 · chore: add CI and the Docker image of the notifications portal
- [#7](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/7) → `develop` · 2026-10-26 · feat: show the inbox loading, empty and error states
- [#6](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/6) → `develop` · 2026-10-26 · feat: show the notifications inbox page
- [#5](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/5) → `develop` · 2026-10-26 · feat: keep the inbox state in useInbox
- [#4](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/4) → `develop` · 2026-10-26 · feat: present inbox notifications in Spanish
- [#3](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/3) → `develop` · 2026-10-26 · feat: add the notifications inbox data layer
- [#2](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/2) → `develop` · 2026-10-26 · chore: build the notifications portal as a federated remote
- [#1](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/1) → `develop` · 2026-10-26 · chore: add notifications portal dependencies
