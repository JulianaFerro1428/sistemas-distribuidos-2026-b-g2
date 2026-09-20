# Planning — Versioned Contracts and Contract Testing

> This document defines how TeleMed IA will formalize, version, validate, and evolve communication contracts between services as the current modular monolith transitions toward independent microservices.

---

## 1. Objective

Independent services must be able to evolve without silently breaking their consumers.

TeleMed IA therefore defines machine-readable contracts for:

```text
REST APIs
Domain Events
Future gRPC interfaces
```

The main principle is:

```text
If it is not documented in the contract,
it is not part of the integration.
```

The authoritative REST contracts are maintained in:

```text
07-api/contracts/openapi/
```

---

## 2. Contract Types

TeleMed IA uses different contract formats depending on the communication mechanism.

| Communication | Contract Format | Current Use |
|---|---|---|
| REST | OpenAPI YAML | Primary API contract |
| gRPC | `.proto` | Future option |
| Domain Events | JSON Schema | Planned asynchronous contracts |

Examples:

```text
REST
↓
openapi.yaml

gRPC
↓
service.proto

Event
↓
appointment-created-v1.schema.json
```

---

## 3. REST Contract Structure

Every REST endpoint must define at least:

```text
HTTP method
Path
Request
Response
Errors
Version
```

Example:

```http
POST /api/v1/agent/conversations
```

A contract must specify:

- Request body.
- Required fields.
- Response body.
- HTTP status codes.
- Validation errors.
- Authentication requirements.
- Data types.
- API version.

---

## 4. API Versioning

TeleMed IA uses explicit API versions.

Example:

```text
/api/v1/agent/conversations
/api/v1/appointments
/api/v1/patients
```

The first stable contract is:

```text
v1
```

A compatible change may remain in the same version.

A breaking change requires a new version.

---

## 5. Backward-Compatible Changes

The following changes are normally safe:

```text
Add optional field
Add new endpoint
Add optional query parameter
Add new error code
```

Example:

### v1 before

```json
{
  "patientId": 25,
  "active": true
}
```

### Compatible addition

```json
{
  "patientId": 25,
  "active": true,
  "preferredLanguage": "es"
}
```

Existing consumers can continue reading:

```text
patientId
active
```

without modification.

---

## 6. Breaking Changes

The following changes are considered breaking:

```text
Rename a field
Remove a field
Change a field type
Change required → incompatible behavior
Change endpoint path
Change event meaning
```

Incorrect:

```json
{
  "patientId": 25
}
```

changed silently to:

```json
{
  "personId": 25
}
```

A consumer expecting:

```text
patientId
```

would fail.

---

## 7. Breaking Change Strategy

Breaking changes require a new contract version.

Example:

```text
/api/v1/patients/{id}
```

remains available while:

```text
/api/v2/patients/{id}
```

is introduced.

The transition becomes:

```text
v1
 │
 │ deprecated
 │
 ├───────────────┐
 ▼               ▼
old consumer   new consumer
                  │
                  ▼
                 v2
```

The old version must remain available until affected consumers have migrated.

---

## 8. Deprecation

Deprecated APIs should be clearly identified.

When applicable, HTTP responses may use:

```http
Deprecation: true
Sunset: <date>
```

Documentation must explain:

- Which version is deprecated.
- Its replacement.
- Migration instructions.
- Planned removal date.

---

## 9. Standard Error Envelope

TeleMed IA APIs should use a consistent error structure.

Recommended format:

```json
{
  "error": {
    "code": "APPOINTMENT_NOT_AVAILABLE",
    "message": "The selected appointment is no longer available.",
    "details": [],
    "traceId": "corr-145"
  }
}
```

Required fields:

```text
code
message
details
traceId
```

Internal stack traces must never be returned to the client.

---

## 10. Common Data Conventions

Contracts should follow common conventions.

### Dates

Use ISO-8601.

Example:

```text
2026-09-20T15:30:00Z
```

### Identifiers

Identifiers must use a consistent documented type.

### Pagination

When required:

```text
?page=0&limit=20
```

### Content Type

REST APIs use:

```text
application/json
```

unless another format is explicitly required.

---

## 11. TeleMed IA OpenAPI Contracts

The target contract structure is:

```text
07-api/contracts/openapi/
│
├── auth-service.yaml
├── patient-service.yaml
├── professional-service.yaml
├── agent-service.yaml
├── appointment-service.yaml
├── consultation-service.yaml
├── notification-service.yaml
└── document-service.yaml
```

The OpenAPI contract is the Source of Truth for exposed REST interfaces.

