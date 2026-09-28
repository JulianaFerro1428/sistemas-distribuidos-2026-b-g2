# Week 08 — MVP 2 Planning Implementation

## TeleMed-AI — Story Mapping, Estimation & Cross-Service Planning

## 1. Overview

This activity applies product planning directly to the **TeleMed-AI distributed architecture**.

After strengthening the Identity & Access foundation during the sprint, the next step is to define a realistic **MVP 2** that connects the main patient journey across multiple services.

The planning process focuses on:

- Building a product story map.
- Identifying the thinnest end-to-end MVP 2 slice.
- Estimating stories using Planning Poker.
- Detecting cross-service dependencies.
- Defining API contracts before implementation.
- Using mocks and stubs to avoid blocking teams.
- Sequencing stories according to dependencies.
- Committing only the work that fits the sprint capacity.
- Keeping the scope aligned with the MVP 2 Sprint Goal.

The main principle is:

> **Build an integrated patient journey, not a collection of disconnected features.**

---

# 2. MVP 2 Sprint Goal

The proposed Sprint Goal for TeleMed-AI MVP 2 is:

> **Deliver an integrated patient pre-consultation flow where an authenticated patient can start a pre-consultation, interact with the intelligent agent, persist the collected information, check professional availability, schedule an appointment, and generate information that can later be consumed by the healthcare professional.**

MVP 2 therefore focuses on two major technical capabilities:

1. **Communication between services.**
2. **Persistent end-to-end data flow.**

The objective is not to complete every possible TeleMed-AI feature.

The objective is to demonstrate that the main services can communicate and preserve the patient's information through a usable end-to-end journey.

---

# 3. Product Story Map

The TeleMed-AI story map is organized around the patient's journey.

The horizontal axis represents the main activities performed by the patient.

The vertical axis represents the stories required to support each activity.

| Priority | Access TeleMed-AI | Start Pre-Consultation | Intelligent Agent | Check Availability | Schedule Appointment | Clinical Summary |
|---|---|---|---|---|---|---|
| **Must** | Register patient | Create pre-consultation | Collect reason for consultation | Query available slots | Select appointment slot | Persist collected information |
| **Must** | Authenticate patient | Associate patient identity | Collect relevant answers | Receive availability response | Create appointment | Generate consultation summary |
| **Must** | Validate access token | Persist pre-consultation | Evaluate urgency level | Handle unavailable slots | Persist appointment | Associate summary with patient |
| **Should** | Refresh session | Resume pre-consultation | Ask optional follow-up questions | Filter professionals | Reschedule appointment | Professional-ready formatting |
| **Could** | Remember session | Draft recovery | Personalized questions | Advanced filters | Notifications | Extended clinical metadata |

---

# 4. MVP 2 Release Line

The release line defines the **thinnest usable end-to-end path** for MVP 2.

```text id="x3zv4r"
Patient
   ↓
Registration / Authentication
   ↓
Authenticated identity
   ↓
Start Pre-Consultation
   ↓
Intelligent Agent Interaction
   ↓
Persist Collected Information
   ↓
Evaluate Urgency
   ↓
Check Availability
   ↓
Select Slot
   ↓
Create Appointment
   ↓
Persist Appointment
   ↓
Generate Summary
```

Everything required for this path is considered part of the MVP 2 candidate scope.

Features below the release line remain in the backlog.

Examples:

```text id="d5gq2c"
Advanced profile customization
Advanced professional filters
Multiple notification channels
Optional conversational questions
Advanced analytics
UI personalization
Additional clinical metadata
```

This prevents the team from spending sprint capacity on secondary features before the integrated patient journey works.

---

# 5. Existing Foundation

MVP 2 does not start from zero.

The current sprint already established part of the required foundation.

## Identity & Access

Completed work includes:

- Patient registration.
- Authentication core.
- Access-token generation.
- Login REST API.
- BCrypt password handling.
- Generic invalid-credentials behavior.
- API error standardization.
- Correlation ID handling.
- Modular API architecture.
- PostgreSQL persistence.
- UUID evolution.
- Token persistence.
- Database access-control hardening.

These capabilities are prerequisites for the patient flow planned for MVP 2.

---

# 6. Planning Poker

