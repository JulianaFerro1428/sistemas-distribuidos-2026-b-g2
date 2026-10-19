# HOW TO RUN - intelligent-agent-api instrumentation

Minimal prototype of intelligent-agent-api instrumentation (HU-06).
The real service repository (telemed-ia-intelligent-agent-api) is still a seed.
Endpoint follows agent-service.yaml: POST /api/v1/agent/conversations/{id}/finish.

## Requirements
- Java 21
- Maven 3.9

## Install and test
```bash
mvn -B test
```

## Run
```bash
mvn spring-boot:run
curl -i -X POST http://localhost:8082/api/v1/agent/conversations/3f1c2b7a-0000-4000-8000-000000000001/finish \
  -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"
curl http://localhost:8082/actuator/prometheus | grep agent_
```

## What the test proves
- The trace_id received in `traceparent` is the same one stored in the
  `PreConsultationSummaryGenerated` outbox event headers.
- A missing header creates a new 32-hex trace_id.
- Finishing a conversation twice returns 409 and is counted in `agent_requests_total`.

## Real console output
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.527 s -- in com.telemed.intelligentagent.preconsultation.TracePropagationTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  16.847 s
[INFO] Finished at: 2026-10-19T10:07:18-05:00
[INFO] ------------------------------------
```
