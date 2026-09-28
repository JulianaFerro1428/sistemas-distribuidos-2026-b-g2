<!-- HU-STATUS TEMPLATE - do NOT remove the comment markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       08-week/hu-status/README.md (inside YOUR fork). English. -->

# Weekly Status - Week 08

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai
- SPRINT_GOAL: Advance the migration to a microservices architecture by strengthening the Identity & Access API and database, implementing and hardening HU-01 and HU-02, improving API error and correlation handling, evolving the PostgreSQL/Liquibase persistence model, and aligning the shared API documentation and contracts with the current microservice architecture.

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-01 | Patient Registration | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/bd54fc43d9a345e2ff447c226e0f08ffafd0bc8f |
| HU-02 | Authentication and Access Token | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/652c2b838e92c52d674c4e574e1f308da1405132 |
| Cross-cutting | Identity & Access API architecture, runtime support, errors and correlation IDs | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a9a2875716c813a0677508d14879032f728def15 |
| Cross-cutting | Identity & Access database evolution, security and validation | done | https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/1f7a52c |
| API Docs | Authentication contract consistency and clinical field serialization | done | https://github.com/code-corhuila/telemed-ia-docs/commit/81f988e0e1c0ecf20e8c3cd549f41b9795882daf |

## 2. My individual contribution

- Implemented the core patient registration flow for HU-01 in the Identity & Access API.
- Implemented the HU-02 authentication core, access-token generation and login REST API.
- Hardened authentication behavior with generic invalid-credential handling, additional unit coverage and BCrypt password rehashing for legacy hashes after successful login.
- Refactored the Identity & Access API into separated core, adapters and application modules to reinforce DDD and hexagonal architecture boundaries.
- Added project support files, CI configuration, Compose/runtime support, environment configuration examples and repository documentation.
- Standardized API error responses and correlation ID handling, and decoupled error responses from the correlation context.
- Evolved the Identity & Access database using a structured Liquibase migration layout and a single migration authority.
- Added database runtime and validation support, least-privilege database access rules, runtime roles and explicit access-control validation.
- Expanded the Identity & Access persistence model with UUID identifiers and completed the UUID identifier cutover.
- Improved refresh-token and password-reset-token persistence and addressed token lifecycle review findings.
- Updated the shared API documentation to align the authentication service contract and API overview with the current microservice architecture.
- Defined clinical field serialization rules and incorporated review feedback into the API documentation.

## 3. Blockers and risks

- GitHub Actions CI could not run for part of the work because of a repository billing restriction, so the affected changes were validated locally.
- Changes across the API, database and shared documentation must remain synchronized to avoid contract drift between the documented API behavior and the microservice implementation.
- Review follow-ups can introduce additional consistency changes in authentication, token persistence and shared API contracts.

## 4. Plan for next week

- Continue the remaining Identity & Access user-story work and integrate the API and database flows end to end.
- Expand integration and end-to-end coverage for registration, authentication, token persistence, error responses and correlation IDs.
- Continue validating the modular DDD/hexagonal architecture boundaries of the Identity & Access service.
- Keep the OpenAPI contracts and shared API documentation synchronized with the implemented microservices.
- Re-run repository CI when the GitHub Actions billing restriction is resolved and address any CI-specific findings.
- Continue using small, reviewable PRs and separate changes by responsibility across API, database and documentation repositories.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

### API documentation

