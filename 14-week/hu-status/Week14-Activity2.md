# TeleMed IA — CI/CD Planning: DoD Gates, Branch Protection, and MVP 3

**Unit 3 · Planning · Corte 3**  
**Project:** TeleMed IA — Grupo 2  
**Activity:** Finalize the CI/CD pipeline plan  
**Review date:** 2026-11-10  
**Evidence scope:** `telemed-ia-docs` (`main`) and selected application repositories (`develop`)

> **This week:** Finalize the **pipeline**: map every Definition of Done (DoD) item to a **blocking gate**, enable **branch protection** on `develop`/`qa`/`main`, add **path-filtered per-service pipelines** and **cross-service contract tests**, and a **smoke test**. Slice the CI/CD stories for MVP 3. Next week: testing at scale, then the MVP 3 release.

## 1. Objective and evidence policy

The goal is to make **Done** an auditable result rather than an assertion: an applicable check must fail when its condition is false, and GitHub must prevent merging until the required checks and reviews succeed. In a distributed product, each repository verifies its own changes while integration tests verify that the system's contracts remain compatible.

**Status legend:**

- **Implemented in repository:** a file or automated test demonstrates the control in code.
- **Documented / not verified as enforced:** a project rule is recorded, but the live GitHub setting or successful execution was not confirmed.
- **Partial:** the repository has part of the necessary mechanism.
- **Proposed for MVP 3:** an implementation or configuration that still needs a PR and evidence.

**Verification limitations:** This is a repository-content review, **not** an execution report. No workflow was run or deployment performed for this activity. The GitHub connector returned **403 (Resource not accessible by integration)** for live branch-protection endpoints, so actual enforcement of protection settings could not be confirmed. The project's [`10-devops/ci-cd.md`](https://github.com/code-corhuila/telemed-ia-docs/blob/main/10-devops/ci-cd.md) states that GitHub Actions execution is blocked by the organization's billing limit; the current billing state must be reconfirmed with an administrator before describing any gate as operational.

## 2. System boundaries and pipeline architecture

TeleMed IA is **multi-repository**, not one monorepo. It comprises Angular/React portals and a shell, the NGINX API Gateway, Java/Spring Boot services, a Python/FastAPI appointment API, a Go medical-consultation API, a Java worker and workflow, plus PostgreSQL/MongoDB migrations and shared infrastructure. The gateway handles public entry; service-to-service requests and asynchronous events also need integration coverage.

```text
FEATURE / FIX (child branch of each code repository)
          |
          v
PULL REQUEST --> per-repository CI --> BLOCKING QUALITY GATES
          |        (build, lint/format, type, tests, coverage,
          |         security, API/schema contracts)
          v
REVIEW + PROTECTED BRANCH
          |
          v
       develop
          |
          |  controlled cherry-pick -x into a child of qa
          v
          qa  --> integration environment --> cross-service tests --> smoke
          |
          |  release/<version> child of main, validated changes
          v
         main --> signed/immutable version tag --> image publication
          |
          v
  controlled environment promotion + post-deploy smoke
```

**Course-specific constraint:** `develop`, `qa`, and `main` are **separate permanent branches in code repositories**. Promotion is **re-application using `git cherry-pick -x` through a PR**, not `merge develop → qa` or `merge qa → main`. The `telemed-ia-docs` documentation repository has **only `main`** as a permanent branch. Refer to the [Branching Policy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/branching-policy.md) and [Git Conventions](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/git-conventions.md).

## 3. Current implementation snapshot

