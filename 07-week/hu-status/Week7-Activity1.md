# Inter-Service Communication — REST, gRPC and Messaging

> This document defines how TeleMed IA services should communicate as the current modular monolith evolves toward independent microservices.

---

## 1. Objective

TeleMed IA must choose the communication mechanism according to the needs of each interaction.

The system uses two main communication modes:

```text
Synchronous
Asynchronous
```

The main rule is:

```text
Need an immediate answer?
        │
   ┌────┴────┐
   │         │
  YES        NO
   │         │
   ▼         ▼
 REST       Event
```

---

## 2. Current Context

The current MVP is still a modular monolith:

```text
Angular / Ionic
      ↓
Spring Boot Backend
      ↓
PostgreSQL
```

The target architecture is:

```text
Angular / Ionic
      ↓
API Gateway
      ↓
auth-service
patient-service
professional-service
agent-service
appointment-service
consultation-service
notification-service
document-service
```

When these services are extracted, communication must occur through explicit APIs or events.

---

## 3. Synchronous Communication

Synchronous communication means that the caller waits for a response.

TeleMed IA uses REST as the main synchronous communication mechanism.

Example:

```text
agent-service
      │ REST
      ▼
patient-service
```

REST is appropriate because:

- It uses HTTP and JSON.
- It is easy to debug.
- It works well with Angular.
- It supports OpenAPI.
- It allows services written in different languages to communicate.

Example:

```text
Spring Boot service
        │
        │ HTTP + JSON
        ▼
Go service
```

The implementation language does not matter as long as the API contract is respected.

---

## 4. REST in TeleMed IA

Expected synchronous interactions include:

| Source | Destination | Purpose |
|---|---|---|
| Frontend | `auth-service` | Login and authentication |
| Frontend | `appointment-service` | View availability |
| `agent-service` | `patient-service` | Obtain required patient context |
| `appointment-service` | `professional-service` | Obtain professional information |
| `agent-service` | External AI Provider | Generate the next agent response |

REST contracts must be documented in:

```text
07-api/contracts/openapi/
```

---

## 5. gRPC

gRPC is another synchronous option.

It provides:

- Strong contracts with `.proto`.
- Compact binary messages.
- HTTP/2.
- High performance.
- Streaming support.

However, TeleMed IA does not currently require gRPC.

The current decision is:

```text
REST = current default

gRPC = future option if performance or latency requirements justify it
```

A future adoption of gRPC should be documented through an ADR.

---

## 6. Asynchronous Communication

Asynchronous communication is used when a business event has already occurred and the producer does not need to wait for consumers.

Example:

```text
appointment-service
        │
        │ AppointmentCreated
        ▼
Message Broker
        │
        ▼
notification-service
```

The appointment should remain successfully created even if the notification service or email provider is temporarily unavailable.

---

## 7. Domain Events

TeleMed IA already defines business events such as:

```text
PatientRegistered
PasswordRecoveryRequested
PreConsultationSummaryGenerated

AppointmentCreated
AppointmentRescheduled
AppointmentCancelled
AppointmentReminderDue
AppointmentCompleted

ConsultationCompleted
ReferralCreated
PostConsultationSummaryGenerated

ConsultationPdfGenerated
```

The authoritative event definitions remain in:

```text
02-domain/domain-events.md
```

The broker technology is still not defined.

Therefore:

```text
Message Broker = TBD
```

Potential technologies may later include RabbitMQ, Kafka, or AWS-native messaging services.

---

## 8. Main Asynchronous Interactions

| Producer | Event | Consumer |
|---|---|---|
| `auth-service` | `PatientRegistered` | `patient-service` |
| `auth-service` | `PasswordRecoveryRequested` | `notification-service` |
| `agent-service` | `PreConsultationSummaryGenerated` | `appointment-service` |
| `appointment-service` | `AppointmentCreated` | `notification-service` |
| `appointment-service` | `AppointmentCancelled` | `notification-service` |
| `appointment-service` | `AppointmentRescheduled` | `notification-service` |
| `consultation-service` | `ConsultationCompleted` | `appointment-service` |
| `consultation-service` | `PostConsultationSummaryGenerated` | `document-service` |
| `document-service` | `ConsultationPdfGenerated` | `patient-service` |

---

## 9. Delivery Semantics

Distributed messaging may duplicate or lose messages.

The main delivery models are:

```text
At-most-once
At-least-once
```

TeleMed IA should prefer:

```text
At-least-once delivery
        +
Idempotent consumers
```

because important business events should not be silently lost.

The consequence is that a consumer must tolerate duplicate delivery.

---

## 10. Idempotency

An idempotent consumer can receive the same event multiple times without repeating the business effect.

Example:

```text
AppointmentCreated
eventId = evt-123
```

First delivery:

```text
evt-123
   ↓
notification created
   ↓
event marked as processed
```

