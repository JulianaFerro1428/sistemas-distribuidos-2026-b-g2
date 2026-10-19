# CP4 - Golden signals and metric catalog

Journey: AI pre-consultation (HU-06). Metric names follow Prometheus conventions:
snake_case, unit suffix, `_total` for counters.

## telemed-ia-api-gateway (NGINX, agent route /api/v1/agent)
| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | gateway_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(gateway_request_duration_seconds_bucket{route="/api/v1/agent"}[5m])))` |
| Traffic | gateway_requests_total | counter | route, method, status_class | `sum(rate(gateway_requests_total{route="/api/v1/agent"}[5m]))` |
| Errors | gateway_requests_total | counter | route, status_class | `sum(rate(gateway_requests_total{route="/api/v1/agent",status_class="5xx"}[5m])) / sum(rate(gateway_requests_total{route="/api/v1/agent"}[5m]))` |
| Saturation | gateway_rate_limited_total | counter | route | `sum(rate(gateway_rate_limited_total{route="/api/v1/agent"}[5m]))` (429 from limit_req 20 r/s) |

## intelligent-agent-api (implemented in 03-instrumentation)
| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | agent_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(agent_request_duration_seconds_bucket{route="/api/v1/agent/conversations/{id}/finish"}[5m])))` |
| Traffic | agent_requests_total | counter | route, method, outcome | `sum by (route) (rate(agent_requests_total[5m]))` |
| Errors | agent_requests_total | counter | route, outcome | `sum(rate(agent_requests_total{outcome="error"}[5m])) / sum(rate(agent_requests_total[5m]))` |
| Saturation | tomcat_threads_busy_threads | gauge | (none) | `max(tomcat_threads_busy_threads) / max(tomcat_threads_config_max_threads)` |

## Async hop: outbox_events -> telemed-ia-worker -> RabbitMQ (planned)
| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | agent_outbox_publish_delay_seconds | histogram | event_type | `histogram_quantile(0.95, sum by (le) (rate(agent_outbox_publish_delay_seconds_bucket[5m])))` |
| Traffic | agent_outbox_published_total | counter | event_type | `sum(rate(agent_outbox_published_total{event_type="PreConsultationSummaryGenerated"}[5m]))` |
| Errors | agent_outbox_publish_failures_total | counter | event_type | `sum(rate(agent_outbox_publish_failures_total[5m]))` |
| Saturation | agent_outbox_pending_events | gauge | event_type | `max(agent_outbox_pending_events)` |

## Label cardinality
| Label | Values | Estimated series |
|---|---|---|
| route | route templates only: /conversations, /conversations/{id}/messages, /conversations/{id}/finish, /preconsultations/{id} | 4 |
| method | GET, POST | 2 |
| outcome | success, client_error, error | 3 |
| status_class | 2xx, 4xx, 5xx | 3 |
| event_type | PreConsultationSummaryGenerated | 1 |

agent_requests_total: 4 x 2 x 3 = at most 24 series. Safe.

### Forbidden labels
| Unbounded label | Why | Replaced by |
|---|---|---|
| conversation_id, patient_id | One series per patient: cardinality explosion and health data exposure | Logs and traces (CP2), searched by trace_id |
| raw URL (/conversations/3f1c...) | Every UUID creates a new series | `route` with the template `{id}` (Spring BEST_MATCHING_PATTERN) |
| email, message content | Personal and clinical data | Never stored in metrics |

## Histogram buckets
Buckets: 0.1, 0.25, 0.5, 1, 2, 4, 8 seconds.
- Starting and finishing a conversation are MongoDB writes: the patient expects them under 0.5 s.
- A message turn calls the external AI provider; a chat answer feels acceptable up to 2-4 s.
- 8 s marks the slow tail before the shell timeout (10 s) and the gateway proxy_read_timeout (15 s).
