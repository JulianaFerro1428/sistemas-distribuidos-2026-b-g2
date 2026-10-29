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
| HU-01 | Patient registration: account enumeration on `POST /api/v1/auth/register` | todo | [issue #153](https://github.com/code-corhuila/telemed-ia-docs/issues/153) |
| HU-02 | Gateway global rate limit for `/api/v1/auth` | todo | [issue #154](https://github.com/code-corhuila/telemed-ia-docs/issues/154) |

## 2. My individual contribution
- Claimed the trust boundary Internet → `api-gateway` → `auth-service` (`/api/v1/auth`) in [#139](https://github.com/code-corhuila/telemed-ia-docs/issues/139).
- Mapped the attack surface of TeleMed IA (TB1–TB6, 9 identity assets, 8 entry points): [d8b9402](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/d8b9402).
- Wrote the STRIDE table, risk ranking, residual risk and library references in `05-architecture/security/stride-JulianaFerro1428.md` ([#143](https://github.com/code-corhuila/telemed-ia-docs/pull/143), [#148](https://github.com/code-corhuila/telemed-ia-docs/pull/148)).
- Opened three security stories with labels and priority on the team board: [#152](https://github.com/code-corhuila/telemed-ia-docs/issues/152) (S, P0), [#153](https://github.com/code-corhuila/telemed-ia-docs/issues/153) (I, P1), [#154](https://github.com/code-corhuila/telemed-ia-docs/issues/154) (D, P1).
- Proposed `security.yml` (gitleaks, OWASP dependency-check, authorization tests) and the negative test `LoginAccountLockE2ETest`: [365a7df](https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/commit/365a7df).
- Peer review of [#144](https://github.com/code-corhuila/telemed-ia-docs/pull/144): the S row misses brute force on `POST /internal/service-token` ([review](https://github.com/code-corhuila/telemed-ia-docs/pull/144#pullrequestreview-5699376616)).

## 3. Blockers and risks
- PR #143 was merged by a teammate after the first push, so the second half of the STRIDE is in PR #148.
- The negative test is red until the per-account lock of #152 is implemented in `identity-and-access-api`; it has not been run locally yet (CP8).

## 4. Plan for next week
- Answer the review on PR #148 and finish the STRIDE (CP6).
- Run `LoginAccountLockE2ETest` locally, paste the output and validate `security.yml` with actionlint (CP8).
- Start #152 in `identity-and-access-api` with a red/green TDD pair.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Workshop folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/13-week/workshop
- STRIDE PRs: https://github.com/code-corhuila/telemed-ia-docs/pull/143, https://github.com/code-corhuila/telemed-ia-docs/pull/148
- Security stories: https://github.com/code-corhuila/telemed-ia-docs/issues/152, https://github.com/code-corhuila/telemed-ia-docs/issues/153, https://github.com/code-corhuila/telemed-ia-docs/issues/154
- Review comment: https://github.com/code-corhuila/telemed-ia-docs/pull/144#pullrequestreview-5699376616