Stories are estimated using **relative sizing**, not hours.

The scale used is:

```text id="7r5nnm"
1 — Very small
2 — Small
3 — Moderate
5 — Significant
8 — Large / uncertain
13 — Too large / should be split
```

The estimate considers:

- Complexity.
- Amount of implementation work.
- Testing effort.
- Technical uncertainty.
- Number of repositories affected.
- Cross-service communication.
- Persistence changes.
- Contract changes.
- Integration risk.

---

# 7. Planning Poker Process

For each MVP 2 story, the team should follow this process:

```text id="q7cu9i"
1. Read the user story
        ↓
2. Review acceptance criteria
        ↓
3. Identify dependencies
        ↓
4. Each member selects a point value
        ↓
5. Reveal estimates simultaneously
        ↓
6. Discuss highest and lowest estimates
        ↓
7. Clarify assumptions
        ↓
8. Vote again if necessary
        ↓
9. Record the agreed estimate
```

The purpose of Planning Poker is not simply to obtain a number.

The most important result is the discussion about hidden complexity and dependencies.

---

# 8. MVP 2 Story Estimates

The following estimates represent the planning baseline for the MVP 2 activity.

| ID | Story | Priority | Estimate |
|---|---|---|---:|
| BASE-01 | Patient registration | Completed foundation | Done |
| BASE-02 | Patient authentication | Completed foundation | Done |
| MVP2-01 | Propagate authenticated patient identity across services | Must | 3 |
| MVP2-02 | Start and persist a patient pre-consultation | Must | 5 |
| MVP2-03 | Execute the intelligent-agent pre-consultation flow | Must | 5 |
| MVP2-04 | Evaluate and persist urgency information | Must | 3 |
| MVP2-05 | Query professional availability | Must | 3 |
| MVP2-06 | Create and persist an appointment | Must | 5 |
| MVP2-07 | Generate the professional consultation summary | Should | 3 |
| MVP2-08 | Resume an unfinished pre-consultation | Should | 5 |
| MVP2-09 | Add advanced availability filters | Could | 3 |
| MVP2-10 | Send appointment notifications | Could | 5 |

The values should be confirmed by the full team during Planning Poker before becoming final sprint estimates.

---

# 9. Large Story Rule

Any story estimated at **8 or more points** should be reviewed before entering the sprint.

For example, this story would be too large:

```text id="gd6pcw"
Implement the complete pre-consultation system
Estimate: 13
```

Instead, it should be split into smaller independently testable stories:

```text id="yql35u"
MVP2-02
Start and persist a pre-consultation
5 points

MVP2-03
Execute the intelligent-agent conversation
5 points

MVP2-04
Evaluate and persist urgency
3 points
```

This makes the work easier to:

- Develop.
- Review.
- Test.
- Integrate.
- Demonstrate.
- Complete within the sprint.

---

# 10. Cross-Service Dependency Map

TeleMed-AI MVP 2 requires communication between several logical components.

```text id="uljzat"
                    ┌───────────────────────┐
                    │   Identity & Access   │
                    │ register / login / JWT│
                    └──────────┬────────────┘
                               │
                               │ authenticated identity
                               ▼
                    ┌───────────────────────┐
                    │      API Gateway      │
                    │ validation / routing  │
                    └──────────┬────────────┘
                               │
                               │ X-User-Id
                               │ X-User-Role
                               │ X-Correlation-Id
                               ▼
                 ┌────────────────────────────┐
                 │      Pre-Consultation      │
                 │ patient interaction state  │
                 └────────────┬───────────────┘
                              │
                              ▼
                 ┌────────────────────────────┐
                 │      Intelligent Agent     │
                 │ questions / urgency flow   │
                 └────────────┬───────────────┘
                              │
                              ▼
                 ┌────────────────────────────┐
                 │      Persistence Layer     │
                 │ consultation information   │
                 └────────────┬───────────────┘
                              │
                              ▼
                 ┌────────────────────────────┐
                 │ Availability / Scheduling  │
                 │ slots / appointments       │
                 └────────────┬───────────────┘
                              │
                              ▼
                 ┌────────────────────────────┐
                 │    Consultation Summary    │
                 │ professional information   │
                 └────────────────────────────┘
```

