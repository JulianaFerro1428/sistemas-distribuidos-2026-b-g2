<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       09-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 09

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Group 2
- SPRINT_GOAL: Complete and harden the Identity & Access MVP 2 authentication lifecycle: patient registration, authentication, RS256 sessions, refresh/logout, password recovery/reset, and UUID-aligned persistence.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-01 | Patient registration and secure credential validation | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/db1b1bda236a9c84b9d157a06115b27880b0ab6e |
| HU-02 | Authentication, RS256 access tokens, refresh rotation and logout | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/b58e2b8181a55e627437f46a8ba439d02d595af2 |
| HU-03 | Password recovery and password reset | doing | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6502be34efe7e5d965d98168337c0f680359b038 |

## 2. My individual contribution

- Continued the implementation and hardening of **HU-01, HU-02 and HU-03** for the Identity & Access bounded context.
- Aligned registration and persistence identifiers with the final **UUID model**.
- Hardened HU-01 password validation, including minimum and maximum limits and the BCrypt UTF-8 byte boundary.
- Updated HTTP and core tests for patient registration validation.
- Migrated access-token issuance and verification to **RS256**.
- Implemented and hardened the authenticated-session boundary.
- Implemented refresh-token issuance, hashing, JPA persistence, rotation and logout.
- Added tests for refresh-token hashing, persistence, rotation, HTTP behavior and logout.
- Validated the complete HU-02 authentication flow.
- Implemented the HU-03 password-recovery token foundation.
- Exposed the password-recovery HTTP endpoint.
- Implemented password-reset core behavior and persistence.
- Added and reorganized tests for the password-recovery/reset flow.
- Maintained the **DDD / Hexagonal Architecture** boundaries between core, adapters and application modules.
- Hardened the Identity & Access database token lifecycle.
- Hardened password-reset concurrency at the database level.
- Validated the UUID migration and rollback contract.

## 3. Blockers and risks

- GitHub Actions execution may be affected by repository/account billing restrictions, so local Maven verification is still required when CI cannot execute.
- HU-03 still requires final end-to-end validation of the complete password-reset lifecycle.
- PostgreSQL/Testcontainers integration coverage must remain aligned with the modular project structure.
- Password-recovery tokens must preserve expiration, single-use and concurrency guarantees across API and database layers.
- API and database contracts must stay aligned on UUID identifiers and must not depend on legacy numeric identifiers.
- JWT private-key material must remain outside source control and runtime logs.
- Persistence behavior must be revalidated after any token-lifecycle or UUID schema change.

## 4. Plan for next week

- Complete HU-03 password-reset HTTP behavior and final integration.
- Validate the complete password recovery/reset lifecycle end to end.
- Restore and strengthen PostgreSQL/Testcontainers integration coverage for the Identity & Access persistence adapters.
- Validate duplicate-email and duplicate-identity-document registration through the real HTTP + PostgreSQL path.
- Validate refresh-token and password-reset-token lifecycle behavior against the final database schema.
- Continue persistence hardening before the MVP 2 release.
- Validate required runtime configuration at application startup.
- Keep secrets outside Git and inject sensitive configuration at runtime.
- Prepare the service for the MVP 2 progressive release and rollback process.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

### HU-01 — Patient registration

- Registration password policy:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/db1b1bda236a9c84b9d157a06115b27880b0ab6e
- BCrypt UTF-8 byte-limit validation:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6fb369c4e69728801b1975b583d013373f595439
- HTTP password-validation coverage:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/7fc7d93c077f697d7b219f3faa8aca1f89b1e657
- UUID registration identifiers:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/91a385a7097d7478acb9ad674d2b28f182762121
- UUID registration test:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/01f578dc1cf38a264b32b267d89c5b1fbacb5bc1

### HU-02 — Authentication and session lifecycle

- RS256 migration:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/8b202c4378b9db84ade10e0109fd257bb978b7b6
- RS256 access-token verification:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/113917cc5b5476b2cafdc5c034cfa61211441d0a
- Authenticated session boundary:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/bac0dc95c13f8b009915253cc241b09fc5860e8c
- Refresh-token issuance:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/2f417ebfab1a5cb9b7f63543fed426b6761d7a56
- Refresh-token JPA persistence:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/7d89c84f560721dbf9cf2ffdea723a888c994613
- Refresh-token rotation:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a6df19ee13219ccbabe6b7780de9c1ed7bd35130
- Refresh-token endpoint:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/fde6cc93105e34eabedf0c2ac40a6b6e8914b717
- Refresh-token logout:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/f7515ace55af28b367fcfe4043ebe97c0967af93
- Complete HU-02 authentication-flow validation:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/b58e2b8181a55e627437f46a8ba439d02d595af2

### HU-03 — Password recovery and reset

- Password-recovery token foundation:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/1140f342f5ae822e73c75bb7c8c802b3a3b1579c
- Password-recovery endpoint:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/bba1ac2af11bdcf54bbb29f6668a7558b7ce1ab9
- Password-recovery endpoint behavior test:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/4e009bda5f0058ac7d53ed5e6c438c3b29812eaa
- Password-reset core and persistence:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6502be34efe7e5d965d98168337c0f680359b038
- Password-reset implementation commit:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/e4a94a328148ba7a4e8f67d4defafa9977b7a2c1

### Identity & Access database

- Complete Identity token persistence model:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/fcac92adb2b0f09fa4c198c7636b126ee9241166
- Token lifecycle review hardening:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/44ca98011fb9938f86472e4cee69926f96f2513c
- Password-reset lifecycle concurrency:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/caaa374b97f4b75bd5dee1fd2d7cfa8e266f4e85
- Password-reset lifecycle review fixes:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/e2a734a2a7a468a3ef953e5c34a957aee9c6af71
- UUID identifier cutover:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/4659985bdaccbff38e2cf2e69492e7cbf8910852
- UUID validation and rollback hardening:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/26d99e2d95428b38b343bf501cb9c0731376f787
- UUID rollback and migration documentation:
  https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/491b475a2c5ecd177895b9b8c56daf02f75404e1