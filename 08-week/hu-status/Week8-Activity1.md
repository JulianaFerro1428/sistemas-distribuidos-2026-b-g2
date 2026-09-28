# Week 08 — Sprint Execution Implementation

## TeleMed-AI — Identity & Access Microservice

## 1. Overview

This week, the TeleMed-AI team applied Scrum and DevOps practices directly to the development of the **Identity & Access microservice**.

The work was distributed across three repositories:

- `telemed-ia-identity-and-access-api`
- `telemed-ia-identity-and-access-db`
- `telemed-ia-docs`

The sprint focused on completing and hardening the first Identity & Access capabilities while keeping the API implementation, PostgreSQL persistence model, and shared contracts synchronized.

The sprint was managed using:

- A prioritized backlog.
- Testable user stories.
- A Work in Progress limit.
- Small and focused Pull Requests.
- Daily synchronization.
- Continuous review feedback.
- Throughput tracking.
- MVP 2 backlog refinement.

---

# 2. Sprint Goal

The goal for this sprint was:

> **Strengthen the Identity & Access microservice by completing the patient registration and authentication flows, improving its modular architecture, hardening database persistence and access control, and keeping the shared API contracts aligned with the implementation.**

This goal involved coordinated work across the API, database, and documentation repositories.

---

# 3. Prioritized Sprint Backlog

The backlog was prioritized according to the value required to make the Identity & Access service usable and ready for future integration with the rest of TeleMed-AI.

| Priority | Work Item | Repository | Status |
|---|---|---|---|
| Must | HU-01 — Patient Registration | Identity & Access API | Done |
| Must | HU-02 — Authentication and Access Token | Identity & Access API | Done |
| Must | Identity database schema and migrations | Identity & Access DB | Done |
| Must | Password and token persistence | Identity & Access DB | Done |
| Must | Authentication API contract alignment | Docs | Done |
| Must | Unified API error responses | Identity & Access API | Done |
| Must | Correlation ID handling | Identity & Access API | Done |
| Must | Database access control | Identity & Access DB | Done |
| Should | Modularize API into core, adapters, and application modules | Identity & Access API | Done |
| Should | UUID identifier migration | Identity & Access DB | Done |
| Should | Repository CI and project support files | Identity & Access API | Done |
| Should | Clinical field serialization documentation | Docs | Done |
| Could | Additional documentation refinements | Docs | Ongoing |
| Could | Additional integration coverage | API / DB | Planned |

The team prioritized the **Must** items before adding architectural or documentation improvements.

---

# 4. Testable User Stories

## HU-01 — Patient Registration

### User Story

> **As a patient, I want to create an account so that I can access the TeleMed-AI platform.**

### Testable Acceptance Criteria

- [x] A valid patient registration request creates a new user.
- [x] The patient is assigned the expected initial role.
- [x] The password is stored using BCrypt instead of plain text.
- [x] The registered user is persisted in PostgreSQL.
- [x] Duplicate registration conflicts are rejected.
- [x] Validation errors are returned using the shared API error structure.
- [x] The registration core is separated from infrastructure concerns.

### Implementation Evidence

- [HU-01 patient registration core](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/bd54fc43d9a345e2ff447c226e0f08ffafd0bc8f)
- [HU-01 patient registration implementation](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/7c2e1cfc8d86eeda6ac6c7860fb00f889cb2a4b1)

---

## HU-02 — Authentication and Access Token

### User Story

> **As a registered patient, I want to authenticate with my credentials so that I can securely access protected TeleMed-AI functionality.**

### Testable Acceptance Criteria

- [x] A user with valid credentials can authenticate.
- [x] Successful authentication generates an access token.
- [x] Invalid credentials return a generic authentication error.
- [x] Unknown emails and incorrect passwords do not expose account existence.
- [x] Inactive users cannot authenticate successfully.
- [x] Authentication behavior is covered by unit tests.
- [x] Legacy BCrypt hashes can be rehashed after successful authentication.
- [x] Login functionality is available through the REST API.

### Implementation Evidence

- [HU-02 authentication core and access token](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/652c2b838e92c52d674c4e574e1f308da1405132)
- [HU-02 authentication hardening and unit coverage](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/0e5207dcd475355eed54762ce21577dc51e896c9)
- [HU-02 login REST API](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/31569e1ce75826f26014e8899a96759b4b61e876)
- [BCrypt legacy password rehash](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a5de75b79a00b7cbeacb274c277582f2a497116c)

---

# 5. Work in Progress Limit

To prevent too many unfinished tasks from accumulating, the sprint uses the following operational rule:

> **Maximum WIP: 2 active work items per contributor.**

The expected flow is:

