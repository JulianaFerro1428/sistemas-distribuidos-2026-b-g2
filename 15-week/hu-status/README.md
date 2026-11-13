<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       15-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 15

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai - Group 2
- SPRINT_GOAL: Close MVP 2: build the HU-05 professional onboarding saga in telemed-ia-workflow, run a full-system test from `qa` and fix every defect it found (including the teammates' services), generate the patient's consultation report automatically, promote every repository to `qa`, finish telemed-ia-docs for the final delivery and start the release to `main`.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-05 | Professional onboarding saga: custom Java orchestrator (Anexo E) that creates the identity account, creates the professional profile and activates the account, with compensations in reverse order, RS256 tokens, its own service token, idempotent start, saga state in PostgreSQL, a gateway route and a scheduled recovery sweep | done (merged into develop and promoted to qa) | [workflow #5](https://github.com/code-corhuila/telemed-ia-workflow/pull/5)–[#19](https://github.com/code-corhuila/telemed-ia-workflow/pull/19), [#30](https://github.com/code-corhuila/telemed-ia-workflow/pull/30), [#32](https://github.com/code-corhuila/telemed-ia-workflow/pull/32), [#33](https://github.com/code-corhuila/telemed-ia-workflow/pull/33), [infra-postgres #35](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/35), [gateway #19](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/19), [docs #184](https://github.com/code-corhuila/telemed-ia-docs/pull/184)–[#188](https://github.com/code-corhuila/telemed-ia-docs/pull/188), [#190](https://github.com/code-corhuila/telemed-ia-docs/pull/190), [#191](https://github.com/code-corhuila/telemed-ia-docs/pull/191), [#195](https://github.com/code-corhuila/telemed-ia-docs/pull/195) |
| HU-05 | Professional's own profile: `GET /me` in professional-management and the "Mi perfil" page linked from the shell | done (merged into develop and promoted to qa) | [professional-api #54](https://github.com/code-corhuila/telemed-ia-professional-management-api/pull/54), [professional-portal #18](https://github.com/code-corhuila/telemed-ia-professional-management-portal/pull/18), [front #62](https://github.com/code-corhuila/telemed-ia-front/pull/62), [docs #196](https://github.com/code-corhuila/telemed-ia-docs/pull/196) |
| HU-11 / HU-12 | Medical consultation: the consultation API creates the consultation when the professional starts the attention from a confirmed appointment (`Idempotency-Key`), and the portal starts it and records the attention with real names | done (merged into develop and promoted to qa) | [consultation-api #27](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/27), [consultation-portal #12](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/12), [#13](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/13), [docs #197](https://github.com/code-corhuila/telemed-ia-docs/pull/197) |
| HU-14 | Complete the appointment when its consultation is completed: `ConsultationCompleted` published through the outbox to `consultation.events` and consumed by appointment (queue + DLQ, idempotent) | done (merged into develop and promoted to qa) | [consultation-api #28](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/28), [appointment-api #30](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/30), [consultation-portal #17](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/17), [docs #200](https://github.com/code-corhuila/telemed-ia-docs/pull/200), [#201](https://github.com/code-corhuila/telemed-ia-docs/pull/201) |
| HU-15 | Post-consultation summary: created automatically when the attention is recorded, stored in the appointment, routed by the gateway and shown to the patient on the consultation detail | done (merged into develop and promoted to qa) | [consultation-api #31](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/31), [appointment-api #33](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/33), [gateway #21](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/21), [consultation-portal #16](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/16), [#19](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/19), [docs #202](https://github.com/code-corhuila/telemed-ia-docs/pull/202) |
| HU-16 | Patient consultation PDF: names of patient and professional, date, reason for the visit written by the agent from its own pre-consultation, medication table, referral box, page numbers and disclaimer | done (merged into develop and promoted to qa) | [document-generation #59](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/59), [#60](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/60), [agent-api #59](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/59), [identity-api #160](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/160) |
| HU-06 | Intelligent agent pre-consultation: appointment consumes `PreConsultationSummaryGenerated` and links the summary to its appointment; professionals read the summary of the appointments they attend | done (merged into develop and promoted to qa) | [appointment-api #34](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/34), [agent-api #57](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/57), [docs #203](https://github.com/code-corhuila/telemed-ia-docs/pull/203) |
| HU-09 / HU-10 | Appointment booking and agenda: real patient and professional names (identity batch names lookup), booking with real professionals, reschedule fixed, Spanish dates, portal theme inside the shell | done (merged into develop and promoted to qa) | [identity-api #158](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/158), [appointment-portal #27](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/27)–[#29](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/29), [#32](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/32), [#33](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/33), [front #63](https://github.com/code-corhuila/telemed-ia-front/pull/63), [docs #198](https://github.com/code-corhuila/telemed-ia-docs/pull/198) |
| HU-03 | Password recovery: Mailpit test inbox under the `mail-test` profile, so tests never send real e-mail | done (merged into develop and promoted to qa) | [notifications-api #44](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/44), [docs #199](https://github.com/code-corhuila/telemed-ia-docs/pull/199) |
| HU-04 | Patient profile: profile styled inside the shell | done (merged into develop and promoted to qa) | [patient-portal #11](https://github.com/code-corhuila/telemed-ia-patient-management-portal/pull/11) |
| All | Promotion of every repository from `develop` to `qa` with `git cherry-pick -x` (three rounds, final tree equal to `develop`) | done (merged into qa) | [identity-api #145](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/145)–[#157](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/157), [notifications-api #33](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/33)–[#43](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/43), [agent-api #49](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/49)–[#56](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/56), [workflow #20](https://github.com/code-corhuila/telemed-ia-workflow/pull/20)–[#29](https://github.com/code-corhuila/telemed-ia-workflow/pull/29), [infra-postgres #36](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/36)–[#43](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/43), [front #50](https://github.com/code-corhuila/telemed-ia-front/pull/50)–[#57](https://github.com/code-corhuila/telemed-ia-front/pull/57) (full list in section 7) |
| All | Release `2.0.0` to `main`: pilot with notifications-db (`release/2.0.0` cherry-picked from `qa`, tag `v2.0.0` after the merge) | doing (PR open, waiting for the code owner review) | [notifications-db #10](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10) |
| All | Final documentation delivery: catalogs, gateway, service statuses, workflow, UX/UI, observability, incident playbook, user and admin manuals, project control, DevOps and quality | done (merged into main) | [docs #204](https://github.com/code-corhuila/telemed-ia-docs/pull/204)–[#217](https://github.com/code-corhuila/telemed-ia-docs/pull/217) |

## 2. My individual contribution
- **HU-05 onboarding saga (telemed-ia-workflow, 30 PRs):** following Anexo E, I built a custom Java saga orchestrator (no engine, ADR-028) in three modules (core, adapters, app). It returns the shared error envelope with the correlation id ([#5](https://github.com/code-corhuila/telemed-ia-workflow/pull/5)), verifies the RS256 tokens issued by identity on every route but `/health` ([#6](https://github.com/code-corhuila/telemed-ia-workflow/pull/6), [#7](https://github.com/code-corhuila/telemed-ia-workflow/pull/7)), gets its own service token ([#8](https://github.com/code-corhuila/telemed-ia-workflow/pull/8)), calls identity and professional-management as participants ([#9](https://github.com/code-corhuila/telemed-ia-workflow/pull/9), [#10](https://github.com/code-corhuila/telemed-ia-workflow/pull/10)), exposes `POST /api/v1/sagas/professional-onboarding` and `GET /api/v1/sagas/{id}` to administrators ([#13](https://github.com/code-corhuila/telemed-ia-workflow/pull/13)), keeps the saga state in the `workflow` schema of the shared PostgreSQL with its own Liquibase changelog and `workflow_app` user ([#14](https://github.com/code-corhuila/telemed-ia-workflow/pull/14)–[#16](https://github.com/code-corhuila/telemed-ia-workflow/pull/16), [infra-postgres #35](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/35)), runs as a container behind the gateway ([#17](https://github.com/code-corhuila/telemed-ia-workflow/pull/17), [gateway #19](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/19)), and recovers the sagas that a restart left running with a scheduled sweep ([#32](https://github.com/code-corhuila/telemed-ia-workflow/pull/32), [#33](https://github.com/code-corhuila/telemed-ia-workflow/pull/33)). Every code PR has a separate failing-test commit before the implementation (TDD red/green). Platform run on 2026-11-10 through the gateway: completed saga `201`, replay `200`, key reuse `422`, duplicate license compensated with the account deactivated, no token `401`, professional role `403`.
- **Full-system test from `qa` and fixes:** I rebuilt every service from fresh `qa` clones and ran the API journeys of HU-01 to HU-18 through the gateway plus a browser walk as patient and professional. The first run (2026-11-10) passed 57 checks with 0 failures, but the browser walk found defects that the API checks did not show. With the team's approval I fixed them in every repository, each in its own PR with TDD:
  - the shell returned the account id as a number, which broke every portal that used it with UUIDs ([front #58](https://github.com/code-corhuila/telemed-ia-front/pull/58), [consultation-portal #8](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/8));
  - the gateway did not route the post-summary read ([gateway #21](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/21)); the appointment API redirected trailing slashes to an internal URL ([appointment-api #31](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/31)); the worker used another broker login than every service ([worker #14](https://github.com/code-corhuila/telemed-ia-worker/pull/14));
  - Native Federation does not load a remote's global styles, so the portals lost their theme inside the shell; fixed with a theme stylesheet per component ([appointment-portal #27](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/27), [consultation-portal #11](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/11), [patient-portal #11](https://github.com/code-corhuila/telemed-ia-patient-management-portal/pull/11));
  - the portals showed `Paciente <uuid>`: identity now looks up the names of several accounts for professionals, administrators and the document service ([identity-api #158](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/158), [#160](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/160));
  - no API created consultations and `ConsultationCompleted` (DEC-003) existed only on paper, so appointments stayed `CONFIRMED`; both are now implemented (HU-11, HU-12, HU-14 rows above).
- **Automatic patient report (HU-15, HU-16):** recording the attention now creates the post-summary, completes the appointment and stores the summary id; the document service builds a patient PDF with names, date, medication table and referral box, and the agent writes only the "reason for the visit" from its own pre-consultation, so the AI never rewrites the professional's clinical record (ADR-008). Verified in the browser on 2026-11-11: booking → pre-consultation with the real model → attention → appointment `COMPLETED` → PDF generated automatically.
- **Promotion to `qa` (101 PRs):** all my repositories, the shared ones (infra-postgres, infra-mongo, gateway, front) and the teammates' repositories I fixed, in three rounds. Branches `qa-<desc>` cut from `qa`, `git cherry-pick -x` of the `develop` commits, parts of at most 400 non-test lines, and the last part of each series checked with "tree equal to `develop`". Final end-to-end run from `qa` (2026-11-11): **74 checks passed, 0 failed, 1 not applicable (HU-07)**.
- **Release pilot to `main`:** `release/2.0.0` of notifications-db cherry-picked from `qa` with the scope, deployment and rollback in the PR body ([notifications-db #10](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10)); tag `v2.0.0` goes on `main` after the merge.
- **Documentation (33 PRs in telemed-ia-docs, decisions before code):** ADR-028 completed and accepted and the workflow decisions WF-003 to WF-006, OpenAPI contract and catalogs ([#184](https://github.com/code-corhuila/telemed-ia-docs/pull/184)–[#195](https://github.com/code-corhuila/telemed-ia-docs/pull/195)); the decisions of each fix before its code ([#196](https://github.com/code-corhuila/telemed-ia-docs/pull/196)–[#203](https://github.com/code-corhuila/telemed-ia-docs/pull/203)); and the final delivery of the whole repository: event and service catalogs, stateless NGINX gateway, final service statuses, design system and wireframes, real observability and incident playbook, user and administrator manuals, project control (risks, backlog, open questions), DevOps and quality ([#204](https://github.com/code-corhuila/telemed-ia-docs/pull/204)–[#217](https://github.com/code-corhuila/telemed-ia-docs/pull/217)).

## 3. Blockers and risks
- GitHub Actions still does not start on organization PRs ("The job was not started because your account is locked due to a billing issue"); the release PR [notifications-db #10](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10) shows its only check as failed for that reason. Every PR of the week was verified with local runs (`mvn verify`, Testcontainers, pytest, `go test`, image builds and the end-to-end runs above), cited in each PR body.
- The release to `main` needs the approval of a code owner (`main` protection: one approval, code owner required). Until it merges, `main` of the other repositories still holds the seed.
- The free tier of the language model answered `429` in one end-to-end run, so the agent answered `503`; a rerun passed. Risk during the final demonstration.
- Docker Engine 29 rejects the default Docker API version of Testcontainers; the workflow tests pin `api.version=1.44` (identity-api end-to-end tests may hit the same problem).
- Open contradictions between sources (for example the coverage floor) are recorded as open questions Q-009 to Q-016 in project control instead of being decided alone ([docs #215](https://github.com/code-corhuila/telemed-ia-docs/pull/215), [#217](https://github.com/code-corhuila/telemed-ia-docs/pull/217)).

## 4. Plan for next week
- Merge [notifications-db #10](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10), tag `v2.0.0` on `main`, and repeat `release/2.0.0` → `main` + tag for the rest of my repositories and the shared ones, one PR per repository.
- Run the full-system end-to-end test again from `main` and attach the results to the HU issues.
- Prepare the final presentation and defense of the MVP with the evidence of each story.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Week folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/15-week
- All my pull requests of the week: https://github.com/pulls?q=is%3Apr+author%3AJulianaFerro1428+org%3Acode-corhuila+created%3A2026-11-09..2026-11-13
- HU-05 saga recovery sweep: https://github.com/code-corhuila/telemed-ia-workflow/pull/33
- HU-05 saga on the platform: https://github.com/code-corhuila/telemed-ia-docs/pull/188
- HU-14 `ConsultationCompleted`: https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/28, https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/30
- HU-15 / HU-16 patient report: https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/31, https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/60
- Promotion to qa (last part of the identity series): https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/157
- Release pilot to main: https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10
- Documentation: https://github.com/code-corhuila/telemed-ia-docs/pulls?q=is%3Apr+author%3AJulianaFerro1428+created%3A2026-11-09..2026-11-13

## 7. Pull requests in my microservices
The 196 pull requests I opened from 2026-11-09 to 2026-11-13 (195 merged, 1 open) in the repositories I own
(identity, intelligent agent, notifications and workflow), the teammates' services I fixed, the shared repositories
and the documentation, newest first, with the target branch and the creation date.

### Identity & Access

**`telemed-ia-identity-and-access-api`** (17 PRs)

- [#161](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/161) → `qa` · 2026-11-11 · chore(qa): promote the internal account names lookup (HU-16)
- [#160](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/160) → `develop` · 2026-11-11 · feat(identity): add GET /internal/account-names for service clients (HU-16)
- [#159](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/159) → `qa` · 2026-11-11 · chore(qa): promote the account names lookup (HU-10)
- [#158](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/158) → `develop` · 2026-11-11 · feat(identity): look up the names of several accounts (HU-10)
- [#157](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/157) → `qa` · 2026-11-10 · feat(identity-api): promote the idempotent registration and the saga account endpoints to qa (13/13)
- [#156](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/156) → `qa` · 2026-11-10 · feat(identity-api): promote the span export and the shared registration retry to qa (12/13)
- [#155](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/155) → `qa` · 2026-11-10 · feat(identity-api): promote the domain event logs, metrics and fast 503 to qa (11/13)
- [#154](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/154) → `qa` · 2026-11-10 · feat(identity-api): promote the trace-aware logs and outbox trace to qa (10/13)
- [#153](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/153) → `qa` · 2026-11-10 · feat(identity-api): promote the per-account login lock to qa (9/13)
- [#152](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/152) → `qa` · 2026-11-10 · feat(identity-api): promote the notification recipient lookup to qa (8/13)
- [#151](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/151) → `qa` · 2026-11-10 · feat(identity-api): promote the RabbitMQ outbox relay to qa (7/13)
- [#150](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/150) → `qa` · 2026-11-10 · feat(identity-api): promote the professional names and the outbox relay core to qa (6/13)
- [#149](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/149) → `qa` · 2026-11-10 · feat(identity-api): promote the account status administration and the 4xx answers to qa (5/13)
- [#148](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/148) → `qa` · 2026-11-10 · feat(identity-api): promote the shared database defaults and the account list to qa (4/13)
- [#147](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/147) → `qa` · 2026-11-10 · feat(identity-api): promote the internal professional accounts to qa (3/13)
- [#146](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/146) → `qa` · 2026-11-10 · feat(identity-api): promote the bootstrap administrator to qa (2/13)
- [#145](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/145) → `qa` · 2026-11-10 · feat(identity-api): promote the platform network, name claim and service tokens to qa (1/13)

**`telemed-ia-identity-and-access-db`** (1 PR)

- [#62](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/pull/62) → `qa` · 2026-11-09 · chore(db): promote the login lock and the outbox traceparent to qa

**`telemed-ia-identity-and-access-portal`** (4 PRs)

- [#29](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/29) → `qa` · 2026-11-09 · chore(identity-portal): promote the login lock message and governance to qa (part 4 of 4)
- [#28](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/28) → `qa` · 2026-11-09 · chore(identity-portal): promote blocking and restoring an account to qa (part 3 of 4)
- [#27](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/27) → `qa` · 2026-11-09 · chore(identity-portal): promote the account list to qa (part 2 of 4)
- [#26](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/26) → `qa` · 2026-11-09 · chore(identity-portal): promote the account administration data service to qa (part 1 of 4)

### Intelligent agent

**`telemed-ia-intelligent-agent-api`** (12 PRs)

- [#60](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/60) → `qa` · 2026-11-11 · chore(qa): promote the reason for the visit for the patient PDF (HU-16)
- [#59](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/59) → `develop` · 2026-11-11 · feat(agent): write the reason for the visit for the patient's PDF (HU-16)
- [#58](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/58) → `qa` · 2026-11-10 · docs(agent-api): promote the professional summary access note to qa (HU-06)
- [#57](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/57) → `develop` · 2026-11-10 · docs(agent): say that professionals read the summary of the appointments they attend
- [#56](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/56) → `qa` · 2026-11-09 · chore(agent-api): promote the scheduled outbox relay to qa (part 8 of 8)
- [#55](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/55) → `qa` · 2026-11-09 · chore(agent-api): promote the outbox publisher to qa (part 7 of 8)
- [#54](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/54) → `qa` · 2026-11-09 · chore(agent-api): promote the outbox relay core to qa (part 6 of 8)
- [#53](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/53) → `qa` · 2026-11-09 · chore(agent-api): promote the child spans to qa (part 5 of 8)
- [#52](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/52) → `qa` · 2026-11-09 · chore(agent-api): promote the SLO documentation and the span export to qa (part 4 of 8)
- [#51](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/51) → `qa` · 2026-11-09 · chore(agent-api): promote the observability documentation to qa (part 3 of 8)
- [#50](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/50) → `qa` · 2026-11-09 · chore(agent-api): promote the circuit breaker and the agent-db fail-fast to qa (part 2 of 8)
- [#49](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/49) → `qa` · 2026-11-09 · chore(agent-api): promote trace, logs, SLO and pause to qa (part 1 of 8)

**`telemed-ia-intelligent-agent-db`** (1 PR)

- [#23](https://github.com/code-corhuila/telemed-ia-intelligent-agent-db/pull/23) → `qa` · 2026-11-09 · feat(agent-db): promote the outbox trace context to qa

**`telemed-ia-intelligent-agent-portal`** (1 PR)

- [#48](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/48) → `qa` · 2026-11-09 · chore(agent-portal): promote the pause notice and the published port to qa

### Notifications

**`telemed-ia-notifications-api`** (13 PRs)

- [#45](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/45) → `qa` · 2026-11-11 · chore(qa): promote the Mailpit mail-test profile (HU-03)
- [#44](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/44) → `develop` · 2026-11-11 · build(notifications): add a Mailpit test inbox under the mail-test profile (HU-03)
- [#43](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/43) → `qa` · 2026-11-09 · chore(notifications-api): promote the week 11 metrics, spans and fail-fast to qa (part 11 of 11)
- [#42](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/42) → `qa` · 2026-11-09 · chore(notifications-api): promote the trace-aware JSON logs to qa (part 10 of 11)
- [#41](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/41) → `qa` · 2026-11-09 · chore(notifications-api): promote the appointment events and reminders to qa (part 9 of 11)
- [#40](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/40) → `qa` · 2026-11-09 · chore(notifications-api): promote the appointment notices and SMTP e-mails to qa (part 8 of 11)
- [#39](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/39) → `qa` · 2026-11-09 · chore(notifications-api): promote the recipient lookup to qa (part 7 of 11)
- [#38](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/38) → `qa` · 2026-11-09 · chore(notifications-api): promote the consultation PDF inbox notice to qa (part 6 of 11)
- [#37](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/37) → `qa` · 2026-11-09 · chore(notifications-api): promote the inbox HTTP endpoints to qa (part 5 of 11)
- [#36](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/36) → `qa` · 2026-11-09 · chore(notifications-api): promote the inbox persistence to qa (part 4 of 11)
- [#35](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/35) → `qa` · 2026-11-09 · chore(notifications-api): promote the inbox use cases to qa (part 3 of 11)
- [#34](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/34) → `qa` · 2026-11-09 · chore(notifications-api): promote the RS256 token check to qa (part 2 of 11)
- [#33](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/33) → `qa` · 2026-11-09 · chore(notifications-api): promote the password recovery consumer to qa (part 1 of 11)

**`telemed-ia-notifications-db`** (2 PRs)

- [#10](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/10) → `main` · 2026-11-13 · **open** · chore(release): publish notifications database 2.0.0
- [#9](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/9) → `qa` · 2026-11-09 · feat(db): promote the notification inbox to qa

**`telemed-ia-notifications-portal`** (5 PRs)

- [#15](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/15) → `qa` · 2026-11-09 · chore(notifications-portal): promote the inbox states, CI and governance to qa (part 5 of 5)
- [#14](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/14) → `qa` · 2026-11-09 · chore(notifications-portal): promote the inbox page to qa (part 4 of 5)
- [#13](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/13) → `qa` · 2026-11-09 · chore(notifications-portal): promote the inbox texts and state to qa (part 3 of 5)
- [#12](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/12) → `qa` · 2026-11-09 · chore(notifications-portal): promote the inbox data layer to qa (part 2 of 5)
- [#11](https://github.com/code-corhuila/telemed-ia-notifications-portal/pull/11) → `qa` · 2026-11-09 · chore(notifications-portal): promote the federated build to qa (part 1 of 5)

### Workflow (HU-05 saga)

**`telemed-ia-workflow`** (30 PRs)

- [#34](https://github.com/code-corhuila/telemed-ia-workflow/pull/34) → `qa` · 2026-11-10 · feat(workflow): promote the saga recovery sweep to qa (HU-05)
- [#33](https://github.com/code-corhuila/telemed-ia-workflow/pull/33) → `develop` · 2026-11-10 · feat(workflow): run the saga recovery sweep on a schedule (HU-05)
- [#32](https://github.com/code-corhuila/telemed-ia-workflow/pull/32) → `develop` · 2026-11-10 · feat(workflow): undo or complete the sagas a restart left running (HU-05)
- [#31](https://github.com/code-corhuila/telemed-ia-workflow/pull/31) → `qa` · 2026-11-10 · fix(workflow): promote the Idempotency-Key length check to qa (HU-05)
- [#30](https://github.com/code-corhuila/telemed-ia-workflow/pull/30) → `develop` · 2026-11-10 · fix(workflow): accept an Idempotency-Key from 8 to 128 characters
- [#29](https://github.com/code-corhuila/telemed-ia-workflow/pull/29) → `qa` · 2026-11-10 · fix(workflow): promote the schema CI and the saga time precision to qa (HU-05, 10/10)
- [#28](https://github.com/code-corhuila/telemed-ia-workflow/pull/28) → `qa` · 2026-11-10 · feat(workflow): promote the PostgreSQL saga store and the deploy to qa (HU-05, 9/10)
- [#27](https://github.com/code-corhuila/telemed-ia-workflow/pull/27) → `qa` · 2026-11-10 · feat(workflow): promote the saga schema and the saga snapshot to qa (HU-05, 8/10)
- [#26](https://github.com/code-corhuila/telemed-ia-workflow/pull/26) → `qa` · 2026-11-10 · feat(workflow): promote the saga wiring, CI and saga routes to qa (HU-05, 7/10)
- [#25](https://github.com/code-corhuila/telemed-ia-workflow/pull/25) → `qa` · 2026-11-10 · feat(workflow): promote the identity and professional participants to qa (HU-05, 6/10)
- [#24](https://github.com/code-corhuila/telemed-ia-workflow/pull/24) → `qa` · 2026-11-10 · feat(workflow): promote the auth filter and the service token to qa (HU-05, 5/10)
- [#23](https://github.com/code-corhuila/telemed-ia-workflow/pull/23) → `qa` · 2026-11-10 · feat(workflow): promote the error envelope and RS256 verification to qa (HU-05, 4/10)
- [#22](https://github.com/code-corhuila/telemed-ia-workflow/pull/22) → `qa` · 2026-11-10 · feat(workflow): promote the onboarding saga and its idempotent start to qa (HU-05, 3/10)
- [#21](https://github.com/code-corhuila/telemed-ia-workflow/pull/21) → `qa` · 2026-11-10 · feat(workflow): promote the saga instance model to qa (HU-05, 2/10)
- [#20](https://github.com/code-corhuila/telemed-ia-workflow/pull/20) → `qa` · 2026-11-10 · chore(workflow): promote the project scaffold to qa (HU-05, 1/10)
- [#19](https://github.com/code-corhuila/telemed-ia-workflow/pull/19) → `develop` · 2026-11-10 · fix(workflow): keep the saga times to the microsecond
- [#18](https://github.com/code-corhuila/telemed-ia-workflow/pull/18) → `develop` · 2026-11-09 · ci: run the workflow schema tests on every pull request (HU-05)
- [#17](https://github.com/code-corhuila/telemed-ia-workflow/pull/17) → `develop` · 2026-11-09 · chore(workflow): add the container image and the platform compose (HU-05)
- [#16](https://github.com/code-corhuila/telemed-ia-workflow/pull/16) → `develop` · 2026-11-09 · feat(workflow): keep the saga state in PostgreSQL (HU-05)
- [#15](https://github.com/code-corhuila/telemed-ia-workflow/pull/15) → `develop` · 2026-11-09 · feat(workflow): restore a saga from the snapshot a store keeps (HU-05)
- [#14](https://github.com/code-corhuila/telemed-ia-workflow/pull/14) → `develop` · 2026-11-09 · feat(workflow): add the workflow schema migrations for the saga store (HU-05)
- [#13](https://github.com/code-corhuila/telemed-ia-workflow/pull/13) → `develop` · 2026-11-09 · feat(workflow): expose the onboarding saga to administrators (HU-05)
- [#12](https://github.com/code-corhuila/telemed-ia-workflow/pull/12) → `develop` · 2026-11-09 · feat(workflow): wire the onboarding saga with a temporary in-memory store (HU-05)
- [#11](https://github.com/code-corhuila/telemed-ia-workflow/pull/11) → `develop` · 2026-11-09 · ci: add build-and-test workflow (HU-05)
- [#10](https://github.com/code-corhuila/telemed-ia-workflow/pull/10) → `develop` · 2026-11-09 · feat(workflow): call professional-management as the saga participant
- [#9](https://github.com/code-corhuila/telemed-ia-workflow/pull/9) → `develop` · 2026-11-09 · feat(workflow): call identity as the onboarding saga participant
- [#8](https://github.com/code-corhuila/telemed-ia-workflow/pull/8) → `develop` · 2026-11-09 · feat(workflow): get the workflow's own service token from identity
- [#7](https://github.com/code-corhuila/telemed-ia-workflow/pull/7) → `develop` · 2026-11-09 · feat(workflow): require a valid RS256 token on every route but /health
- [#6](https://github.com/code-corhuila/telemed-ia-workflow/pull/6) → `develop` · 2026-11-09 · feat(workflow): verify RS256 access tokens issued by identity
- [#5](https://github.com/code-corhuila/telemed-ia-workflow/pull/5) → `develop` · 2026-11-09 · feat(workflow): return the correlation id and the shared error envelope

### Teammates' services (fixes from the full-system test)

**`telemed-ia-appointment-scheduling-api`** (7 PRs)

- [#36](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/36) → `qa` · 2026-11-11 · chore(qa): promote linking the pre-consultation summary (HU-06) [2/2]
- [#35](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/35) → `qa` · 2026-11-11 · chore(qa): promote storing the post-summary id (HU-15) [1/2]
- [#34](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/34) → `develop` · 2026-11-11 · feat(appointment): link the pre-consultation summary to its appointment (HU-06)
- [#33](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/33) → `develop` · 2026-11-11 · feat(appointment): store the post-summary id from ConsultationCompleted (HU-15)
- [#32](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/32) → `qa` · 2026-11-11 · chore(qa): promote the ConsultationCompleted consumer and the slash fix (HU-14)
- [#31](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/31) → `develop` · 2026-11-11 · fix(appointment): answer trailing-slash variants without redirecting
- [#30](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/pull/30) → `develop` · 2026-11-11 · feat(appointment): complete the appointment when its consultation is completed (HU-14)

**`telemed-ia-appointment-scheduling-portal`** (8 PRs)

- [#34](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/34) → `qa` · 2026-11-11 · chore(qa): promote the reschedule fixes (HU-09)
- [#33](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/33) → `develop` · 2026-11-11 · fix(appointment-portal): hide the reschedule action on cancelled appointments (HU-09)
- [#32](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/32) → `develop` · 2026-11-11 · fix(appointment): load the appointment to reschedule by its id (HU-09)
- [#31](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/31) → `qa` · 2026-11-11 · chore(qa): promote real names and real professionals (HU-10) [2/2]
- [#30](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/30) → `qa` · 2026-11-11 · chore(qa): promote the portal theme and Spanish dates (HU-09) [1/2]
- [#29](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/29) → `develop` · 2026-11-11 · feat(portal): show real names and book real professionals (HU-10)
- [#28](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/28) → `develop` · 2026-11-11 · fix(portal): show appointment dates in Spanish (HU-09)
- [#27](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-portal/pull/27) → `develop` · 2026-11-11 · fix(portal): keep the portal theme when the shell mounts it (HU-09)

**`telemed-ia-medical-consultation-api`** (6 PRs)

- [#32](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/32) → `qa` · 2026-11-11 · chore(qa): promote the automatic post-summary (HU-15)
- [#31](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/31) → `develop` · 2026-11-11 · feat(consultation): create the post-summary when the attention is recorded (HU-15)
- [#30](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/30) → `qa` · 2026-11-11 · chore(qa): promote the ConsultationCompleted event (HU-14) [2/2]
- [#29](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/29) → `qa` · 2026-11-11 · chore(qa): promote starting a consultation from an appointment (HU-11) [1/2]
- [#28](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/28) → `develop` · 2026-11-11 · feat(consultation): publish ConsultationCompleted when the attention is recorded (HU-14)
- [#27](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/pull/27) → `develop` · 2026-11-11 · feat(consultation): start a consultation from an appointment (HU-11)

**`telemed-ia-medical-consultation-portal`** (13 PRs)

- [#20](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/20) → `qa` · 2026-11-11 · chore(qa): promote the patient attention detail, Spanish dates and pre-consultation access (HU-15)
- [#19](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/19) → `develop` · 2026-11-11 · fix(consultation-portal): read the pre-consultation the way the agent allows (HU-15)
- [#18](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/18) → `develop` · 2026-11-11 · fix(consultation-portal): show consultation dates in Spanish (HU-15)
- [#17](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/17) → `develop` · 2026-11-11 · fix(consultation-portal): stop asking to mark the appointment as attended (HU-14)
- [#16](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/16) → `develop` · 2026-11-11 · fix(consultation-portal): show the attention on the patient's consultation detail (HU-15)
- [#15](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/15) → `qa` · 2026-11-11 · chore(qa): promote the consultation portal fixes and attention flow (HU-12)
- [#14](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/14) → `develop` · 2026-11-11 · fix(portal): show a missing pre-consultation as an empty state
- [#13](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/13) → `develop` · 2026-11-11 · feat(portal): start the consultation and record the attention (HU-12)
- [#12](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/12) → `develop` · 2026-11-11 · feat(portal): show real patient and professional names (HU-11)
- [#11](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/11) → `develop` · 2026-11-11 · fix(portal): keep the portal theme when the shell mounts it (HU-15)
- [#10](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/10) → `develop` · 2026-11-11 · fix(consultation-portal): read post-summaries from the contract path
- [#9](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/9) → `develop` · 2026-11-10 · fix(consultation-portal): probe the healthcheck over IPv4
- [#8](https://github.com/code-corhuila/telemed-ia-medical-consultation-portal/pull/8) → `develop` · 2026-11-10 · fix(consultation-portal): use the account UUID from the shell session

**`telemed-ia-document-generation-api`** (4 PRs)

- [#62](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/62) → `qa` · 2026-11-11 · chore(qa): promote the patient PDF layout (HU-16) [2/2]
- [#61](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/61) → `qa` · 2026-11-11 · chore(qa): promote the patient PDF data (HU-16) [1/2]
- [#60](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/60) → `develop` · 2026-11-11 · feat(document-generation): lay out the consultation PDF for the patient (HU-16)
- [#59](https://github.com/code-corhuila/telemed-ia-document-generation-api/pull/59) → `develop` · 2026-11-11 · feat(document-generation): read the names and the reason for the patient's PDF (HU-16)

**`telemed-ia-professional-management-api`** (2 PRs)

- [#55](https://github.com/code-corhuila/telemed-ia-professional-management-api/pull/55) → `qa` · 2026-11-11 · chore(qa): promote the professional's own profile at /me (HU-05)
- [#54](https://github.com/code-corhuila/telemed-ia-professional-management-api/pull/54) → `develop` · 2026-11-11 · feat(professional-api): let a professional read its own profile at /me

**`telemed-ia-professional-management-portal`** (2 PRs)

- [#19](https://github.com/code-corhuila/telemed-ia-professional-management-portal/pull/19) → `qa` · 2026-11-11 · chore(qa): promote the professional's own profile page (HU-05)
- [#18](https://github.com/code-corhuila/telemed-ia-professional-management-portal/pull/18) → `develop` · 2026-11-11 · feat(professional-portal): show the professional's own profile at /professionals/me

**`telemed-ia-patient-management-portal`** (2 PRs)

- [#12](https://github.com/code-corhuila/telemed-ia-patient-management-portal/pull/12) → `qa` · 2026-11-11 · chore(qa): promote the patient profile styles (HU-04)
- [#11](https://github.com/code-corhuila/telemed-ia-patient-management-portal/pull/11) → `develop` · 2026-11-11 · fix(portal): style the patient profile inside the shell (HU-04)

**`telemed-ia-worker`** (4 PRs)

- [#17](https://github.com/code-corhuila/telemed-ia-worker/pull/17) → `qa` · 2026-11-11 · chore(qa): promote real reminders, DLQ replay and broker defaults [3/3]
- [#16](https://github.com/code-corhuila/telemed-ia-worker/pull/16) → `qa` · 2026-11-11 · chore(qa): promote the worker DLQ, timeouts and reminder scheduler [2/3]
- [#15](https://github.com/code-corhuila/telemed-ia-worker/pull/15) → `qa` · 2026-11-11 · chore(qa): promote the worker and its RabbitMQ consumer [1/3]
- [#14](https://github.com/code-corhuila/telemed-ia-worker/pull/14) → `develop` · 2026-11-11 · fix(worker): default the broker login to the one every service uses

### Shared repositories

**`telemed-ia-infra-postgres`** (9 PRs)

- [#43](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/43) → `qa` · 2026-11-10 · feat(infra-postgres): promote the notifications SLO alerts and the workflow schema to qa (8/8)
- [#42](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/42) → `qa` · 2026-11-10 · feat(infra-postgres): promote the agent and identity SLO alerts to qa (7/8)
- [#41](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/41) → `qa` · 2026-11-10 · feat(infra-postgres): promote the professional management SLO alerts and dashboard to qa (6/8)
- [#40](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/40) → `qa` · 2026-11-10 · feat(infra-postgres): promote the professional management metrics and postgres-exporter to qa (5/8)
- [#39](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/39) → `qa` · 2026-11-10 · feat(infra-postgres): promote the document generation dashboard to qa (4/8)
- [#38](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/38) → `qa` · 2026-11-10 · feat(infra-postgres): promote the Alertmanager routing to qa (3/8)
- [#37](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/37) → `qa` · 2026-11-10 · feat(infra-postgres): promote the document generation SLOs and Jaeger to qa (2/8)
- [#36](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/36) → `qa` · 2026-11-10 · feat(infra-postgres): promote the domain roles, object storage and consultation grants to qa (1/8)
- [#35](https://github.com/code-corhuila/telemed-ia-infra-postgres/pull/35) → `develop` · 2026-11-09 · feat(postgres): add the workflow schema and roles for the saga store (HU-05)

**`telemed-ia-infra-mongo`** (1 PR)

- [#16](https://github.com/code-corhuila/telemed-ia-infra-mongo/pull/16) → `qa` · 2026-11-10 · fix(infra-mongo): promote the agent-db changeset count to qa

**`telemed-ia-api-gateway`** (4 PRs)

- [#22](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/22) → `qa` · 2026-11-11 · chore(qa): promote the post-summary route (HU-15)
- [#21](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/21) → `develop` · 2026-11-11 · fix(gateway): route the post-summary read to medical-consultation
- [#20](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/20) → `qa` · 2026-11-10 · feat(gateway): promote the service routes to qa
- [#19](https://github.com/code-corhuila/telemed-ia-api-gateway/pull/19) → `develop` · 2026-11-09 · feat(gateway): add workflow route (HU-05)

**`telemed-ia-front`** (15 PRs)

- [#64](https://github.com/code-corhuila/telemed-ia-front/pull/64) → `qa` · 2026-11-11 · chore(qa): promote the shell session, greeting and navigation fixes
- [#63](https://github.com/code-corhuila/telemed-ia-front/pull/63) → `develop` · 2026-11-11 · fix(shell): mark only Mi agenda on the professional schedule (HU-10)
- [#62](https://github.com/code-corhuila/telemed-ia-front/pull/62) → `develop` · 2026-11-11 · feat(shell): link Mi perfil of a professional to its own profile
- [#61](https://github.com/code-corhuila/telemed-ia-front/pull/61) → `develop` · 2026-11-11 · revert(shell): drop the es-CO locale provider that blanks the shell
- [#60](https://github.com/code-corhuila/telemed-ia-front/pull/60) → `develop` · 2026-11-11 · fix(shell): format dates in Colombian Spanish for the shell and its remotes
- [#59](https://github.com/code-corhuila/telemed-ia-front/pull/59) → `develop` · 2026-11-11 · fix(shell): greet by first name, skipping a leading title
- [#58](https://github.com/code-corhuila/telemed-ia-front/pull/58) → `develop` · 2026-11-10 · fix(shell): return the account UUID from getUserId
- [#57](https://github.com/code-corhuila/telemed-ia-front/pull/57) → `qa` · 2026-11-10 · style(shell): promote the home appointment styles to qa (8/8)
- [#56](https://github.com/code-corhuila/telemed-ia-front/pull/56) → `qa` · 2026-11-10 · feat(shell): promote the home upcoming appointments to qa (7/8)
- [#55](https://github.com/code-corhuila/telemed-ia-front/pull/55) → `qa` · 2026-11-10 · feat(shell): promote the portal bridge, notifications mount and image fix to qa (6/8)
- [#54](https://github.com/code-corhuila/telemed-ia-front/pull/54) → `qa` · 2026-11-10 · feat(shell): promote the administrator home to qa (5/8)
- [#53](https://github.com/code-corhuila/telemed-ia-front/pull/53) → `qa` · 2026-11-10 · feat(shell): promote the professional remote and account administration to qa (4/8)
- [#52](https://github.com/code-corhuila/telemed-ia-front/pull/52) → `qa` · 2026-11-10 · feat(shell): promote the landing benefits, footer and token revocation to qa (3/8)
- [#51](https://github.com/code-corhuila/telemed-ia-front/pull/51) → `qa` · 2026-11-10 · feat(shell): promote the landing how-it-works section to qa (2/8)
- [#50](https://github.com/code-corhuila/telemed-ia-front/pull/50) → `qa` · 2026-11-10 · feat(shell): promote the identity remote and the landing page to qa (1/8)

### Documentation

**`telemed-ia-docs`** (33 PRs)

- [#217](https://github.com/code-corhuila/telemed-ia-docs/pull/217) → `main` · 2026-11-12 · docs(quality): record the test state per repository and the open coverage question
- [#216](https://github.com/code-corhuila/telemed-ia-docs/pull/216) → `main` · 2026-11-12 · docs(devops): update branch state, CI commands and startup issues to 2026-11-12
- [#215](https://github.com/code-corhuila/telemed-ia-docs/pull/215) → `main` · 2026-11-12 · docs(project-control): bring dependencies, risks, backlog and open questions to the final state
- [#214](https://github.com/code-corhuila/telemed-ia-docs/pull/214) → `main` · 2026-11-12 · docs(training): add the administrator manual
- [#213](https://github.com/code-corhuila/telemed-ia-docs/pull/213) → `main` · 2026-11-12 · docs(training): add the user manual for patients and professionals
- [#212](https://github.com/code-corhuila/telemed-ia-docs/pull/212) → `main` · 2026-11-12 · docs(operations): adapt the incident playbook to the team, the tools and the incidents of the project
- [#211](https://github.com/code-corhuila/telemed-ia-docs/pull/211) → `main` · 2026-11-12 · docs(operations): describe the real logs, metrics, traces, dashboards and alerts of the platform
- [#210](https://github.com/code-corhuila/telemed-ia-docs/pull/210) → `main` · 2026-11-12 · docs(ux-ui): add low-fidelity wireframes of the main screens
- [#209](https://github.com/code-corhuila/telemed-ia-docs/pull/209) → `main` · 2026-11-12 · docs(ux-ui): replace the design system template with the real tokens and add the professional profile route
- [#208](https://github.com/code-corhuila/telemed-ia-docs/pull/208) → `main` · 2026-11-12 · docs(workflow): complete the runbook and state that the workflow uses no events; date the worker qa state
- [#207](https://github.com/code-corhuila/telemed-ia-docs/pull/207) → `main` · 2026-11-12 · docs(document): add the endpoints, scope limits and local run of the document service readme
- [#206](https://github.com/code-corhuila/telemed-ia-docs/pull/206) → `main` · 2026-11-12 · docs(microservices): set the final status of auth, appointment and consultation and fix the incident guide links
- [#205](https://github.com/code-corhuila/telemed-ia-docs/pull/205) → `main` · 2026-11-12 · docs(gateway): describe the stateless nginx configuration and the json access log instead of redis
- [#204](https://github.com/code-corhuila/telemed-ia-docs/pull/204) → `main` · 2026-11-12 · docs(microservices): record the published events and the final service states in the shared catalogs
- [#203](https://github.com/code-corhuila/telemed-ia-docs/pull/203) → `main` · 2026-11-11 · docs(appointment): link the pre-consultation summary to its appointment (HU-06)
- [#202](https://github.com/code-corhuila/telemed-ia-docs/pull/202) → `main` · 2026-11-11 · docs(services): decide the automatic summary and the patient's consultation PDF (HU-15)
- [#201](https://github.com/code-corhuila/telemed-ia-docs/pull/201) → `main` · 2026-11-11 · docs(project-control): record the risk of an appointment left CONFIRMED (HU-14)
- [#200](https://github.com/code-corhuila/telemed-ia-docs/pull/200) → `main` · 2026-11-11 · docs(consultation): publish ConsultationCompleted and complete the appointment (HU-14)
- [#199](https://github.com/code-corhuila/telemed-ia-docs/pull/199) → `main` · 2026-11-11 · docs(notifications): keep Mailpit under a test profile (DEC-018)
- [#198](https://github.com/code-corhuila/telemed-ia-docs/pull/198) → `main` · 2026-11-11 · docs(identity): look up the names of several accounts (DEC-013)
- [#197](https://github.com/code-corhuila/telemed-ia-docs/pull/197) → `main` · 2026-11-11 · docs(consultation): create the consultation when the attention starts (DEC-006)
- [#196](https://github.com/code-corhuila/telemed-ia-docs/pull/196) → `main` · 2026-11-11 · docs(professional): let a professional read its own profile (DEC-016)
- [#195](https://github.com/code-corhuila/telemed-ia-docs/pull/195) → `main` · 2026-11-10 · docs(workflow): sweep the interrupted sagas instead of compensating them by hand (HU-05)
- [#194](https://github.com/code-corhuila/telemed-ia-docs/pull/194) → `main` · 2026-11-10 · docs(project-control): close the saga store items and record the workflow change log
- [#193](https://github.com/code-corhuila/telemed-ia-docs/pull/193) → `main` · 2026-11-10 · docs(requirements): trace the identity and notifications requirements to their tests
- [#192](https://github.com/code-corhuila/telemed-ia-docs/pull/192) → `main` · 2026-11-10 · docs(devops): update the CI state per repository and the qa promotion method
- [#191](https://github.com/code-corhuila/telemed-ia-docs/pull/191) → `main` · 2026-11-10 · docs(workflow): add the saga store and the gateway routes to the shared catalogs
- [#190](https://github.com/code-corhuila/telemed-ia-docs/pull/190) → `main` · 2026-11-10 · docs(workflow): add the OpenAPI contract of the saga routes
- [#188](https://github.com/code-corhuila/telemed-ia-docs/pull/188) → `main` · 2026-11-10 · docs(workflow): record the onboarding saga platform run
- [#187](https://github.com/code-corhuila/telemed-ia-docs/pull/187) → `main` · 2026-11-09 · docs(workflow): record the PostgreSQL saga store, the container and the gateway route (HU-05)
- [#186](https://github.com/code-corhuila/telemed-ia-docs/pull/186) → `main` · 2026-11-09 · docs(architecture): accept ADR-028 (HU-05)
- [#185](https://github.com/code-corhuila/telemed-ia-docs/pull/185) → `main` · 2026-11-09 · docs(auth): record the internal account status for the onboarding saga (HU-05)
- [#184](https://github.com/code-corhuila/telemed-ia-docs/pull/184) → `main` · 2026-11-09 · docs(workflow): complete ADR-028 and record the workflow decisions (HU-05)
