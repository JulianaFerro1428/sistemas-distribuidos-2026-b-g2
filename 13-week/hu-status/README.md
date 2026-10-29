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
| HU-02 | Per-account login lock (#152): `login_attempts` table and `429` with `Retry-After` in `auth-service` | doing (PRs in review) | [identity-db PR #59](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/59), [identity-api PR #134](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/134) |
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
- #134 depends on #59: the database PR must be merged first. Both need a reviewer other than me.

## 4. Plan for next week
- Get #59 and then #134 reviewed and merged into `develop`.
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