---

# 11. Main Dependencies

| Consumer | Dependency | Provider | Risk |
|---|---|---|---|
| API Gateway | Access-token validation | Identity & Access | High |
| Pre-Consultation | Authenticated patient identity | Gateway / Identity | High |
| Intelligent Agent | Active pre-consultation context | Pre-Consultation | High |
| Persistence | Patient and consultation identifiers | Identity / Pre-Consultation | High |
| Pre-Consultation | Availability information | Scheduling | High |
| Scheduling | Patient identity | Gateway / Identity | Medium |
| Scheduling | Pre-consultation context | Pre-Consultation | Medium |
| Clinical Summary | Persisted consultation data | Pre-Consultation / Persistence | Medium |

The High-risk dependencies must be handled early in the sprint.

---

# 12. Contract-First Strategy

Cross-service development follows a **contract-first** approach.

The contract must be agreed before both services depend on the final implementation.

For example:

```text id="90xah4"
Pre-Consultation
       │
       │ GET /api/availability
       ▼
Scheduling
```

Before implementing the complete Scheduling service, both sides should agree on:

- Endpoint.
- HTTP method.
- Request parameters.
- Response fields.
- Status codes.
- Error format.
- Authentication requirements.
- Correlation headers.

---

# 13. Shared API Conventions

The current TeleMed-AI API conventions already provide a common foundation.

Current API base paths use:

```text id="mtu7jk"
/api/...
```

Authentication routes follow:

```text id="vu5tff"
/api/auth/...
```

Internal identity propagation uses:

```text id="9r5h44"
X-User-Id
X-User-Role
X-Correlation-Id
```

Expected technical roles include:

```text id="txh06c"
PATIENT
PROFESSIONAL
ADMIN
```

The shared error structure follows:

```json id="5na3q9"
{
  "error": "ERROR_CODE",
  "message": "Human-readable message",
  "details": [],
  "traceId": "correlation-id"
}
```

Keeping these conventions stable reduces integration uncertainty between services.

---

# 14. Contract Example — Availability

A possible availability contract can be defined before the final Scheduling implementation.

```http id="dch3d2"
GET /api/availability?date=2026-10-05
```

Expected response:

```json id="82e7an"
{
  "date": "2026-10-05",
  "slots": [
    {
      "professionalId": "uuid",
      "startTime": "09:00",
      "available": true
    }
  ]
}
```

Possible responses:

```text id="acbbfa"
200 — Availability returned
400 — Invalid request
401 — Authentication required
422 — Domain validation error
503 — Scheduling service unavailable
```

The exact contract must be stored in the shared OpenAPI documentation before integration.

---

# 15. Mock-First Development

If the Scheduling service is not ready, the Pre-Consultation work should not stop.

The agreed contract can be mocked.

```text id="hkdhzi"
Pre-Consultation
        │
        ▼
Mock Availability API
        │
        ▼
Known availability response
```

This allows the consumer to implement and test its behavior independently.

Later:

```text id="l4zrej"
Pre-Consultation
        │
        ▼
Real Scheduling Service
```

As long as both follow the same contract, integration should require minimal changes.

---

# 16. Avoiding a Dependency Deadlock

The team must avoid this situation:

```text id="y51xqk"
Pre-Consultation team:
"We cannot continue until Scheduling exists."

Scheduling team:
"We cannot continue until we know what
Pre-Consultation expects."
```

This is resolved with:

```text id="q35ot0"
Agree contract
      ↓
Publish OpenAPI contract
      ↓
Create mock
      ↓
Implement consumer
      +
Implement provider
      ↓
Contract validation
      ↓
Real integration
```

This enables parallel development.

---

# 17. Recommended Work Sequence

The planned implementation order is:

## Step 1 — Identity Foundation

Already available:

- Patient registration.
- Authentication.
- Access token.
- Patient identifier.
- Roles.

---

## Step 2 — Gateway Identity Propagation

Validate the access token and propagate:

```text id="gnfv16"
X-User-Id
X-User-Role
X-Correlation-Id
```

This provides a common identity context for downstream services.

---

## Step 3 — Pre-Consultation Creation

Implement:

