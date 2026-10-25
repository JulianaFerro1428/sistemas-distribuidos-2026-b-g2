# TeleMed IA — Observability: Implementation Review

## Weekly activity

Instrument services with **structured logs**, a propagated **trace ID**, **request/error/latency metrics**, and a distributed trace across a multi-service request. The second session defines reliability objectives and alerting.

## 1. Project context

TeleMed IA uses microservices to support healthcare workflows, including AI-assisted pre-consultations. This README describes observability work **already present in the `intelligent-agent-api` repository** and distinguishes it from integration work that still requires verification.

**Reviewed repository:** `code-corhuila/telemed-ia-intelligent-agent-api`  
**Branch:** `develop`

## 2. Structured logging — implemented

The service includes `ContractLogFormatter.java`, a Spring Boot structured log formatter that writes one JSON object per log line. Fields include `timestamp`, `level`, `service`, `version`, `environment`, `trace_id`, `span_id`, `correlation_id`, `event`, and `message`. Event-specific fields, such as `duration_ms`, are added through SLF4J key-value pairs.

The formatter is activated in `intelligent-agent-app/src/main/resources/application.yml`:

```yaml
logging:
  structured:
    format:
      console: com.telemed.intelligentagent.app.observability.ContractLogFormatter
```

The application has a `ContractLogFormatterTest.java` test for its log contract. Sensitive clinical content, tokens, and secrets must not be included in log messages.

## 3. Trace and correlation context — implemented for incoming HTTP requests

`TraceContext.java` reads the W3C `traceparent` header and keeps the incoming trace identifier when valid. `TraceContextFilter.java` runs early in the HTTP filter chain, populates the SLF4J MDC with `trace_id`, `span_id`, and `correlation_id`, and includes tracing headers in the response.

At request completion, the filter logs the route template, HTTP method, status, and duration in milliseconds. It avoids logging raw paths and request bodies, which could expose patient data. `TraceContextFilterTest.java` verifies the context behavior.

**Important distinction:** request-context handling and correlation IDs are implemented. A complete exported trace spanning multiple independently deployed services (for example, gateway → agent → downstream service) must be demonstrated separately. The inspected files do not by themselves prove a Jaeger/Tempo/OpenTelemetry end-to-end trace.

## 4. Metrics — implemented and covered by a test

The application includes the Micrometer Prometheus registry. Its management endpoint is configured on a separate port (`MANAGEMENT_PORT`, default `9086`), while the application port defaults to `8086`.

```yaml
management:
  server:
    port: ${MANAGEMENT_PORT:9086}
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  endpoint:
    health:
      show-details: never
```

`MetricsEndpointTest.java` checks that `/actuator/prometheus` is available on the management port and is not served on the public application port. It asserts the presence of these metrics:

- `agent_requests_total` — request count, useful for traffic and error analysis.
- `agent_request_duration_seconds_bucket` — request duration histogram, useful for latency analysis.
- `agent_outbox_pending_events` — pending outbox events, useful for detecting processing backlogs.

The existence of these metrics and tests is confirmed in source code. Live Prometheus scraping and Grafana dashboard operation still require runtime evidence.

## 5. Golden signals for TeleMed IA

| Signal | Source / measurement | Use |
|---|---|---|
| Traffic | `agent_requests_total` | How many agent requests are handled |
| Errors | Failed requests derived from request metrics | Detect user-visible failures |
| Latency | `agent_request_duration_seconds_bucket` | Analyze slow agent requests |
| Saturation | Resource metrics and outbox backlog | Investigate pressure or delayed work |

## 6. How to verify the implementation

1. Run the project's automated tests, including `ContractLogFormatterTest`, `TraceContextFilterTest`, and `MetricsEndpointTest`.
2. Send an HTTP request containing a valid W3C `traceparent` header.
3. Confirm the trace/correlation context is present in the response and structured request log.
4. Check `/actuator/prometheus` on the **management port** and verify the metrics above.
5. Confirm that the public port does not expose `/actuator/prometheus`.
6. Capture a request passing through at least two services and verify consistent trace context across them; if using a trace backend, capture the full trace view.

These are verification steps, not claims that the tests were executed during this review.

## 7. Current status

| Requirement | Status |
|---|---|
| Structured JSON logs | Implemented; test exists |
| Incoming W3C trace context | Implemented; test exists |
| Trace/correlation IDs in request logs | Implemented; test exists |
| Request count and latency histogram | Implemented; endpoint test exists |
| Prometheus endpoint isolated on management port | Configured; test exists |
| Multi-service exported distributed trace | Not yet verified |
| Live Grafana/Prometheus evidence | Not yet verified |

## 8. Conclusion

The Intelligent Agent API already contains meaningful observability instrumentation. This activity therefore focuses on documenting, testing, and demonstrating the existing implementation rather than treating logs, trace IDs, and metrics as future work. The remaining evidence is the cross-service trace and the runtime monitoring views.

## Source files

- [`ContractLogFormatter.java`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-app/src/main/java/com/telemed/intelligentagent/app/observability/ContractLogFormatter.java)
- [`TraceContextFilter.java`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-adapters/src/main/java/com/telemed/intelligentagent/adapter/in/http/observability/TraceContextFilter.java)
- [`application.yml`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-app/src/main/resources/application.yml)
- [`MetricsEndpointTest.java`](https://github.com/code-corhuila/telemed-ia-intelligent-agent-api/blob/develop/intelligent-agent-app/src/test/java/com/telemed/intelligentagent/MetricsEndpointTest.java)
