package com.telemed.intelligentagent.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * agent_requests_total and agent_request_duration_seconds.
 */
@Component
public class AgentMetrics {

    private final MeterRegistry registry;

    public AgentMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void record(String route, String method, int status, long durationNanos) {
        Counter.builder("agent.requests")
                .description("Pre-consultation HTTP requests by outcome")
                .tags("route", route, "method", method, "outcome", outcome(status))
                .register(registry)
                .increment();
        Timer.builder("agent.request.duration")
                .description("Pre-consultation HTTP latency")
                .tags("route", route, "method", method)
                .serviceLevelObjectives(
                        Duration.ofMillis(100), Duration.ofMillis(250), Duration.ofMillis(500),
                        Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(4),
                        Duration.ofSeconds(8))
                .register(registry)
                .record(Duration.ofNanos(durationNanos));
    }

    static String outcome(int status) {
        if (status >= 500) {
            return "error";
        }
        return status >= 400 ? "client_error" : "success";
    }
}