```text
Backlog
   ↓
To Do
   ↓
In Progress
   ↓
Pull Request / Review
   ↓
Validation
   ↓
Done
```

A new item should not be started when the WIP limit has already been reached.

Instead, priority is given to:

1. Completing the current implementation.
2. Resolving review comments.
3. Running the required tests.
4. Merging the Pull Request.
5. Starting the next backlog item.

This approach was especially important because the work was distributed across API, database, and documentation repositories.

---

# 6. Pull Request Strategy

Every significant change was developed as an isolated responsibility and integrated through Pull Requests.

The team avoided combining unrelated API, persistence, documentation, and architecture changes into the same PR.

## Identity & Access API

### PR #28 — BCrypt Rehash

```text
fix(auth): rehash legacy BCrypt passwords after login
```

Evidence:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a5de75b79a00b7cbeacb274c277582f2a497116c

---

### PR #29 — Modularization

```text
refactor(identity): separate core, adapters and app modules
```

The API was reorganized to better respect DDD and hexagonal architecture boundaries.

Evidence:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/a9a2875716c813a0677508d14879032f728def15

---

### PR #30 — Project Support

```text
chore(identity): add ci and project support files
```

This change added operational support required by the new modular structure.

Evidence:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/140dc1a56720d1a0a58d350d518c70d275a4e348

---

### PR #31 — Error Responses and Correlation IDs

```text
fix(identity-api): unify error responses and correlation ids
```

Evidence:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/265a9715910f7ef68cfcc123b7d3a1de7eed111c

Supporting implementation:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6fdabaf4132866fe9fc6d755eaefdb258baa9b75

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/6a2fe2db620362e015feb01080d1d484feaa4a78

---

# 7. Database Work

The database repository evolved together with the API instead of being treated as an independent implementation.

The work included:

- Liquibase-managed schema evolution.
- Structured migration layout.
- Database validation.
- Runtime database support.
- Database access control.
- Reader and writer runtime roles.
- UUID identifier migration.
- Token persistence.
- Password-reset-token lifecycle improvements.
- Migration and rollback hardening.

Examples of completed work include:

```text
feat(identity-db): define identity and access schema
test(identity-db): add core schema validation
fix(identity-db): address schema review findings
chore(db): add structured Liquibase migration layout
chore(db): add database runtime and validation support
chore(db): harden migration authority and database access
test(db): validate identity database access control
feat(db): expand identity schema with UUID identifiers
feat(db): complete UUID identifier cutover
fix(db): complete identity token persistence model
fix(db): address token lifecycle review follow-up
```

This work ensures that the database contract evolves consistently with the API implementation.

---

# 8. Shared Documentation

The `telemed-ia-docs` repository was also included in the sprint because contracts are part of the distributed system implementation.

## PR #42 — Authentication Contract Alignment

```text
docs(api): align auth service contract and API overview
```

Evidence:

https://github.com/code-corhuila/telemed-ia-docs/commit/81f988e0e1c0ecf20e8c3cd549f41b9795882daf

---

## PR #43 — Clinical Serialization

```text
docs(api): define clinical field serialization
```

Evidence:

https://github.com/code-corhuila/telemed-ia-docs/commit/6ae07df90a065344ff6083de415254ed1c4c7689

Review feedback was also incorporated:

https://github.com/code-corhuila/telemed-ia-docs/commit/ffe892aa79f1a42367110c4aa86b84e0f4369bd5

This prevented the documentation from drifting away from the implemented contracts.

---

# 9. Review Feedback Loop

Pull Requests were not treated only as merge mechanisms.

Review feedback was used as part of the sprint feedback loop.

The workflow was:

```text
Implementation
      ↓
Pull Request
      ↓
Review
      ↓
Review findings
      ↓
Follow-up commit
      ↓
Validation
      ↓
Merge
```

Examples included:

- Authentication hardening.
- Database rollback corrections.
- Token lifecycle corrections.
- API error consistency.
- Documentation contract corrections.
- Clinical serialization review changes.
- UUID migration improvements.

This allowed changes to remain small while still incorporating feedback before integration.

---

# 10. Daily Sync

The sprint used a short daily synchronization centered around three questions.

## 1. What was completed?

Examples:

- HU-01 core completed.
- HU-02 authentication implemented.
- Login REST endpoint exposed.
- Database schema migrations added.
- API modularization completed.
- API documentation updated.

## 2. What is currently in progress?

Examples:

- PR review follow-ups.
- Database migration hardening.
- Authentication contract alignment.
- Error and correlation handling.
- Integration tests.

## 3. What is blocked?

Examples identified during the sprint included:

- PRs waiting for review.
- Cross-repository contract consistency.
- CI execution affected by GitHub Actions billing restrictions.
- Database migration dependencies.
- Synchronization between API identifiers and database UUID changes.

The purpose of the daily sync was to expose blockers quickly rather than allow unfinished work to accumulate.

---

# 11. Throughput Tracking

For this sprint, throughput is measured primarily using **completed Pull Requests / integrated change sets**, rather than the number of commits.

This is important because several commits may belong to the same logical work item.

The provided repository evidence clearly identifies the following merged Pull Requests:

| Repository | Completed PRs |
|---|---:|
| Identity & Access API | 4 |
| Shared API Documentation | 2 |
| **Clearly evidenced throughput** | **6 merged PRs** |

API PRs:

```text
#28 — BCrypt password rehash
#29 — API modularization
#30 — CI and project support
#31 — Error responses and correlation IDs
```

Documentation PRs:

```text
#42 — Authentication contract consistency
#43 — Clinical serialization
```

Database work was also integrated into `develop`, but it is tracked separately because the supplied database history contains multiple implementation and merge commits and not every PR number is explicitly available in the current evidence.

Therefore:

> **Minimum verified throughput for the week: 6 completed PR integrations, plus the completed database change stream.**

This measurement avoids artificially inflating throughput by counting every individual commit as a separate delivered item.

---

# 12. Sprint Flow Metrics

The team tracks the following metrics:

## WIP

```text
Maximum active work:
2 items per contributor
```

## Throughput

```text
Completed Pull Requests / sprint
```

Current verified value:

```text
6 merged PRs + DB integrations
```

## Cycle Time

Measured conceptually from:

```text
Development start
        ↓
Implementation
        ↓
PR opened
        ↓
Review
        ↓
Corrections
        ↓
Merge
```

Review follow-ups should reduce future cycle time by detecting architectural and contract problems earlier.

---

# 13. Definition of Done

For TeleMed-AI, an item is considered **Done** only when the relevant conditions are satisfied.

- [x] Implementation completed.
- [x] Acceptance criteria verified.
- [x] Tests added or updated where required.
- [x] Architecture rules respected.
- [x] Pull Request reviewed.
- [x] Review findings resolved.
- [x] No secrets committed.
- [x] Environment configuration externalized.
- [x] Database migrations validated when required.
- [x] Documentation updated when contracts change.
- [x] Change integrated into the appropriate repository branch.

A story is not considered Done simply because the code was written.

---

# 14. Session 2 — MVP 2 Backlog Refinement

The second planning session uses what was learned during the sprint to refine the next MVP 2 backlog.

The Identity & Access work establishes a foundation for future service integration.

Candidate backlog items for the next planning stage include:

| Candidate Work | Priority | Relative Size |
|---|---|---:|
| End-to-end patient registration validation | Must | 3 |
| End-to-end authentication validation | Must | 3 |
| Access-token integration with downstream services | Must | 5 |
| API Gateway identity header propagation | Must | 5 |
| Validate UUID contract between API and DB | Must | 3 |
| Token persistence integration | Must | 5 |
| Cross-service authentication contract tests | Should | 5 |
| Additional API documentation refinement | Should | 2 |
| Additional CI automation | Could | 3 |

These values are **relative planning estimates** and should be validated by the team through Planning Poker before the final MVP 2 commitment.

---

# 15. MVP 2 Planning Criteria

Before committing the next scope, each backlog item should satisfy:

- [ ] Clear user or technical value.
- [ ] Testable acceptance criteria.
- [ ] Relative estimate agreed by the team.
- [ ] Cross-service dependencies identified.
- [ ] API contract available when another service depends on it.
- [ ] Story small enough to complete during the sprint.
- [ ] Priority defined as Must, Should, or Could.
- [ ] Contribution to the MVP 2 Sprint Goal is clear.

---

# 16. Results

This week's sprint moved Identity & Access from isolated implementation work toward a more production-oriented microservice.

The team delivered:

- Patient registration.
- Authentication core.
- Access-token generation.
- Login REST API.
- Authentication hardening.
- BCrypt password migration support.
- Modular hexagonal structure.
- Unified API errors.
- Correlation ID handling.
- PostgreSQL schema evolution.
- Database access control.
- UUID migration.
- Token persistence improvements.
- Shared contract documentation.
- Review-driven corrections across repositories.

At the same time, the work was managed using a controlled delivery flow instead of opening multiple unrelated tasks simultaneously.

---

# 17. Key Takeaway

> **Start less, finish more, integrate continuously.**

For TeleMed-AI, sprint success is not measured by the number of commits produced.

It is measured by how many **tested, reviewed, documented, and integrated changes** reach a usable state while keeping the API, database, and shared contracts consistent.