- [docs(api): define clinical field serialization](https://github.com/code-corhuila/telemed-ia-docs/commit/6ae07df90a065344ff6083de415254ed1c4c7689)
- [chore: sync clinical serialization branch with main](https://github.com/code-corhuila/telemed-ia-docs/commit/27de91b31248bf4c8762f6781520776617d19b86)
- [docs(api): address clinical serialization review feedback](https://github.com/code-corhuila/telemed-ia-docs/commit/ffe892aa79f1a42367110c4aa86b84e0f4369bd5)
- [docs(api): define clinical field serialization](https://github.com/code-corhuila/telemed-ia-docs/commit/282ab4baf1ac86bc7cb26c4f71630c850b5d38d2)
- [docs(api): align auth service contract and API overview](https://github.com/code-corhuila/telemed-ia-docs/commit/81f988e0e1c0ecf20e8c3cd549f41b9795882daf)

### Identity & Access API

- [fix(identity-api): unify error responses and correlation ids](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/265a9715910f7ef68cfcc123b7d3a1de7eed111c)
- [fix(identity-api): decouple error responses from correlation context](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6a2fe2db620362e015feb01080d1d484feaa4a78)
- [fix(identity-api): unify error responses and correlation ids](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6fdabaf4132866fe9fc6d755eaefdb258baa9b75)
- [chore(identity): add ci and project support files](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/140dc1a56720d1a0a58d350d518c70d275a4e348)
- [refactor(identity): separate core, adapters and app modules](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a9a2875716c813a0677508d14879032f728def15)
- [fix(auth): rehash legacy BCrypt passwords after login](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a5de75b79a00b7cbeacb274c277582f2a497116c)
- [fix(auth): harden identity API bootstrap](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/d83ecbe75635ccad2c77a1ee3ab67630b418b469)
- [chore(auth): bootstrap identity and access service](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/d85dca1567003e0d22b366baa5ce4e0f57832833)
- [feat(auth): HU-01 - add patient registration core use case](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/bd54fc43d9a345e2ff447c226e0f08ffafd0bc8f)
- [feat(auth): HU-01 - add patient registration core use case](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/7c2e1cfc8d86eeda6ac6c7860fb00f889cb2a4b1)
- [feat(auth): HU-02 - add authentication core and access token](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/652c2b838e92c52d674c4e574e1f308da1405132)
- [fix(auth): harden HU-02 authentication and add unit coverage](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/0e5207dcd475355eed54762ce21577dc51e896c9)
- [feat(auth): HU-02 - expose login REST API](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/31569e1ce75826f26014e8899a96759b4b61e876)

### Identity & Access Database

- [fix(db): address token lifecycle review follow-up](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/44ca98011fb9938f86472e4cee69926f96f2513c)
- [fix(db): address token lifecycle review follow-up](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/65352db9c1799bedb9e501c078624c01d74afa9e)
- [fix(db): complete identity token persistence model](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/fcac92adb2b0f09fa4c198c7636b126ee9241166)
- [fix(db): complete identity token persistence model](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/b39c61833ca520287ae3f30044a0e5cb290353e4)
- [feat(db): complete UUID identifier cutover](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/4659985bdaccbff38e2cf2e69492e7cbf8910852)
- [fix(db): harden UUID contract and rollback](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/6932b6080221825bb4b75e42c446f9c21dd61c5d)
- [feat(db): complete UUID identifier cutover](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/bb12e200cf09f1dcb977f348e57b234064ef2cb0)
- [test(db): validate identity database access control](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/6e18515ed73e5158acd1ae998a8f1263ad94793f)
- [test(db): validate identity database access control](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/195af169b7e89998bfbf523ec2e961bddc4e2d2a)
- [feat(db): expand identity schema with UUID identifiers](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/9bba30b95f5196069a7302b58ac7efb330d84e61)
- [refactor(db): split UUID migration into expand phase](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/4b4e0c66e8f59de6304386764a3bab9d003ac859)
- [feat(db): migrate identity identifiers to UUID](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/071b1c47963f45112f889c9a21d56c9316ce6ebd)
- [chore(db): harden migration authority and database access](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/1f7a52cac62c1fd3c7e0a14fd83b8f66edfa575b)
- [fix(db): harden role rollback handling](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/df2aab534d32497ae7c58b7d46f3dd9ca7374b1f)
- [chore(db): harden migration authority and database access](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/09e4e3ecd413a90557faaf0714b17050c3cc1009)
- [chore(db): add database runtime and validation support](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/bcbd1e305a4ed22004abda731af7b853a7a88b99)
- [chore(db): add database runtime and validation support](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/fef4648d29b698e3d94b13b9d20c8726ced622ad)
- [chore(db): add structured Liquibase migration layout](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/abd57107c9bba1e9fa4e03d62629cbe923eb09f6)
- [fix(db): address password reset lifecycle review](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/85d3ea23182ab03dde2e38fc5cd10a62a66e0934)
- [chore(db): add structured Liquibase migration layout](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/f892338bc4cd1f850b9ce29c7eeb718168d06898)
- [fix(identity-db): address schema review findings](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/950848e113a470e4066d01f1144531741e9ab5a3)
- [fix(identity-db): address schema review findings](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/7db408ef880cde13fc6264763cd9010fa798dc87)
- [test(identity-db): add core schema validation](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/98cbc45319568b1df693c35afa83065ee3df7893)
- [test(identity-db): add core schema validation](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/c8f734edb59b221014e7bbe3ab7dcfe7c227e209)
- [feat(identity-db): define Liquibase-managed identity schema- #1](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/22dc78471673a8c8dd8245e83cb0ecf36d546e0f)
- [fix(auth): harden identity schema with Liquibase](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/6e8ebdff8216ca0308f5626eca8b004f07bcc066)
- [feat(identity-db): define identity and access schema](https://github.com/code-corhuila/telemed-ia-identity-and-access-db/commit/175960485bac801d2f90b1404c2fc59c00369890)