| Concern | Evidence observed | Assessment |
|---|---|---|
| Java API CI | `mvn -B verify` on PRs in Identity, Professional Management, Document Generation, and other Java repos | **Implemented:** baseline compile/test; full six-gate policy not demonstrated |
| Gateway CI | Docker image build, `nginx -t`, and `tests/smoke.sh` | **Implemented:** component-level smoke tests |
| Frontend CI | `npm ci`, `npm run test:ci`, `npm run build` in the shell | **Implemented:** baseline tests and build; separate type/coverage/security gates not demonstrated |
| Python appointment CI | `ci.yml` on `develop`: Python setup and `python -m pytest -q` | **Implemented:** test workflow; expand lint, typing, integration and security checks |
| Go consultation CI | Go 1.25, formatting, `go vet`, build, and race-enabled tests | **Implemented:** stronger baseline gates than most repos |
| Workflow CI | `mvn -B verify` plus separate workflow database tests | **Implemented:** service and database checks |
| Contract verification | Test strategy defines validation against OpenAPI; Java project POMs include a contract validator in some services | **Partial:** per-service checking exists; a mandatory **cross-service** integration job is not verified |
| Coverage policy | DoD says coverage must not decrease without approved justification; no project-wide numeric floor is defined | **Documented / incomplete automation** |
| Dependency, secret, SAST scans | Listed as desired quality/security controls; not confirmed as required jobs in examined workflows | **Proposed for MVP 3** |
| Branch protection | Policy prohibits direct commits and defines reviews/status checks | **Documented; live settings unverified** |
| Path filters | The inspected service workflows primarily target branches without selective `paths` filters | **Proposed for MVP 3 where appropriate** |
| Registry and automatic deployment | Current documented deployment uses Docker Compose builds; no hosted environment or shared image registry | **Proposed for MVP 3** |

