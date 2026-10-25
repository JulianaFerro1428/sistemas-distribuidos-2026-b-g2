# TeleMed IA — SLOs, Error Budget, Alerts and Runbook

## Weekly activity

Define **2–3 SLOs** with measurement windows, an **error-budget policy**, a **symptom-based alert** with an owner and runbook, and observability stories with testable acceptance criteria.

## 1. Existing implementation and planning scope

The `intelligent-agent-api` repository already includes structured JSON logging, incoming W3C trace-context handling, and Prometheus-compatible metrics. The following components are evidenced by source code:

- `ContractLogFormatter.java`: structured JSON log format.
- `TraceContextFilter.java`: trace ID, span ID, correlation ID and request-completion logging.
- `MetricsEndpointTest.java`: test for `agent_requests_total`, `agent_request_duration_seconds_bucket`, and `agent_outbox_pending_events` on the management port.

This README uses that implementation as the starting point. **SLO targets, error-budget thresholds, alerting policy and runbook below are documented planning decisions**; the review did not verify that they are deployed as executable alert rules or automated budget dashboards.

## 2. Three proposed SLOs

| SLO | User experience | Target | Window | Measurement |
|---|---|---|---|---|
| Availability | Patients can use the agent API | At least 99.5% successful eligible requests | Rolling 30 days | Successful / eligible requests |
| Latency | Agent API endpoints respond promptly | At least 95% of eligible non-generation requests under 3 seconds | Rolling 30 days | Request duration histogram |
| Freshness | Completed asynchronous updates become visible promptly | At least 95% visible within 60 seconds | Rolling 30 days | Visibility time minus commit time |

**Scope notes:** exclude expected client errors from availability; define eligible routes explicitly. Long-running AI generation should be measured separately from ordinary API request latency. The freshness objective requires measuring event commit and read-model visibility times; the existing outbox backlog gauge alone does not prove freshness.

### SLI formulas

```text
Availability = successful eligible requests / all eligible requests
Latency compliance = eligible requests under 3 s / all eligible requests
Freshness compliance = updates visible within 60 s / all completed updates
```

The targets are initial project objectives, not measured production performance.

## 3. Error-budget policy

For the 99.5% availability SLO:

```text
Error budget = 100% - 99.5% = 0.5%
Example: 100,000 eligible requests → 500 allowed failures in 30 days
Budget consumed = observed eligible failures / allowed failures × 100%
```

| Budget consumed | Proposed team action |
|---|---|
| Below 50% | Normal delivery and monitoring |
| 50–75% | Review trends and prioritize emerging risks |
| 75–100% | Review risky releases and prioritize stabilization |
| 100% or more | Pause nonessential risky changes; investigate and restore reliability |

Security fixes and patient-safety corrections take priority over this release policy.

## 4. Symptom-based alert policy

