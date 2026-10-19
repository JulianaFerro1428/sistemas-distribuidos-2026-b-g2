# CP7 - Burn-rate alert policy

SLO: 99.0 % availability of intelligent-agent-api (`/api/v1/agent`) over 30 days (see `05-slo.md`).
Error budget: 1 % = 600 failed requests = 432 minutes per 30 days (60 000 requests).
Rules: `06-alerts.yml`, metric `agent_requests_total{outcome="error"}` from CP3/CP4.

## How much budget each alert represents before it fires
Budget consumed = burn rate x long window / 30 days (720 h).

| Alert | Burn rate | Error ratio threshold | Long / short window | Budget consumed when it fires | In requests | In minutes |
|---|---|---|---|---|---|---|
| AgentAvailabilityFastBurn (page) | 14.4 | 14.4 % | 1 h / 5 m | 14.4 x 1 / 720 = 2 % | 2 % of 600 = 12 failed requests | 2 % of 432 = 8.6 min |
| AgentAvailabilitySlowBurn (ticket) | 6 | 6 % | 6 h / 30 m | 6 x 6 / 720 = 5 % | 5 % of 600 = 30 failed requests | 5 % of 432 = 21.6 min |

- With about 83 requests per hour (2 000 per day), the fast alert means roughly 12 of the last 83 requests
  failed: for example, the AI provider returning 503 after the D-C13 retries.
- If nobody acts, the fast burn empties the whole budget in 720 / 14.4 = 50 hours; the slow burn in 720 / 6 = 120 hours.
- The short window (5 m / 30 m) makes the alert stop soon after the problem is fixed, so it does not keep paging.
- Low traffic risk: at night there are very few requests, so 2 failures out of 5 would cross 14.4 %.
  Mitigation: during the pilot, review night pages and add a minimum-traffic condition if they are noisy.

## Cause-based alerts we will NOT create
| Alert we skip | Why |
|---|---|
| CPU of the intelligent-agent-api container > 80 % | High CPU does not mean patients are affected; if it matters, latency or errors already show it in the SLO alerts. It would page people for no user impact. |
| MongoDB `outbox_events` collection size or a single 5xx from the AI provider | One retry or a growing collection is normal (D-C13 retries, worker batches). We alert on the symptom (budget burn and summary freshness), not on each cause. |