Implementation must remain consistent with the contract.

---

## 12. Intelligent Agent Contract Example

Example endpoint:

```http
POST /api/v1/agent/conversations/{conversationId}/messages
```

Request:

```json
{
  "message": "Tengo dolor abdominal desde ayer."
}
```

Response:

```json
{
  "conversationId": 15,
  "response": "¿El dolor ha sido constante o intermitente?",
  "status": "ACTIVA",
  "preconsultationComplete": false
}
```

The response contract must not suddenly rename:

```text
response
```

to:

```text
agentMessage
```

inside `v1`.

That would require either compatibility handling or a new version.

---

## 13. Event Contracts

Domain Events also require machine-readable contracts.

Example:

```text
AppointmentCreated
```

should have a schema such as:

```text
appointment-created-v1.schema.json
```

Example payload:

```json
{
  "eventId": "evt-123",
  "eventType": "AppointmentCreated",
  "eventVersion": 1,
  "occurredAt": "2026-09-20T15:20:00Z",
  "producer": "appointment-service",
  "correlationId": "corr-145",
  "payload": {
    "appointmentId": 145,
    "patientId": 25,
    "professionalId": 7
  }
}
```

---

## 14. Event Versioning

Events must include:

```text
eventType
eventVersion
```

Example:

```json
{
  "eventType": "AppointmentCreated",
  "eventVersion": 1
}
```

Adding an optional field may remain:

```text
v1
```

Changing the meaning or removing required information requires:

```text
v2
```

---

## 15. Event Compatibility Example

Compatible:

```json
{
  "appointmentId": 145,
  "patientId": 25,
  "professionalId": 7,
  "appointmentType": "GENERAL"
}
```

if:

```text
appointmentType
```

is optional.

Breaking:

```text
patientId
```

renamed to:

```text
personId
```

without changing the event version.

---

## 16. Consumer-Driven Contract Testing

Unit tests validate one service internally.

They do not guarantee that another service still understands its API.

TeleMed IA plans to use consumer-driven contract testing with Pact.

The flow is:

```text
Consumer
   │
   │ declares expectation
   ▼
Pact Contract
   │
   ▼
Producer Verification
   │
   ├── compatible → CI passes
   │
   └── breaking → CI fails
```

---

## 17. First Contract Test Candidate

A suitable first interaction is:

```text
agent-service
      │
      │ REST
      ▼
patient-service
```

The Intelligent Agent requires a limited amount of patient context.

The consumer is:

```text
agent-service
```

The producer is:

```text
patient-service
```

The consumer may expect:

```json
{
  "patientId": 25,
  "active": true
}
```

---

## 18. Pact Example

The consumer contract would express:

```text
Given:
patient 25 exists

When:
GET /api/v1/patients/25/context

Then:
200 OK
```

Expected response:

```json
{
  "patientId": 25,
  "active": true
}
```

If `patient-service` later changes:

```text
patientId
```

to:

```text
personId
```

the producer contract verification fails before deployment.

---

## 19. Pact Flow in CI

Target CI flow:

```text
agent-service
      │
      ▼
Generate consumer Pact
      │
      ▼
Publish / store contract
      │
      ▼
patient-service CI
      │
      ▼
Verify producer
      │
  ┌───┴────┐
  │        │
 PASS     FAIL
  │        │
  ▼        ▼
continue  block build
```

This prevents integration changes from silently breaking consumers.

---

## 20. Contract Testing Does Not Replace Other Tests

TeleMed IA still requires:

```text
Unit tests
Integration tests
Contract tests
End-to-end tests
```

Their purposes differ:

| Test | Purpose |
|---|---|
| Unit | Validate internal logic |
| Integration | Validate infrastructure integration |
| Contract | Validate service expectations |
| End-to-end | Validate complete user flow |

---

## 21. Contract Source of Truth

The expected documentation hierarchy is:

```text
Business Requirement
        ↓
User Story
        ↓
API / Event Contract
        ↓
Implementation
        ↓
Contract Test
```

The implementation must not redefine the contract independently.

---

## 22. Contract Change Workflow

When a contract must change:

```text
Need identified
      ↓
Review consumers
      ↓
Determine compatibility
      │
 ┌────┴────┐
 │         │
Safe     Breaking
 │         │
 ▼         ▼
Update    Create v2
v1        + deprecate v1
 │         │
 └────┬────┘
      ▼
Update contract
      ↓
Update tests
      ↓
Pull Request
      ↓
CI verification
```

---

## 23. Contract Rules for TeleMed IA

TeleMed IA adopts the following rules:

1. REST contracts are defined through OpenAPI.
2. Event contracts use versioned schemas.
3. gRPC contracts will use `.proto` if gRPC is adopted.
4. Contract files are version controlled.
5. A contract is updated before or together with implementation changes.
6. Optional additive fields are preferred.
7. Fields must not be silently renamed or removed.
8. Breaking changes require a new version.
9. Deprecated contracts remain available during migration.
10. Consumers must be identified before removing old versions.
11. Contract testing complements unit and integration testing.
12. CI must eventually reject producer changes that break active consumer contracts.

---

## 24. MVP 2 Integration Tasks

The contract work for MVP 2 can be divided into the following technical tasks.

### CONTRACT-01 — Publish OpenAPI Contracts

**Objective**

Formalize REST interfaces as machine-readable contracts.

**Acceptance Criteria**

- Relevant REST endpoints are represented in OpenAPI.
- Request and response schemas are documented.
- Authentication requirements are documented.
- Expected errors are documented.
- API version is explicit.

---

### CONTRACT-02 — Define Compatibility Rules

**Objective**

Prevent uncoordinated breaking API changes.

**Acceptance Criteria**

- Additive optional changes are documented as compatible.
- Rename/remove/type changes are documented as breaking.
- Breaking changes require a new version.
- Deprecation rules are documented.

---

### CONTRACT-03 — Define Event Schemas

**Objective**

Formalize asynchronous Domain Event structures.

**Acceptance Criteria**

- Events include `eventId`.
- Events include `eventVersion`.
- Events include `occurredAt`.
- Events identify their producer.
- Event payload contains only required information.

---

### CONTRACT-04 — Add First Pact Contract Test

**Objective**

Validate at least one real consumer-producer expectation.

**Initial Candidate**

```text
agent-service
      ↓
patient-service
```

**Acceptance Criteria**

- Consumer expectation is defined.
- Pact contract is generated.
- Producer verifies the contract.
- A breaking response change causes the verification to fail.

---

### CONTRACT-05 — Integrate Contract Verification into CI

**Objective**

Prevent incompatible contracts from being merged or promoted.

**Acceptance Criteria**

- Contract verification executes automatically.
- Failed verification produces a failed CI result.
- Contract failures block promotion until resolved.

---

## 25. MVP 2 Contract Backlog

| ID | Task | Priority | Status |
|---|---|---|---|
| `CONTRACT-01` | Publish OpenAPI contracts | P1 | In progress / planned |
| `CONTRACT-02` | Define compatibility rules | P1 | Defined in this document |
| `CONTRACT-03` | Define event schemas | P1 | Planned |
| `CONTRACT-04` | Add first Pact test | P1 | Planned |
| `CONTRACT-05` | Add contract verification to CI | P2 | Planned |

These identifiers represent technical integration work and do not replace the official product User Stories.

---

## 26. Final Strategy

TeleMed IA uses:

```text
REST
   ↓
OpenAPI

Domain Events
   ↓
Versioned Event Schema

Future gRPC
   ↓
.proto
```

Contract evolution follows:

```text
Compatible change
      ↓
same version

Breaking change
      ↓
new version
      ↓
deprecate previous version
      ↓
migrate consumers
```

Contract testing follows:

```text
Consumer expectation
       ↓
Pact
       ↓
Producer verification
       ↓
CI
```

The goal is to allow services to evolve independently without silently breaking other parts of the TeleMed IA platform.

---

## Activity Requirements Coverage

| Requirement | TeleMed IA Strategy |
|---|---|
| Machine-readable REST contract | OpenAPI |
| gRPC contract | `.proto` if adopted |
| Event contract | Versioned JSON Schema |
| API versioning | `/api/v1`, future `/api/v2` |
| Backward compatibility | Optional additive changes |
| Breaking changes | New version required |
| Standard errors | `code`, `message`, `details`, `traceId` |
| Consumer-driven testing | Pact |
| First Pact candidate | `agent-service` → `patient-service` |
| CI contract verification | Planned |
| Event versioning | `eventVersion` |
| Source of Truth | Versioned contract files |

---

## Related Documentation

- Domain Events → `02-domain/domain-events.md`
- User Stories → `04-requirements/user-stories.md`
- Architecture Overview → `05-architecture/overview.md`
- Architectural Decisions → `05-architecture/decisions/records/`
- API Contracts → `07-api/contracts/openapi/`
- Microservices Catalog → `09-microservices/service-catalog.md`
- Communication Patterns → `09-microservices/communication-patterns.md`
- Quality and Testing → `11-quality/`