# CP6 - SLIs, SLOs and error budget

Journey: AI pre-consultation (HU-06) through telemed-ia-api-gateway -> intelligent-agent-api
(`/api/v1/agent`) -> MongoDB `intelligent_agent` -> outbox_events -> telemed-ia-worker.
All PromQL uses the metrics defined in `04-metrics.md`.

## SLO 1 - Availability of the agent API
- SLI: good events / valid events = requests that did not fail with 5xx / all requests to `/api/v1/agent`.
  4xx (for example 409 on a second `/finish`) are the client's fault and count as good.
- PromQL:
  `sum(rate(agent_requests_total{outcome!="error"}[30d])) / sum(rate(agent_requests_total[30d]))`
- Target: 99.0 %. The turn depends on an external AI provider; after the D-C13 retries are exhausted the API returns 503, so 99.5 % is not realistic for an MVP.
- Window: 30 days (rolling)

## SLO 2 - Latency of a conversation turn
- SLI: message requests answered in 4 s or less / all message requests.
  `POST /conversations/{id}/messages` calls the AI provider, so it is the slowest step the patient waits for.
- PromQL:
  `sum(rate(agent_request_duration_seconds_bucket{route="/api/v1/agent/conversations/{id}/messages",le="4"}[30d])) / sum(rate(agent_request_duration_seconds_count{route="/api/v1/agent/conversations/{id}/messages"}[30d]))`
- Target: 95 % of message turns in 4 s or less. A chat answer feels acceptable up to about 4 s; above that, patients resend or abandon. The other 5 % still finishes before the 10 s shell timeout.
- Window: 30 days (rolling)

## SLO 3 - Pre-consultation summary freshness
- SLI: `PreConsultationSummaryGenerated` events published from `outbox_events` within 60 s / all events published.
  If the summary is late, the professional opens the appointment without the pre-consultation.
- PromQL:
  `sum(rate(agent_outbox_publish_delay_seconds_bucket{event_type="PreConsultationSummaryGenerated",le="60"}[30d])) / sum(rate(agent_outbox_publish_delay_seconds_count{event_type="PreConsultationSummaryGenerated"}[30d]))`
- Target: 99.0 % of summaries published within 60 s. The appointment usually starts minutes or hours later, so 60 s is enough, but a lost or stuck summary hurts the consultation.
- Window: 30 days (rolling)
- Note: requires a 60 s bucket on `agent_outbox_publish_delay_seconds` (planned metric, see CP4).

## Traffic estimate
Assumption for the MVP pilot: 10 General Practitioners x 20 first consultations per day, each preceded by
a pre-consultation (HU-06).
- Patients per day who start a pre-consultation: 200
- Messages per conversation (average): 8 (reason for consultation, symptoms, duration, follow-up questions)
- Requests per conversation = 1 start + 8 messages + 1 finish = 10
- Requests per day = 2 000 ; per 30 days = 60 000
- Message turns per 30 days = 200 x 8 x 30 = 48 000
- Summaries per 30 days = 200 x 30 = 6 000
- Peak: most patients book in the morning, so about 40 % of the daily traffic arrives between 7:00 and 10:00.

## Error budget
| SLO | Target | Budget (%) | Budget in requests / 30 d | Budget in minutes / 30 d |
|---|---|---|---|---|
| Availability | 99.0 % | 1.0 % | 1 % of 60 000 = 600 failed requests | 1 % of 43 200 = 432 min |
| Latency | 95 % <= 4 s | 5.0 % | 5 % of 48 000 = 2 400 slow turns | 5 % of 43 200 = 2 160 min |
| Summary freshness | 99.0 % <= 60 s | 1.0 % | 1 % of 6 000 = 60 late summaries | 1 % of 43 200 = 432 min |

Minutes = budget % x 43 200 minutes (30 days), read as minutes of total outage.
Check: 600 failed requests at 2 000 requests per day is 0.3 days = 432 min, the same budget.

## Error budget policy (team telemed-ia, Group 2)
| Budget consumed | Action |
|---|---|
| 50 % | Warn the team in the daily standup; review the top error routes in agent_requests_total and the slow traces. Releases continue. |
| 75 % | Only bug fixes and reliability work for intelligent-agent-api; new HU-06 features wait. Review the AI provider timeouts and D-C13 retries. |
| 100 % | Feature freeze for the agent journey until the budget recovers; the next sprint is a stabilization sprint and a blameless postmortem is required. |
