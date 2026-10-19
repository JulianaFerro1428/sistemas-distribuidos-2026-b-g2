# CP9 - Incident drill and blameless postmortem

## Scenario
Hop marked in CP1: `intelligent-agent-api FinishConversation` -> `mongodb transaction preconsultation_summaries + outbox_events`.
Injected failure: MongoDB `intelligent_agent` (replica set rs0) answers in 3 s for 25 % of the requests for 40 minutes.
Assumption: intelligent-agent-api uses a 2 s MongoDB operation timeout (`socketTimeoutMS=2000`, fail fast like D-C13),
so a 3 s answer becomes a timeout and the API returns 503 (`ServiceUnavailable` in `_shared.yaml`).
Drill clock: starts 08:00 (morning peak, 267 requests per hour, see `05-slo.md`).

## Trace of a slow request (trace_id 4bf92f3577b34da6a3ce929d0e0e4736)
- `gateway POST /api/v1/agent/conversations/{id}/finish` 2 660 ms, status 503 (root)
  - `intelligent-agent-api FinishConversation` 2 640 ms, ERROR
    - `agent.model draft summary` 610 ms, OK (AI provider, as in the CP2 example)
    - `mongodb transaction preconsultation_summaries + outbox_events` 2 000 ms, ERROR (timeout, aborted)
  - no `telemed-ia-worker publish PreConsultationSummaryGenerated` span: the transaction was aborted, so no outbox event exists

## Four log lines of that trace (CP2 contract, timestamps in UTC = 08:12 local)
```json
{"timestamp":"2026-10-20T13:12:03.114Z","level":"INFO","service":"telemed-ia-api-gateway","version":"2.0.0","environment":"dev","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"a1b2c3d4e5f60708","correlation_id":"c0r-4bf92f35","event":"http.request.received","message":"POST /api/v1/agent/conversations/{id}/finish","duration_ms":0}
{"timestamp":"2026-10-20T13:12:03.129Z","level":"INFO","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"00f067aa0ba902b7","correlation_id":"c0r-4bf92f35","event":"conversation.finish.received","message":"finish request accepted","duration_ms":3,"conversation_id":"3f1c..."}
{"timestamp":"2026-10-20T13:12:05.745Z","level":"ERROR","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"b7ad6b7169203331","correlation_id":"c0r-4bf92f35","event":"mongodb.transaction.timeout","message":"preconsultation_summaries + outbox_events aborted after 2000 ms","duration_ms":2000,"conversation_id":"3f1c...","conversation_status":"ACTIVE","error_code":"SERVICE_UNAVAILABLE"}
{"timestamp":"2026-10-20T13:12:05.774Z","level":"ERROR","service":"telemed-ia-api-gateway","version":"2.0.0","environment":"dev","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"a1b2c3d4e5f60708","correlation_id":"c0r-4bf92f35","event":"http.request.completed","message":"upstream answered 503","duration_ms":2660}
```

## SLI values during the incident
| SLI | Normal | During the 40 minutes | 30-day value after the incident |
|---|---|---|---|
| Availability (non-5xx / all) | 99.9 % | 75 % (25 % of requests time out) | (60 000 - 44) / 60 000 = 99.93 %, still above 99.0 % |
| Latency (message turns <= 4 s) | 97 % | about 97 %: failed turns end at about 3.5 s (AI 1.5 s + 2 s timeout) | unchanged |
| Summary freshness (<= 60 s) | 99.8 % | unchanged for summaries that exist, but 25 % of `/finish` calls create no summary | unchanged |

## Which alert fires first, and when
- `AgentAvailabilityFastBurn`: the 5 m ratio passes 14.4 % at about minute 1. The 1 h ratio is 0.25 x t / 60,
  so it passes 14.4 % at t = 0.144 x 60 / 0.25 = 34.6 min. With `for: 5m` the page fires at about **minute 40 (08:40)**.
- `AgentAvailabilitySlowBurn` never fires: the 6 h ratio reaches only 0.25 x 40 / 360 = 2.8 % (< 6 %).
- No latency alert exists, so nothing fires before minute 40.

## Which runbook step stops the damage
The page arrives just as the dependency recovers. Runbook section 2, step 5 ("was anything deployed in the last hour?")
finds the cause. Mitigation 1 (feature flag to pause new pre-consultations) is the step that would have stopped
patient impact; in this drill it came too late, which is the main lesson below.

## Blameless postmortem

### Timeline (2026-10-20, drill)
| Time | Event |
|---|---|
| 08:00 | A Liquibase changeset from telemed-ia-intelligent-agent-db starts an index build on `messages` during the deploy. Disk I/O on the rs0 primary saturates; 25 % of operations take 3 s. |
| 08:01 | First 503 on `/finish` and `/messages`; error ratio 25 %. |
| 08:35 | 1 h error ratio crosses 14.4 %. |
| 08:40 | `AgentAvailabilityFastBurn` pages the on-call developer. The index build finishes; MongoDB is normal again. |
| 08:43 | On-call acknowledges, runs the runbook PromQL and finds trace 4bf92f35... with a 2 000 ms MongoDB span. |
| 08:50 | Recent-changes check links the start of the errors to the migration. Error ratio below 1 % for 10 minutes. |
| 09:05 | Recovery verified (runbook section 5); incident closed. |

### Impact
- 40 min x 267 requests per hour = 178 requests; 25 % failed = **44 failed requests**.
- 44 / 600 = **7.3 % of the monthly availability budget**, equal to 7.3 % x 432 = **32 budget minutes**
  (more than the 10 min of wall-clock outage equivalent, because it happened at peak traffic).
- 25 % of the patients who pressed finish had to repeat it; no clinical data was lost (the transaction aborted).

### Root cause
An index build on the `messages` collection ran on the primary of the MongoDB replica set during peak hours,
so 25 % of MongoDB operations from intelligent-agent-api took 3 s and timed out at 2 s.

### Contributing factors
- Migrations run at deploy time, with no rule to run heavy index builds off-peak.
- Alerts only watch availability; a slow dependency became visible only after 40 minutes.
- The agent portal has no feature flag yet to pause the pre-consultation (runbook mitigation 1 is planned).

### Action items (user stories)
1. As the on-call developer, I want a latency burn-rate alert on `/messages` and `/finish`, so that a slow dependency pages me in less than 10 minutes.
   - AC: a test that injects 3 s MongoDB answers for 25 % of requests fires the alert within 10 minutes.
   - AC: the alert has `severity`, `team`, `summary` and `runbook_url`.
2. As a patient, I want the portal to skip the pre-consultation when the agent is degraded, so that I can still book my appointment.
   - AC: with the flag on, the portal hides "start pre-consultation" and `POST /appointments` works without `preconsultationSummaryId`.
   - AC: turning the flag on or off needs no redeploy.
3. As the team, we want heavy Liquibase index builds in telemed-ia-intelligent-agent-db to run only off-peak, so that migrations do not slow production.
   - AC: CI fails if a changeset creates an index without the `off-peak` label.
   - AC: the release checklist shows the migration window (22:00-05:00).
