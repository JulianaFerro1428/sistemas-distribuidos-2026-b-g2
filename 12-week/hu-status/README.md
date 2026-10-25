<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       12-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 12

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai - Group 2
- SPRINT_GOAL: Deliver and promote TeleMed IA registration, authentication, password recovery, account administration, and AI pre-consultation workflows across API, portal, notification, database and shell repositories, with tests, observability and QA promotion.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-01 | Patient registration: portal registration flow, password policy, validation and QA promotion | done | [Identity and Access Portal #10](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/10), [#11](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/11), [#12](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/12), [QA #19](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/19) |
| HU-02 | Authentication and access: sign-in, shell integration, refresh token, service tokens, internal network isolation and QA portal promotion | done | [Identity and Access Portal #4](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/4), [#7](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/7), [#8](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/8), [#18](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/18), [Identity and Access API #116](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/116), [#117](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/117), [#118](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/118), [TeleMed IA Front #29](https://github.com/code-corhuila/telemed-ia-front/pull/29), [#32](https://github.com/code-corhuila/telemed-ia-front/pull/32), [#37](https://github.com/code-corhuila/telemed-ia-front/pull/37), [Portal QA #19](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/19) |
| HU-03 | Password recovery: transactional outbox, password-reset rules, portal pages, SMTP email delivery, notification persistence and QA promotion | done | [Identity and Access API #111](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/111), [#112](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/112), [#113](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/113), [QA #120](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/120), [Identity and Access Portal #13](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/13), [#14](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/14), [#15](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/15), [Notifications API #2](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/2), [#3](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/3), [#4](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/4), [QA #5](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/5), [Notifications DB #3](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/3), [#4](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/4), [#5](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/5), [QA #6](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/6) |
| HU-05 | Account administration: administrator bootstrap, professional account provisioning, user account listing and blocking/restoring accounts across API, portal and shell | done | [Identity and Access API #121](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/121), [#122](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/122), [#124](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/124), [#125](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/125), [#126](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/126), [#128](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/128), [Identity and Access Portal #20](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/20), [#21](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/21), [#22](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/22), [TeleMed IA Front #39](https://github.com/code-corhuila/telemed-ia-front/pull/39), [#40](https://github.com/code-corhuila/telemed-ia-front/pull/40) |
| HU-06 | Intelligent Agent pre-consultation: secure conversations, AI provider, clinical guards, appointment validation, persistence, idempotency, summaries, portal integration, logs, metrics and QA promotion | done | [Intelligent Agent API #5](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/5), [#7](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/7), [#10](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/10), [#11](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/11), [#14](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/14), [#15](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/15), [#20](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/20), [#25](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/25), [#26](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/26), [#28](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/28), [#29](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/29), [QA #30](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/30), [Intelligent Agent Portal #40](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/40), [#41](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/41), [#42](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/42), [#43](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/43), [QA #44](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/44) |

## 2. My individual contribution

- Implemented and integrated patient registration, identity sign-in, password recovery and password-reset user interfaces in `telemed-ia-identity-and-access-portal`, including password strength guidance, API error handling and shell session integration.
- Extended `telemed-ia-identity-and-access-api` with password-recovery outbox records, consistent password rules, service-to-service tokens, internal account provisioning, administrator bootstrap, account search and blocking/restoring access; addressed HTTP error handling and network/deployment configuration.
- Developed the password-recovery notification flow across `telemed-ia-notifications-api` (use case, persistence, SMTP adapter) and `telemed-ia-notifications-db` (Liquibase migrations, schema, indexes, grants and correlation ID changes).
- Built the `telemed-ia-intelligent-agent-api` HU-06 pre-consultation flow: role-based authorization, appointment verification, clinical safety restrictions, scripted/Groq model integration, conversation persistence, transaction/outbox consistency, idempotency, professional-only summaries, structured logs and Prometheus metrics.
- Integrated the real pre-consultation flow into `telemed-ia-intelligent-agent-portal`, including the patient's upcoming appointments, conversations and a visible finish action.
- Updated `telemed-ia-front` to integrate the identity remote, sign-in/sign-out and refresh-token handling, public landing content, administrator account administration and an administrator-specific home page.
- Submitted reviewed pull requests and promoted HU-01/HU-02/HU-03, HU-06, and notification components from `develop` to `qa` using traceable PRs. Existing PR descriptions document automated tests and integration checks.

## 3. Blockers and risks

- The RabbitMQ listener for consuming password-recovery events is not implemented yet; `telemed-ia-notifications-api` QA PR #5 explicitly identifies this dependency on the shared broker (ADR-011). SMTP sending and persistence are implemented, but this does not prove automatic end-to-end event consumption.
- HU-06 QA integration depends on the shared API Gateway route `/api/v1/agent` and on correct Identity API, Appointments API, agent database and AI provider configuration.
- Blocking a user revokes refresh tokens, but an already-issued access token may remain valid until its configured expiry, as documented in Identity and Access API PR #125.
- Cross-repository integration and QA configuration must remain aligned; QA promotion does not itself prove production deployment or zero-downtime rollout.

## 4. Plan for next week

- Complete and verify event-driven password-recovery delivery once RabbitMQ and its consumer are available.
- Continue integration and QA validation across identity, notifications, shell and intelligent-agent services, including authorization and end-to-end patient workflows.
- Review security controls, secrets handling, service-to-service access and dependency configurations.
- Prepare CI/CD and deployment improvements: versioned artifacts, reproducible environments, health checks and documented rollback procedures for MVP 3.
- Address defects found during QA and preserve automated regression tests.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- **Identity and Access API** (`telemed-ia-identity-and-access-api`): [HU-03 QA promotion #120](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/120); [admin account status with tests #125](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/125); [professional account provisioning #122](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/122); [internal service tokens #117](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/117); [HTTP error handling #127](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/pull/127).
- **Identity and Access Portal** (`telemed-ia-identity-and-access-portal`): [HU-01/HU-02/HU-03 QA promotion #19](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/19); [password-reset page #15](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/15); [admin account management #20–22](https://github.com/code-corhuila/telemed-ia-identity-and-access-portal/pull/22).
- **Notifications API** (`telemed-ia-notifications-api`): [HU-03 QA promotion #5](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/5); [SMTP adapter #4](https://github.com/code-corhuila/telemed-ia-notifications-api/pull/4).
- **Notifications DB** (`telemed-ia-notifications-db`): [schema QA promotion #6](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/6); [migration/rollback fix #4](https://github.com/code-corhuila/telemed-ia-notifications-db/pull/4).
- **Intelligent Agent API** (`telemed-ia-intelligent-agent-api`): [HU-06 QA promotion #30](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/30); [structured logs and trace context #28](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/28); [Prometheus metrics #29](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/pull/29).
- **TeleMed IA Front / Shell** (`telemed-ia-front`): [shell identity integration #29](https://github.com/code-corhuila/telemed-ia-front/pull/29); [public landing page #33](https://github.com/code-corhuila/telemed-ia-front/pull/33); [account administration route #39](https://github.com/code-corhuila/telemed-ia-front/pull/39); [administrator home #40](https://github.com/code-corhuila/telemed-ia-front/pull/40).
- **Intelligent Agent Portal** (`telemed-ia-intelligent-agent-portal`): [real API integration #42](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/42); [QA promotion #44](https://github.com/code-corhuila/telemed-ia-intelligent-agent-portal/pull/44).