```text id="3j6ocb"
Authenticated patient
      ↓
POST pre-consultation
      ↓
Create consultation identifier
      ↓
Persist initial state
```

---

## Step 4 — Intelligent Agent Flow

The agent collects:

- Reason for consultation.
- Relevant answers.
- Follow-up information.
- Urgency information.

The agent assists with pre-consultation but does not provide a medical diagnosis.

---

## Step 5 — Persistence

Persist:

```text id="2i1t8r"
patientId
consultationId
responses
urgency
status
timestamps
```

This ensures the consultation state is not lost between service interactions.

---

## Step 6 — Availability Contract

Define the Scheduling contract before implementation.

Provide a mock response so Pre-Consultation development can continue.

---

## Step 7 — Scheduling Integration

Replace the mock with the real Scheduling implementation.

Validate:

```text id="4fgpv7"
Pre-Consultation
      ↓
Availability request
      ↓
Scheduling
      ↓
Available slots
      ↓
Patient selects slot
      ↓
Appointment created
```

---

## Step 8 — Consultation Summary

Generate a structured summary from persisted pre-consultation information.

The summary becomes the input available to the healthcare professional.

---

# 18. Dependency Ownership

Dependencies must be tracked explicitly.

| Dependency | Owner | Needed By | Target |
|---|---|---|---|
| Authentication contract | Identity & Access | Gateway | Early sprint |
| Internal identity headers | Gateway | All downstream services | Early sprint |
| Pre-consultation contract | Pre-Consultation | Agent / Persistence | Early sprint |
| Availability contract | Scheduling | Pre-Consultation | Before scheduling implementation |
| Appointment contract | Scheduling | Patient flow | Before integration |
| Error contract | Shared API Docs | All services | Continuous |
| Correlation ID convention | Gateway / Services | Observability | Continuous |

A dependency is not considered managed if it only exists in someone's memory.

---

# 19. Velocity and Planning Capacity

Story points and Pull Requests measure different things.

Therefore:

> **PR throughput should not be converted directly into story-point velocity.**

The previous sprint provides useful throughput evidence, but MVP 2 capacity should be confirmed using the team's actual Planning Poker estimates and completed sprint history.

For this planning activity, the proposed commitment is intentionally kept to a small number of Must stories.

The current proposed MVP 2 commitment totals:

```text id="jb6uxm"
MVP2-01 = 3
MVP2-02 = 5
MVP2-03 = 5
MVP2-04 = 3
MVP2-05 = 3
MVP2-06 = 5

----------------
Total = 24 story points
```

This **24-point scope is a planning baseline**, not a fabricated historical velocity.

If the team's recorded velocity differs, the committed scope must be adjusted before the sprint starts.

---

# 20. MVP 2 Committed Scope

The proposed MVP 2 commitment contains only the Must stories required for the end-to-end journey.

| ID | Story | Points | Commitment |
|---|---|---:|---|
| MVP2-01 | Propagate authenticated patient identity | 3 | Commit |
| MVP2-02 | Start and persist pre-consultation | 5 | Commit |
| MVP2-03 | Execute intelligent-agent flow | 5 | Commit |
| MVP2-04 | Evaluate and persist urgency | 3 | Commit |
| MVP2-05 | Query professional availability | 3 | Commit |
| MVP2-06 | Create and persist appointment | 5 | Commit |
|  | **Total** | **24** | |

This scope provides the minimum integrated path required by the Sprint Goal.

---

# 21. Stories Kept Outside the Commitment

The following stories remain in the backlog unless capacity becomes available:

| Story | Priority | Points |
|---|---|---:|
| Generate enhanced professional summary | Should | 3 |
| Resume unfinished pre-consultation | Should | 5 |
| Advanced availability filters | Could | 3 |
| Appointment notifications | Could | 5 |
| Additional UI improvements | Could | TBD |
| Advanced analytics | Could | TBD |

These stories should not be started while committed Must stories remain incomplete.

---

# 22. Capacity Buffer

A small capacity buffer should be protected for:

- Integration problems.
- Contract corrections.
- Review findings.
- Migration issues.
- CI failures.
- Unexpected cross-service behavior.

If the team's confirmed velocity is exactly equal to the selected scope, one Must story should be reconsidered instead of assuming that every planned point will execute perfectly.

