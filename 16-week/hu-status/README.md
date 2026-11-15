<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       16-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 16

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Maria Juliana Ferro Bonilla
- GITHUB_USER: JulianaFerro1428
- TEAM: Telemed ai - Group 2
- SPRINT_GOAL: Project defense week: present and defend TeleMed IA (release `v3.0.0` running from `main`) before the course.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|

No user stories this week: week 16 is the week in which the project is presented and defended.

## 2. My individual contribution
- Presentation and defense of TeleMed IA and of the parts I built (Identity & Access, Intelligent Agent, Notifications and the HU-05 onboarding saga).

## 3. Blockers and risks
- None.

## 4. Plan for next week
- None: the course ends with the project defense.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [ ] No secrets; config via environment variables

## 6. Evidence links
- Week folder: https://github.com/JulianaFerro1428/sistemas-distribuidos-2026-b-g2/tree/main/16-week

## 7. Course conclusion: what I learned and what I built

Sixteen weeks ago TeleMed IA was a scope document and a list of user stories. Today it is a telemedicine
platform with an intelligent pre-consultation agent, split into independent services that own their data,
talk through a gateway and a message broker, validate the same RS256 tokens, record their own traces and
metrics, and move from `develop` to `qa` (and now to `main`) through reviewed and traceable pull requests.
This section is my closing reflection on that journey: what I did, what I learned, what I would change and
what is still honestly open.

### 7.1 The semester in numbers

| Measure | Value |
|---|---|
| Pull requests I opened in the `code-corhuila` organization | **697** (687 merged), in **24** repositories |
| Repositories with most of my work | identity-and-access-api (142), telemed-ia-docs (86), identity-and-access-db (62), intelligent-agent-api (60), front (52), intelligent-agent-portal (48), notifications-api (45), workflow (34) |
| First organization PR | 2026-09-17, `docs(microservices): define TeleMed IA service catalog` |
| Domains I own end to end | Identity & Access, Intelligent Agent, Notifications and the Workflow (saga orchestrator) |
| Final full-system runs from `qa` (2026-11-11) and from `main` (2026-11-15) | 74 checks passed, 0 failed, 1 not applicable (HU-07), both times |
| Certification C3 (HCCDP Solution Architectures) | Chapters 1–14 studied and logged in [certification](../../certification/README.md) |

The numbers are not the point by themselves; small pull requests were a rule of the course (at most 400 lines,
one story each), so many PRs mean many small, reviewable steps rather than a few large ones.

### 7.2 The journey, phase by phase

**Weeks 1–5: foundations and the first monolith.** We defined the product scope, the eight business domains
(Identity & Access, Patient Management, Professional Management, Intelligent Agent, Appointment Scheduling,
Medical Consultation, Notifications, Document Generation) and the first ADRs. I worked on the domain model,
the data documentation and the appointment flow (availability, booking, my appointments) inside a modular
monolith in `telemed-ai`. The lesson of this phase: drawing boundaries on paper is easy, keeping them when the
code grows is the real work.

**Weeks 6–7: the intelligent agent (HU-06).** I built the conversational pre-consultation with ports and
adapters so the AI provider could be replaced, a controlled number of questions to limit tokens, session and
inactivity control, and the Angular/Ionic chat. The most important design decision was a clinical one: the
agent collects and organizes what the patient reports and **never** diagnoses, prescribes or recommends doses.
That rule followed the agent all the way to week 15, where the AI writes only the "reason for the visit" in the
patient PDF and never rewrites the professional's record (ADR-008).

**Weeks 8–10: migration to microservices and the identity lifecycle.** I split Identity & Access into its own
API and database with a hexagonal structure (core, adapters, application), UUID identifiers, a common error
envelope with correlation ids, Liquibase migrations owned only by the `-db` repository, and least-privilege
database roles. Authentication moved from HS256 to **RS256**, with rotated and hashed refresh tokens, logout,
and password recovery with single-use, expiring, concurrency-safe tokens. The professor's correction to use
**one instance per engine** (ANEXO-J) changed how I understood "database per service": ownership is logical
(schema and user per domain), not one server per service. I also built the shared MongoDB replica set and the
agent database, and published the first `2.0.0` component releases.

**Weeks 11–13: making the system observable and secure.** For week 11 I mapped the HU-06 journey across
services, defined the JSON log contract with `trace.id`/`span.id`, three SLOs with an error budget, burn-rate
alerts, a runbook and an incident drill with a blameless postmortem. The drill taught me something no unit test
would: an availability alert fired only at minute 40 for a slow database, so latency needs its own alert. In
week 12 I delivered the identity portal, account administration, the notifications service with SMTP and the
real agent API with Groq, and promoted them to `qa`. In week 13 I threat-modeled the public login boundary with
STRIDE and turned the top risk into a story built with TDD: the per-account login lock that answers `429`.

**Weeks 14–15: closing the MVP for real.** I brought the week 11 observability from a prototype to the real
services (spans to the platform collector, metrics, SLO alerts evaluated by the platform Prometheus), finished
the agent outbox relay, made registration idempotent, and then built **HU-05**: a custom Java saga orchestrator
(Anexo E) that creates the account, creates the professional profile and activates the account, compensates in
reverse order, keeps its state in PostgreSQL and recovers interrupted sagas with a scheduled sweep. Then I ran a
full-system test from `qa`, found the defects that only appear when everything runs together, and fixed them
with the team: UUID handling in the shell, missing gateway routes, portals losing their theme inside the shell,
consultations that no API created, and `ConsultationCompleted`, which existed only on paper. The result was the
complete patient journey working end to end: booking → AI pre-consultation → attention → appointment completed
automatically → patient PDF generated automatically. Finally, I promoted every repository to `qa`, released all of them to
`main` and tagged them `v3.0.0`.

