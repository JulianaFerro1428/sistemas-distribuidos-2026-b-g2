# CP2 - Structured log contract

## Mandatory fields
| Field | Type | Required | Example | Why |
|---|---|---|---|---|
| timestamp | string ISO-8601 UTC | yes | 2026-10-19T14:40:01.120Z | Order events across services |
| level | string | yes | INFO | Filter by severity |
| service | string | yes | intelligent-agent-api | Know who wrote the line |
| version | string | yes | 0.1.0 | Link errors to a release |
| environment | string | yes | dev / qa / main | Separate environments |
| trace_id | string 32 hex | yes | 7d3f9a2c... | Join all lines of one request |
| span_id | string 16 hex | yes | a1b2c3d4e5f60718 | Know which hop wrote it |
| correlation_id | string | yes | value of X-Correlation-Id | Same as the error envelope traceId |
| event | string dot.case | yes | preconsultation.summary.generated | Stable name to search |
| message | string | yes | summary stored | Short human text |
| duration_ms | integer | yes (end events) | 412 | Latency per hop |

## Domain fields
| Field | Type | Example | Why |
|---|---|---|---|
| conversation_id | UUID | 3f1c... | Pre-consultation in `conversations` |
| patient_id | UUID | 9b7e... | Internal id only, never personal data |
| turn | integer | 4 | Which message of the conversation failed |
| conversation_status | string | ACTIVE / FINISHED | Lifecycle state (agent-service.yaml) |
| summary_id | UUID | c2d4... | Row in `preconsultation_summaries` |
| outbox_event_id | UUID | 5e6f... | Row in `outbox_events` for PreConsultationSummaryGenerated |
| error_code | string | BUSINESS_RULE_VIOLATION | Same code as the error envelope |

## Never log
| Data | How it is masked |
|---|---|
| Message content, consultation reason, symptoms, relevant history | Never logged; log only `message_chars` (length) |
| Patient name, email, document number, phone | Never logged; use `patient_id` |
| Authorization header, JWT, refresh token | Replaced by `[REDACTED]` |
| Idempotency-Key | Log only a SHA-256 prefix (8 chars) |
| MongoDB connection string, passwords, model API key | Never logged; read from env |

## Example: one failed request (same trace_id)
```json
{"timestamp":"2026-10-19T14:40:01.120Z","level":"INFO","service":"telemed-ia-api-gateway","version":"2.0.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"1a2b3c4d5e6f7081","correlation_id":"c0r-7d3f9a2c","event":"http.request.received","message":"POST /api/v1/agent/conversations/{id}/finish","duration_ms":0}
{"timestamp":"2026-10-19T14:40:01.135Z","level":"INFO","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"2b3c4d5e6f708192","correlation_id":"c0r-7d3f9a2c","event":"conversation.finish.received","message":"finish request accepted","duration_ms":3,"conversation_id":"3f1c..."}
{"timestamp":"2026-10-19T14:40:04.140Z","level":"WARN","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"3c4d5e6f708192a3","correlation_id":"c0r-7d3f9a2c","event":"agent.model.timeout","message":"model provider did not answer, retrying once","duration_ms":3005,"conversation_id":"3f1c..."}
{"timestamp":"2026-10-19T14:40:05.020Z","level":"INFO","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"4d5e6f708192a3b4","correlation_id":"c0r-7d3f9a2c","event":"agent.model.responded","message":"draft summary received","duration_ms":870,"conversation_id":"3f1c..."}
{"timestamp":"2026-10-19T14:40:05.031Z","level":"WARN","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"5e6f708192a3b4c5","correlation_id":"c0r-7d3f9a2c","event":"preconsultation.summary.rejected","message":"summary contains a medication recommendation, clinical boundary","duration_ms":4,"conversation_id":"3f1c...","error_code":"BUSINESS_RULE_VIOLATION"}
{"timestamp":"2026-10-19T14:40:05.040Z","level":"ERROR","service":"intelligent-agent-api","version":"0.1.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"2b3c4d5e6f708192","correlation_id":"c0r-7d3f9a2c","event":"http.request.completed","message":"422 returned, no summary stored","duration_ms":3905,"conversation_id":"3f1c...","conversation_status":"ACTIVE","error_code":"BUSINESS_RULE_VIOLATION"}
{"timestamp":"2026-10-19T14:40:05.046Z","level":"INFO","service":"telemed-ia-api-gateway","version":"2.0.0","environment":"dev","trace_id":"7d3f9a2c4b1e8f60a9c2d5e7f1b3a4c6","span_id":"1a2b3c4d5e6f7081","correlation_id":"c0r-7d3f9a2c","event":"http.request.completed","message":"upstream answered 422","duration_ms":3926}
```

Note: the AI provider timeout follows D-C13 (retry once; if the retry budget is exhausted the API returns 503).
The clinical boundary rejection follows ADR-008. 422 (`UnprocessableEntity` in `_shared.yaml`) is not yet listed for `/finish` in `agent-service.yaml`; it is a proposed contract addition.

## Log levels
| Level | When | TeleMed IA example |
|---|---|---|
| ERROR | The request failed and needs attention | MongoDB transaction for `preconsultation_summaries` + `outbox_events` aborted |
| WARN | Something went wrong but was handled | Model timeout with a successful retry; summary rejected by clinical boundary (ADR-008) |
| INFO | Normal business milestones | Conversation started, summary generated, outbox event stored |
| DEBUG | Detail for local diagnosis only, off in qa/main | Outbox relay polling cycle details |