**Alert:** `IntelligentAgentHighErrorRate`  
**Affected service:** `intelligent-agent-api`  
**Symptom:** more than 5% eligible requests fail with HTTP 5xx over five minutes, with at least 20 eligible requests in the window.  
**Severity:** Critical  
**Owner:** TeleMed IA backend team / assigned on-call  
**Runbook:** [AI Pre-consultation Incident](#5-runbook--ai-pre-consultation-failures-or-high-latency)

The threshold is proposed for validation against real traffic. The alert should be calculated from appropriate request counters and routed through the team's alerting system when configured. CPU usage can support diagnosis but should not generate a critical page by itself.

## 5. Runbook — AI pre-consultation failures or high latency

**Symptoms:** patients cannot complete pre-consultations, requests return 5xx errors, or API latency rises significantly.

1. **Confirm:** check request traffic, error rate, duration histogram, health endpoint, and affected routes.
2. **Correlate:** identify a failed request and search structured JSON logs by `trace_id` or `correlation_id`.
3. **Investigate:** inspect recent exceptions, MongoDB connectivity, appointment-service calls, and AI-provider timeouts/retries. If distributed traces are available, inspect the slow spans.
4. **Mitigate:** apply a cause-specific safe action, such as correcting a failing dependency, adjusting capacity, or reverting a problematic release. Avoid losing clinical data or disabling required validation.
5. **Escalate:** contact the backend on-call; involve infrastructure, database, or AI-provider owners according to the confirmed failure.
6. **Verify:** repeat the affected request, confirm reduced 5xx rate and latency, and verify normal behavior over a sustained observation period.
7. **Document:** record start/end time, user impact, trace IDs, cause (if confirmed), actions, and follow-up work.

Do not log medical histories, patient identifiers, secrets, or raw request bodies when collecting evidence.

## 6. Observability stories and testable acceptance criteria

### OBS-01 — Structured logs and request correlation

**As a developer**, I want searchable JSON logs with trace context so I can investigate an agent request.

**Evidence in repository:** `ContractLogFormatter.java`, `TraceContextFilter.java`, and their tests.

- [x] JSON formatter exists with `trace_id`, `span_id`, and `correlation_id`.
- [x] HTTP filter records request status and duration.
- [x] Automated test files exist for formatter and filter.
- [ ] Execute tests in the delivery environment and attach passing results.
- [ ] Demonstrate correlation across two independently running services.

### OBS-02 — Agent request metrics

**As a developer**, I want request/error/latency metrics so I can understand user-visible failures.

**Evidence in repository:** `MetricsEndpointTest.java`, `application.yml`, and Micrometer Prometheus dependency.

- [x] Prometheus registry dependency exists.
- [x] Metrics endpoint is configured on a separate management port.
- [x] Test checks request count, duration histogram, and pending outbox metric.
- [x] Test checks that the public port does not serve the metrics endpoint.
- [ ] Execute the test and attach its result.
- [ ] Capture live metrics from the deployed service.

### OBS-03 — Availability and latency SLOs

**As a developer**, I want explicit reliability targets so we can measure whether the service meets user expectations.

- [x] Define a 99.5% availability SLO over rolling 30 days in this document.
- [x] Define a 95%-under-3-seconds latency SLO over rolling 30 days for eligible non-generation requests.
- [ ] Identify eligible routes and status-code classifications.
- [ ] Implement or verify 30-day SLI queries and dashboards.
- [ ] Test calculations with controlled successful, failed, and slow requests.

### OBS-04 — Freshness and error-budget tracking

**As a developer**, I want to detect delayed asynchronous updates and track allowable failures.

- [x] Document freshness objective and error-budget policy.
- [x] Confirm that the repository's metrics test checks an outbox backlog gauge.
- [ ] Instrument update commit-to-visibility delay for the chosen asynchronous workflow.
- [ ] Validate the 95%-within-60-seconds freshness objective.
- [ ] Calculate rolling availability budget consumption from real eligible requests.

### OBS-05 — Symptom-based alert

**As an on-call developer**, I want a meaningful error-rate alert so I can respond to patient-impacting failures.

- [x] Document alert trigger, severity, owner, and runbook.
- [ ] Configure the alert in the monitoring/alerting system.
- [ ] Verify that an isolated 500 response does not page the team.
- [ ] Verify that sustained failures above the threshold activate the alert.
- [ ] Verify alert resolution after recovery.

### OBS-06 — Incident runbook

**As an on-call developer**, I want a repeatable response procedure for failed or slow pre-consultations.

- [x] Document symptoms, confirmation, investigation, mitigation, escalation, and recovery checks.
- [ ] Simulate an incident and record the steps followed.
- [ ] Attach evidence of logs, metrics, and recovery.

## 7. Delivery status

| Component | Status |
|---|---|
| JSON logging | Implemented in source; automated test exists |
| HTTP trace context and correlation | Implemented in source; automated test exists |
| Prometheus metrics endpoint | Implemented/configured; automated test exists |
| Availability and latency SLOs | Defined here; runtime validation pending |
| Freshness SLO | Defined here; end-to-end measurement pending |
| Error-budget policy | Defined here; automation pending verification |
| Symptom-based alert | Specified here; deployment pending verification |
| Incident runbook | Documented here; simulation pending |

## 8. Conclusion

TeleMed IA is not starting observability from zero: its Intelligent Agent API already has structured logs, request correlation and Prometheus metrics with test coverage. This activity adds explicit reliability objectives, a budget policy, an alert contract and an incident runbook. The remaining work is to validate the objectives with real data and demonstrate alerting and cross-service operation.
