# CP8 - Runbook: AgentAvailabilityFastBurn

Alert: `AgentAvailabilityFastBurn` (severity `page`, team `telemed-ia-group-2`), defined in `06-alerts.yml`.
Service: intelligent-agent-api behind telemed-ia-api-gateway (`/api/v1/agent`).
SLO: 99.0 % availability over 30 days (`05-slo.md`). When this alert fires, about 2 % of the monthly
budget (12 failed requests) is already gone and the budget will be empty in about 50 hours.

## 1. Symptoms
- Patients in the agent portal see an error when they send a message or press finish in the pre-consultation.
- `POST /api/v1/agent/conversations/{id}/messages` returns 503 (AI provider retries exhausted, D-C13).
- `POST /api/v1/agent/conversations/{id}/finish` returns 5xx and no `PreConsultationSummaryGenerated` event is created.
- Error ratio above 14.4 % in the last hour and in the last 5 minutes.

## 2. Confirm
1. Error ratio by route (which endpoint is failing):
   `sum by (route) (rate(agent_requests_total{outcome="error"}[5m])) / sum by (route) (rate(agent_requests_total[5m]))`
2. Is the gateway also failing, or only the agent?
   `sum(rate(gateway_requests_total{route="/api/v1/agent",status_class="5xx"}[5m]))`
3. Is it slow, not only failing? p95 latency of message turns:
   `histogram_quantile(0.95, sum by (le) (rate(agent_request_duration_seconds_bucket{route="/api/v1/agent/conversations/{id}/messages"}[5m])))`
4. Take one failed request: copy the `traceparent` response header or the `trace_id` from the gateway log,
   then search all logs for that trace (CP2 contract):
   `{service="intelligent-agent-api"} | json | trace_id="<trace_id>" | level="ERROR"`
   Look at `event`, `route`, `status` and `error.type` to see if the failure is the AI provider, MongoDB
   `intelligent_agent`, or the code.
5. Check recent changes: was intelligent-agent-api or the gateway deployed in the last hour?

## 3. First mitigations (ordered from lowest to highest risk)
| # | Mitigation | When to use it | Risk |
|---|---|---|---|
| 1 | Turn on the feature flag that pauses new pre-consultations in the agent portal and shows "Your appointment can continue without the pre-consultation". | The AI provider is down or very slow. | Low: patients can still book (pre-consultation is optional for the appointment). |
| 2 | Roll back intelligent-agent-api to the previous image tag. | The errors started right after a deploy. | Medium: features from the last release are lost until the fix. |
| 3 | Open the circuit breaker to the AI provider (fail fast with 503 instead of waiting for retries) or scale intelligent-agent-api to one more replica. | Timeouts and busy threads (`tomcat_threads_busy_threads` near the max). | Medium: more fast errors, but threads are free and other routes keep working. |

Note: the feature flag and the circuit breaker are planned controls; today they must be added to
intelligent-agent-api (see the CP10 plan).

## 4. Escalation (Group 2 roles)
| Time since page | Who | Action |
|---|---|---|
| 0 min | On-call developer of intelligent-agent-api | Acknowledge the page, confirm (section 2), apply mitigation 1. |
| 15 min | Tech lead of the agent domain | Decide rollback or circuit breaker; check MongoDB and the gateway. |
| 30 min | Team scrum master / product owner | Inform the professional-management and appointment teams; decide user communication. |
| AI provider issue | Tech lead | Open a ticket with the AI provider and attach trace ids and timestamps. |

## 5. Verify recovery
- The error ratio is below 1 % for 15 minutes:
  `sum(rate(agent_requests_total{outcome="error"}[5m])) / sum(rate(agent_requests_total[5m])) < 0.01`
- `AgentAvailabilityFastBurn` resolves (the 5 m window drops first, then the 1 h window).
- A test pre-consultation (start, one message, finish) returns 201, 200, 200 and creates one
  `PreConsultationSummaryGenerated` event in `outbox_events` with the same trace id.
- Write down the budget spent in the incident and open the blameless postmortem (see `08-incident.md`).