---

# 23. Real-Life Risk Scenario

A realistic risk for TeleMed-AI is:

```text id="ghrkzn"
Pre-Consultation implementation starts
        ↓
Scheduling contract is not defined
        ↓
Pre-Consultation assumes a response structure
        ↓
Scheduling implements a different structure
        ↓
Integration fails
        ↓
Both teams rewrite code
        ↓
Sprint goal is at risk
```

The preventive approach is:

```text id="jl3zyc"
Contract first
      +
OpenAPI
      +
Mock
      +
Parallel implementation
      +
Contract validation
```

---

# 24. Planning Definition of Ready

Before an MVP 2 story enters development, it should satisfy:

- [ ] User or technical value is clear.
- [ ] Acceptance criteria are testable.
- [ ] Priority is defined.
- [ ] Planning Poker estimate exists.
- [ ] Story is small enough for the sprint.
- [ ] Dependencies are identified.
- [ ] Dependency owner is known.
- [ ] API contract exists when required.
- [ ] Mock/stub strategy exists when the provider is unavailable.
- [ ] Database impact is understood.
- [ ] Expected error behavior is defined.
- [ ] Story contributes directly to the Sprint Goal.

---

# 25. Planning Evidence

The existing TeleMed-AI work already provides evidence supporting this planning model.

## Identity & Access API

Authentication foundation:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/652c2b838e92c52d674c4e574e1f308da1405132

Login REST API:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/31569e1ce75826f26014e8899a96759b4b61e876

Unified errors and correlation IDs:

https://github.com/code-corhuila/telemed-ia-identity-and-access-api/commit/265a9715910f7ef68cfcc123b7d3a1de7eed111c

---

## Shared API Documentation

Authentication contract alignment:

https://github.com/code-corhuila/telemed-ia-docs/commit/81f988e0e1c0ecf20e8c3cd549f41b9795882daf

Clinical field serialization:

https://github.com/code-corhuila/telemed-ia-docs/commit/6ae07df90a065344ff6083de415254ed1c4c7689

These contracts demonstrate the project's transition toward explicit **contract-first distributed-service development**.

---

## Identity & Access Database

The database work provides the persistence foundation required by later integrations, including:

- Liquibase-managed migrations.
- Runtime validation.
- UUID identifiers.
- Token persistence.
- Database access control.
- Migration rollback hardening.

This reduces the risk of defining an MVP 2 flow that cannot persist or correlate distributed information correctly.

---

# 26. Expected MVP 2 Result

If the committed stories reach Done, the MVP should demonstrate:

```text id="209zlj"
Patient registers / logs in
        ↓
Identity is propagated
        ↓
Patient starts pre-consultation
        ↓
Agent collects information
        ↓
Information is persisted
        ↓
Urgency is recorded
        ↓
Availability is requested
        ↓
Patient selects slot
        ↓
Appointment is persisted
```

This demonstrates the key MVP 2 objective:

> **Integrated communication + persistence across the TeleMed-AI distributed architecture.**

---

# 27. Results of the Planning Activity

The planning activity produces:

- [x] A patient-centered story map.
- [x] An MVP 2 release line.
- [x] Must / Should / Could prioritization.
- [x] Relative story-point estimates.
- [x] A Planning Poker process.
- [x] A rule for splitting large stories.
- [x] A cross-service dependency map.
- [x] A dependency sequence.
- [x] A contract-first integration strategy.
- [x] A mock/stub strategy.
- [x] Dependency ownership.
- [x] A 24-point proposed commitment.
- [x] Deferred Should/Could backlog.
- [x] A Definition of Ready for MVP 2.

---

# 28. Key Takeaway

> **Realistic planning beats hopeful planning.**

For TeleMed-AI, successful MVP 2 planning means:

```text id="5o8r9a"
Map the patient journey
        ↓
Estimate honestly
        ↓
Identify dependencies
        ↓
Define contracts first
        ↓
Use mocks to unblock work
        ↓
Sequence implementation
        ↓
Commit only what fits
        ↓
Deliver an integrated MVP
```

The goal is not to maximize the number of stories started.

The goal is to deliver a **small, complete, tested, persistent, and integrated TeleMed-AI patient journey**.