Evidence: [Identity CI](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/.github/workflows/ci.yml), [Gateway CI](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/.github/workflows/ci.yml), [Frontend CI](https://github.com/code-corhuila/telemed-ia-front/blob/develop/.github/workflows/ci.yml), [Workflow CI](https://github.com/code-corhuila/telemed-ia-workflow/blob/develop/.github/workflows/ci.yml), [Appointment CI](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/.github/workflows/ci.yml), [Consultation CI](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/blob/develop/.github/workflows/ci.yml), [Testing Strategy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/11-quality/testing-strategy.md).

## 4. Map Definition of Done to automated gates

The source of truth is the project's [Definition of Done](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/definition-of-done.md). **Automation complements human checks; it cannot prove that a business requirement was interpreted correctly.**

| Existing DoD requirement | Proposed verifier / gate | Blocking failure condition | State |
|---|---|---|---|
| Naming, style, formatting | ESLint/Prettier or framework lint; `ruff check`/`ruff format --check`; `gofmt`; Java formatter/Checkstyle after configuration | Formatting or lint rule violation | Partial |
| Code compiles/builds | `mvn -B verify`, `go build ./...`, `npm run build`, Python package install | Non-zero exit | Implemented for several repos |
| Strict types | `tsc --noEmit` or project typecheck script; `mypy`/`pyright` if adopted for Python; Go `go vet` | Type/static-analysis error | Partial |
| Unit / HTTP / integration tests pass | JUnit/MockMvc/Testcontainers, Vitest, pytest, Go tests | Required test fails or is unexpectedly skipped | Partial |
| API and event contracts are preserved | OpenAPI response validation and provider/consumer compatibility tests; schema/event checks | Incompatible contract or failing consumer | Partial |
| Coverage does not decrease | Coverage report with versioned project baseline; threshold ratchet on touched code or service | Measured coverage falls below agreed baseline | Proposed |
| Authorization and resource ownership | Negative 401/403 tests and cross-user access tests | Unauthorized action succeeds | Partial (tests exist) |
| No credentials in code | Secret scan on PR diff and, where supported, repository history | Verified secret detected | Proposed as required gate |
| Dependencies are reviewed | SCA/CVE scan with published severity/exception policy | Unapproved critical/high vulnerability | Proposed as required gate |
| Secure application code | SAST / static security checks | Unapproved finding above policy threshold | Proposed as required gate |
| Database changes are reversible | `db-ci.yml`: apply, validate, roll back, rebuild | Failed migration/rollback/permissions check | Implemented in DB repositories |
| PR review and conflicts | GitHub required reviews, conversation resolution, up-to-date requirement | Missing approval, unresolved discussion, stale check | Documented; enforcement unverified |
| Runtime smoke works | Health + authenticated journey + dependency checks after deployment | Any required smoke check fails | Partial |
| Documentation, ADR, and OpenAPI updated | Change detection and PR checklist; optional schema/link validator | Missing mandatory generated or contract artifact | Partial; human review required |

**Coverage note:** The course does **not** currently prescribe one numeric percentage. Do not present `80%` or any other floor as existing policy. Agree on a service baseline, record it in the repository, and then make the gate enforce it.

**Exceptions:** For any applicable DoD item that cannot be tested automatically, require a manual evidence link, reviewer approval, owner, and risk/technical-debt record. A skipped integration test is **not a pass**. Exceptional overrides must follow the DoD's documented policy and cannot silently bypass the branch rules.

## 5. Branch protection design

Create or verify GitHub rulesets / branch-protection rules in **each code repository**. Policy is not equivalent to a GitHub setting.

| Branch | Required checks | Reviews | Additional protection |
|---|---|---|---|
| `develop` | Build, lint/format, type, tests, approved coverage and security jobs | At least **one other team member** | PR only, no force pushes, resolve conversations, up-to-date status checks |
| `qa` | Above **plus** cross-service contracts and environment smoke | At least **one other team member** | PR only, `cherry-pick -x` trace, no force pushes, up-to-date checks |
| `main` | All release gates, immutable-image/tag and deployment readiness | **One approval from `@ariel5253`** | CODEOWNERS, stale approvals dismissed, resolved conversations, no direct pushes/force pushes |
| `telemed-ia-docs/main` | Documentation/link/contract-file checks, when available | **One approval from `@ariel5253`** | PR only; documentation repo does not have `develop` or `qa` |

### Configuration checklist (GitHub)

- [ ] For each branch, **Require a pull request before merging** and set the correct reviewer rule.
- [ ] **Require status checks to pass**, selecting the exact stable **job names** that the workflow publishes.
- [ ] Require branches to be **up to date** with the target before merging, if compatible with the promotion workflow.
- [ ] Require conversation resolution; dismiss stale approvals for protected release branches.
- [ ] Disable force pushes and branch deletion; restrict bypass privileges.
- [ ] Apply CODEOWNERS to high-risk configuration (`.github/workflows/`, `deploy/`, auth and security rules).
- [ ] Confirm the rules actually reject a failing PR and direct push using a safe, non-production test.
- [ ] Capture the configured rules / required-check names as evidence. If organization settings prevent them, register the blocker explicitly; do not report branch protection as enabled.

**Critical blocker:** The connected GitHub read access could not inspect protection settings (HTTP 403). Also, the team's DevOps documentation reports an Actions billing blocker. **Do not make unavailable checks mandatory and assume the workflow is operational**: an administrator must first restore executions, or register the limitation and use documented local results temporarily. Local results are not equivalent to enforceable GitHub checks.

## 6. Per-service pipelines and path filtering

Each repository owns its `.github/workflows/ci.yml`; it compiles/tests/scans **that repository**. In a multi-repo architecture, a change to Identity does **not** appear as a changed path in the Appointment repository. Therefore:

1. **Per-repository PR CI** triggers directly on that repository's Pull Requests.
2. **Path filters** are useful **within** a repository to avoid unrelated jobs, for example portal assets versus backend code, or DB migrations versus documentation.
3. **Cross-repository contract checks** need a separate orchestrator, reusable workflow plus explicit triggers, or a scheduled/pre-release integration run. A GitHub `paths` filter cannot observe commits in another repository.
4. If an upstream service changes an API/event contract, explicitly trigger or require a compatibility check for its consumers before QA promotion.

### Example: selective workflow triggers (proposed template)

The paths below illustrate a Java service. Adjust them to the actual repository layout; do not copy unchanged if tests, contracts, or deployment files live elsewhere.

```yaml
name: Service CI

on:
  pull_request:
    branches: [develop, qa, main]
    paths:
      - '**/*.java'
      - '**/pom.xml'
      - '**/src/test/resources/contract/**'
      - '.github/workflows/**'
      - 'deploy/**'
  workflow_dispatch:

permissions:
  contents: read

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B verify
```

**Not yet the complete required-gate workflow:** this template illustrates only selective execution and a build/test job; separate **real** lint, type, coverage, secret/SCA/SAST and contract jobs must be configured with their own command and failure thresholds before requiring them. Use a separate always-running, non-path-filtered required check for repository-wide policy, or ensure skipped workflows cannot leave a required status in **Pending**. In a single-service repository, always-running PR CI may be simpler and safer than path filters.

### Pipeline stages by technology

| Stack | Existing foundation | Additional gate to implement and measure |
|---|---|---|
| Java/Spring Boot | Java 21, Maven `verify` in most services | Configured style/lint, JaCoCo baseline if adopted, SCA + secret + SAST, contract checks |
| Python/FastAPI | `pytest`; `ruff` defined as dev dependency | Run `ruff check`/format, type checks if adopted, DB integration tests, coverage and security scan |
| Go | `gofmt`, `go vet`, `go build`, `go test -race` | Test/coverage policy, SCA/security scanning, cross-service contracts |
| Angular/React | `npm ci`, tests, production build | Explicit lint/type gates where scripts exist, coverage floor, npm dependency/secret checks |
| NGINX Gateway | Docker build, `nginx -t`, `smoke.sh` | Config lint/security scan and gateway integration checks |
| PostgreSQL/MongoDB `-db` | Migration application/rollback and role tests | Cross-version compatibility and deployment promotion checks |

Java version is **not uniform in every current repository**: the Patient Management workflow still uses Java 17. Keep each job consistent with its actual runtime and record the migration to the project's Java 21 standard as a separate story.

## 7. Cross-service contract testing

### Why individual unit tests are not sufficient

An API may pass all local tests and still break a downstream consumer. Example: `medical-consultation-api` changes the schema of `PostConsultationSummaryGenerated`; the Worker or Document Generation can then fail even though the producer builds successfully.

### Proposed compatibility matrix

| Producer → Consumer | Contract to verify | Example regression |
|---|---|---|
| Identity → other APIs | RS256 JWT claims, issuer, role, and service-token shape | Consumer rejects valid `SERVICE` token or accepts wrong role |
| Professional Management ↔ Identity | `/internal/accounts` and service token endpoints | Onboarding saga can no longer provision an account |
| Appointment Scheduling → Intelligent Agent | Appointment ownership and availability HTTP contract | Patient can start a pre-consultation on another patient's appointment |
| Medical Consultation → Worker | Outbox / RabbitMQ `PostConsultationSummaryGenerated` envelope | Changed payload breaks event processing |
| Worker → Document Generation | Generation request and service-token authorization | Worker receives unexpected body or `403` |
| Document Generation → Medical Consultation | Consultation summary read API | PDF source cannot be retrieved with authorized service identity |
| Notifications ↔ Identity | Notification recipient and password recovery event contracts | Recipient lookup or event decoding fails |

### Proposed gate procedure

1. Pin the provider and consumer source revisions (commit SHA / immutable image digest) used in the run.
2. Validate OpenAPI contracts under [`telemed-ia-docs/07-api/contracts/openapi`](https://github.com/code-corhuila/telemed-ia-docs/tree/main/07-api/contracts/openapi).
3. Start the required dependencies in **disposable** containers with synthetic data and isolated secrets.
4. Run negative authorization checks (missing JWT → `401`, wrong role or ownership → `403`, invalid contract → controlled `4xx`).
5. Verify that the worker can consume and transform a representative RabbitMQ event and that the callee accepts its service token.
6. Fail the integration workflow if any required provider/consumer combination fails. Save the results and exact tested image digests as artifacts.

The project already documents the testing pyramid in [Testing Strategy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/11-quality/testing-strategy.md). A **centrally orchestrated, mandatory cross-repo contract gate is proposed here**, not presented as already running.

## 8. Post-deployment smoke test

**Component-level smoke exists** in [`telemed-ia-api-gateway/tests/smoke.sh`](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/tests/smoke.sh). The MVP 3 target expands it to a **whole-platform** smoke after QA/release deployment:

- [ ] `GET /health` on the gateway responds successfully within a bounded timeout.
- [ ] Each required domain health endpoint is reachable through its supported route or internal monitor.
- [ ] A synthetic account can log in and obtain a signed token; invalid/missing token is rejected.
- [ ] An authenticated patient can access only their own protected resource.
- [ ] A representative inter-service request succeeds with the intended `SERVICE` token.
- [ ] A known synthetic event traverses RabbitMQ → Worker → its intended consumer and produces the expected status.
- [ ] No checked endpoint exposes credentials or medical data in errors or logs.
- [ ] At least one smoke failure returns a non-zero exit status and stops the release; rollback instructions are linked.

**Existing gateway smoke entry point:**

```bash
# In the telemed-ia-api-gateway repository, with its test stack running:
bash tests/smoke.sh
```

**Important:** Running the gateway's smoke script **does not prove** the end-to-end clinical journey. The system-wide test needs a dedicated runner and a reproducible Compose environment.

## 9. MVP 3 backlog — implementation slices

These are **proposed planning IDs**, not claims that the issues already exist in GitHub. Create the corresponding backlog items and assign an owner before work begins.

Risk priority: **P0** blocks an enforceable MVP 3 pipeline; **P1** is required for a dependable release; **P2** improves automation and performance.

| ID | Priority | Story / deliverable | Testable acceptance criterion (AC) |
|---|---|---|---|
| CI-01 | P0 | Restore GitHub Actions execution and collect proof | A PR workflow runs to completion; success/failure statuses appear on the PR |
| CI-02 | P0 | Protect all code `develop`, `qa`, `main` branches | A deliberately failing PR cannot merge; direct pushes and force pushes are rejected |
| CI-03 | P0 | Make core PR quality checks required | Build, lint/format, relevant type check, unit and HTTP tests return blocking statuses |
| CI-04 | P0 | Block secrets and critical dependencies | A seeded test secret or unapproved critical CVE fails the PR; no real secret is used |
| CI-05 | P1 | Enforce per-service coverage policy | A test fixture that drops coverage below the approved baseline causes CI failure |
| CI-06 | P1 | Add contract compatibility suite | A breaking OpenAPI/event mutation fails provider-consumer compatibility checks |
| CI-07 | P1 | Use safe path filters / selective jobs | A docs-only change skips expensive jobs without blocking required merge checks; code changes run affected tests |
| CI-08 | P1 | Publish release images by version/digest | Approved release produces a traceable immutable image with version, commit SHA and digest |
| CI-09 | P1 | Smoke test deployed QA system | A deliberately unavailable required service makes the smoke job fail with useful logs |
| CI-10 | P1 | Promote validated images across environments | Promotion reuses the **same digest**, not a rebuild of unverified source |
| CI-11 | P1 | Enforce branch audit and release approvals | PR links `cherry-pick -x` origin, QA result, reviewer(s), teacher approval for `main` |
| CI-12 | P2 | Add full delivery metrics | Pipeline records duration, pass/fail rate, deployment lead time and recovery evidence |

### Recommended implementation order

```text
CI-01 → CI-02 → CI-03 + CI-04
                     ↓
               CI-05 + CI-06
                     ↓
               CI-07 + CI-09
                     ↓
               CI-08 + CI-10
                     ↓
                    CI-11
                     ↓
                    CI-12
```

**Owners and proof:** For every story, assign a repository owner and a reviewer, open a PR (no direct permanent-branch commit), attach a failing-then-passing test or a configuration screenshot where applicable, and link the CI run and reviewed commit.

## 10. Release evidence required for MVP 3

Before calling the final release **pipeline-certified**, collect:

- [ ] The final DoD-to-gate matrix with a link to each automated check or a documented manual exception.
- [ ] GitHub ruleset evidence for **each** protected code branch and docs `main`.
- [ ] PR checks that were actually executed and green (not only declared in YAML).
- [ ] CI reports: test counts, failed/skipped cases, coverage baseline, scan findings, contract compatibility.
- [ ] Release PR with the approved user stories, `cherry-pick -x` trail, `@ariel5253` approval, tag and image digest.
- [ ] Deployment record indicating environment, image digest, config revision, operator/automation and timestamp.
- [ ] System smoke log and rollback procedure validated on non-production data.
- [ ] Explicit residual risks / exclusions recorded as technical debt.

**Definition of success:** No high-risk change enters a protected branch without executable evidence; QA exercises the same image that will be released; a failing gate or smoke test blocks promotion; and an audited rollback path is available.

## 11. Risks, dependencies and next week

| Risk / dependency | Consequence | Mitigation / owner action |
|---|---|---|
| Actions billing restriction | Required checks may not run | Organization administrator restores execution; capture proof before enforcing |
| GitHub rules access not verified | Policy may exist only on paper | Repository administrator reviews and screenshots rulesets; test rejection behavior |
| Missing container registry/hosted environment | Cannot claim automatic image deployment | Choose a registry and deployment target, implement immutable-digest promotion |
| Missing cross-repo test orchestration | Consumer breaks after a green provider build | Build a disposable integration runner with exact version pinning |
| Path-filter skipped required workflow | PR stays pending or silently misses a gate | Always-running required aggregator; safe filter design and branch tests |
| Incomplete integration dependencies | Test reported skipped despite required behavior | Provision test services; make critical integration tests mandatory for QA |
| Drift between documentation and code | Wrong CI status or tool version in release docs | Verify against workflow files and actual check runs at release time |

**Next week:** expand automated testing at scale (repeatable integration fixtures, cross-service negative tests, performance/reliability probes and controlled failure scenarios). Then promote a versioned, validated MVP 3 release only when all required checks, approvals and smoke evidence are present.

## 12. Evidence and references

Project documents:

1. [Definition of Done](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/definition-of-done.md)
2. [Branching Policy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/branching-policy.md)
3. [Git Conventions](https://github.com/code-corhuila/telemed-ia-docs/blob/main/00-governance/git-conventions.md)
4. [CI/CD documentation](https://github.com/code-corhuila/telemed-ia-docs/blob/main/10-devops/ci-cd.md)
5. [Deployment architecture](https://github.com/code-corhuila/telemed-ia-docs/blob/main/05-architecture/deployment.md)
6. [Testing Strategy](https://github.com/code-corhuila/telemed-ia-docs/blob/main/11-quality/testing-strategy.md)
7. [OpenAPI contracts](https://github.com/code-corhuila/telemed-ia-docs/tree/main/07-api/contracts/openapi)

Representative implementation files:

8. [API Gateway CI](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/.github/workflows/ci.yml)
9. [Identity & Access CI](https://github.com/code-corhuila/telemed-ia-identity-and-access-api/blob/develop/.github/workflows/ci.yml)
10. [Workflow CI](https://github.com/code-corhuila/telemed-ia-workflow/blob/develop/.github/workflows/ci.yml)
11. [Frontend CI](https://github.com/code-corhuila/telemed-ia-front/blob/develop/.github/workflows/ci.yml)
12. [Appointment Scheduling CI](https://github.com/code-corhuila/telemed-ia-appointment-scheduling-api/blob/develop/.github/workflows/ci.yml)
13. [Medical Consultation CI](https://github.com/code-corhuila/telemed-ia-medical-consultation-api/blob/develop/.github/workflows/ci.yml)
14. [Gateway smoke script](https://github.com/code-corhuila/telemed-ia-api-gateway/blob/develop/tests/smoke.sh)
15. [Document Generation contract dependency](https://github.com/code-corhuila/telemed-ia-document-generation-api/blob/develop/document-generation-app/pom.xml)
16. [Professional Management contract dependency](https://github.com/code-corhuila/telemed-ia-professional-management-api/blob/develop/professional-management-app/pom.xml)

---

**Conclusion:** TeleMed IA has substantial CI foundations and a written DoD/branch policy. For MVP 3, the remaining task is to **turn requirements into running, blocking, observable gates**, prove branch enforcement, coordinate contracts across repositories, and release the **same verified image** through a repeatable deployment-and-smoke process. This README is the implementation plan and evidence inventory; it does **not** claim that proposed controls have already been deployed.