### 7.3 What I learned

**1. A distributed system is defined by its boundaries, not by its number of services.** Splitting code into
repositories is the easy part. The hard part is deciding who owns each piece of data, who may write it, and how
the others learn about changes. Rules like "each domain writes only to its schema with its `<domain>_app` user"
and "no foreign keys across schemas" looked bureaucratic at first; by the end they were what let me change one
service without breaking another.

**2. Contracts are a product.** camelCase JSON, UUIDs, RFC 3339 dates, `{data, meta}` pagination, the error
envelope with `traceId`, `Idempotency-Key` and `X-Correlation-Id` are boring individually, but together they
are what lets the services and portals of different teammates understand each other. Many defects I fixed in
week 15 were contract drift: a portal reading a raw object while the API wrapped it in `{data}`, a numeric id
where a UUID was expected, a route the gateway did not know.

**3. Reliable messaging needs more than a broker.** I learned why the **outbox** pattern exists (write the
event in the same transaction as the business change, publish it later with confirms), why consumers must be
**idempotent** (the same message can arrive twice), why every queue needs a **DLQ**, and why a defined event is
not proof that anything publishes or consumes it. "Documented" and "working" are different states, and I
learned to say which one is true.

**4. Distributed transactions are sagas, and sagas are about failure.** Building HU-05 showed me that the happy
path is the smallest part of a saga. Most of the design went into compensations, idempotent starts, retries,
what happens when the process dies between two steps, and how to recover a saga that a restart left running.

**5. Security is a set of small, verifiable controls.** RS256 so that only identity holds the private key and
every service validates tokens; hashed and rotated refresh tokens; BCrypt with its 72-byte limit; generic error
messages so login does not reveal which accounts exist; a login lock against brute force; secrets only in
environment variables and never in the repository. STRIDE gave me a method to find the risks; TDD gave me a way
to prove each control works (the lock test went from `200` red to `429` green).

**6. Observability is designed, not added.** Trace context has to travel through HTTP headers and through the
outbox and the broker, logs need a contract, and SLOs only matter if alerts, a runbook and a drill exist. The
traces in Jaeger were the fastest way to understand what my own system was really doing.

**7. Tests tell the truth only about what they run.** TDD with a separate failing-test commit made my intent
visible in the history. But unit tests did not catch a Docker image that no longer built, a portal that did not
publish its port, or a shell that broke every remote with UUIDs. Building the images and running the journeys
end to end, through the gateway and in the browser, was the only real proof. With GitHub Actions blocked by the
organization's billing for most of the semester, I learned to run and cite local verification in every PR
instead of assuming CI would catch problems.

**8. Delivery discipline is what makes a team scale.** Conventional Commits, short branches, PRs of at most 400
lines linked to a user story, reviews, promotion only with `git cherry-pick -x`, and never merging permanent
branches into each other. Promoting about a hundred PRs to `qa` with the final tree checked against `develop`
showed me why traceability matters: every change in `qa` can be traced back to the story and the PR that
produced it.

**9. Decisions belong in writing before the code.** ADRs and DEC records first, code after. When two sources
contradicted each other, the right move was to record the contradiction as an open question for the team, not
to pick a side silently. The documentation repository ended the course aligned with what is actually built,
including user and administrator manuals, the real observability and the incident playbook.

**10. Working with AI tools requires judgment.** I used an AI assistant heavily during the semester. It made
me faster, but the responsibility stayed mine: verifying claims against the code and the remote repository,
refusing to mark something done without evidence, keeping secrets out of every prompt and commit, and
understanding every change well enough to defend it.

### 7.4 What I would do differently

- **Integrate earlier.** I validated components one by one for weeks; the full-system test in week 15 found
  defects that had been there since week 10. Next time I would run the whole platform from the first service.
- **Make CI independent of a single account.** The blocked GitHub Actions turned every check into a manual
  step. A self-hosted runner or a local script that reproduces the pipeline would have kept the gates real.
- **Agree on contracts with the consumers before building.** Many fixes were mismatches between a portal and an
  API built by different people at different times; contract tests would have caught them.
- **Decide the saga approach earlier.** Choosing a custom orchestrator over Camunda was right for the course
  rules, but deciding it earlier would have saved a superseded ADR.

### 7.5 What is still open

I want this conclusion to be as honest as the weekly reports, so these items are **not** done at the time of
writing:

- A hosted deployment, a shared image registry and automatic deployment of tagged images (week 14 target).
- Blocking security gates in CI (secret, dependency and SAST scanning) and a universal coverage floor.
- HU-07 had no applicable check in the final end-to-end run.
- The open questions Q-009 to Q-016 recorded in telemed-ia-docs project control, for the team to decide.

### 7.6 Closing thoughts

The most valuable thing this course gave me is not a specific technology but a way of thinking: a distributed
system is a set of agreements (who owns the data, what the contracts say, how failures are handled, how changes
are promoted) and every agreement must be written down, tested and observable. I started the semester writing
code that worked on my machine; I finish it able to explain why a service owns its schema, how an event survives
a crash, how a saga undoes its steps, how a token is verified without sharing a secret, and how to prove all of
that with evidence. TeleMed IA is the result of a team, and I am proud of the part I built in it: the identity
of every user, the agent that prepares every consultation, the notifications that reach every patient and the
saga that onboards every professional.