Second delivery:

```text
evt-123
   ↓
already processed
   ↓
ignore duplicate
```

Result:

```text
Message delivered twice
        ↓
Business effect once
```

---

## 11. Idempotent Consumer Design

A suitable first idempotent consumer in TeleMed IA is:

```text
notification-service
```

consuming:

```text
AppointmentCreated
```

The consumer can maintain:

```text
processed_events
```

with a unique `eventId`.

Example:

```sql
CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);
```

Processing flow:

```text
Receive event
     ↓
eventId already processed?
     │
 ┌───┴────┐
 │        │
YES       NO
 │        │
 ▼        ▼
Ignore   Apply effect
          ↓
     Save eventId
```

---

## 12. Event Structure

Each event should contain metadata such as:

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
    "patientId": 25
  }
}
```

Important fields:

```text
eventId
eventType
eventVersion
occurredAt
producer
correlationId
payload
```

The event must contain only the information required by consumers.

---

## 13. Retry and Failure Handling

Temporary failures may be retried.

Recommended:

```text
Retry
+
Backoff
+
Limited attempts
```

A consumer must not retry forever.

If processing repeatedly fails, the future broker strategy should support a dead-letter mechanism.

```text
Event
  ↓
Retry
  ↓
Retry
  ↓
Failure
  ↓
Dead Letter Queue
```

---

## 14. Avoid Long Synchronous Chains

TeleMed IA should avoid:

```text
A → B → C → D
```

because failure or latency in one service may affect the complete request.

For example, notification delivery should not be part of the synchronous appointment transaction.

Incorrect:

```text
appointment-service
      ↓
notification-service
      ↓
email provider
```

Preferred:

```text
appointment-service
      │
      │ AppointmentCreated
      ▼
Message Broker
      │
      ▼
notification-service
```

---

## 15. Communication Decision Matrix

| Interaction | Mode | Technology |
|---|---|---|
| Login | Synchronous | REST |
| Patient profile | Synchronous | REST |
| Appointment availability | Synchronous | REST |
| Agent → patient context | Synchronous | REST |
| Agent → AI provider | Synchronous | HTTPS/REST |
| Pre-consultation completed | Asynchronous | Domain Event |
| Appointment created | Asynchronous | Domain Event |
| Appointment cancelled | Asynchronous | Domain Event |
| Consultation completed | Asynchronous | Domain Event |
| PDF generation | Asynchronous | Domain Event |

---

## 16. Security Rules

Service communication must follow these rules:

- Validate incoming data.
- Authenticate protected requests.
- Apply authorization.
- Follow least privilege.
- Never send passwords or API keys in events.
- Avoid unnecessary medical information in event payloads.
- Never directly access another service's database.
- Use API contracts or Domain Events.

Correct:

```text
consultation-service
        ↓
REST / Event
        ↓
appointment-service
```

Incorrect:

```text
consultation-service
        ↓
Direct SQL
        ↓
appointment database
```

---

## 17. Current and Target State

The communication model documented here describes the target distributed architecture.

Currently:

```text
Domain Events defined
        ≠
Message broker already deployed
```

and:

```text
Target microservice interaction
        ≠
Independent services already running
```

The migration will be progressive.

---

## 18. Final Communication Strategy

TeleMed IA adopts the following decisions:

1. REST is the current synchronous communication default.
2. OpenAPI documents REST contracts.
3. gRPC is not currently required.
4. Domain Events are used for asynchronous business interactions.
5. The message broker remains TBD.
6. Long synchronous chains should be avoided.
7. Synchronous calls require timeouts.
8. Important events should support at-least-once delivery.
9. Consumers must be idempotent.
10. Every event must have a unique `eventId`.
11. Duplicate events must not create duplicate business effects.
12. Event payloads must contain only necessary information.
13. Services must never access another service's database directly.

---

## 19. Activity Requirements Coverage

| Requirement | TeleMed IA Decision |
|---|---|
| Synchronous vs asynchronous | Defined per interaction |
| REST | Current synchronous default |
| gRPC | Evaluated, not currently adopted |
| Messaging | Domain Events through future broker |
| Delivery semantics | At-least-once preferred |
| Idempotency | Required for consumers |
| First idempotent consumer | `notification-service` |
| First event candidate | `AppointmentCreated` |
| Duplicate prevention | `eventId` + processed event registry |
| Message broker | TBD |

---

## Related Documentation

- Domain Map → `02-domain/domain-map.md`
- Domain Events → `02-domain/domain-events.md`
- User Stories → `04-requirements/user-stories.md`
- Architecture Overview → `05-architecture/overview.md`
- Architecture Decisions → `05-architecture/decisions/records/`
- API Contracts → `07-api/contracts/openapi/`
- Microservices Catalog → `09-microservices/service-catalog.md`
- Security Rules → `00-governance/security-rules.md`