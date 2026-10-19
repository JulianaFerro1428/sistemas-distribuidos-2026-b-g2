package com.telemed.intelligentagent.preconsultation;

import com.telemed.intelligentagent.observability.TraceContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stands in for `outbox_events` until intelligent-agent-api connects to MongoDB.
 * The traceparent is stored with the event so the worker relay can copy it to RabbitMQ.
 */
@Component
public class InMemoryOutbox {

    private final List<OutboxEvent> events = new CopyOnWriteArrayList<>();

    public OutboxEvent addSummaryGenerated(
            TraceContext context, String correlationId, String conversationId, String summaryId) {
        OutboxEvent event = new OutboxEvent(
                UUID.randomUUID().toString(),
                "PreConsultationSummaryGenerated",
                Instant.now(),
                Map.of("conversationId", conversationId, "summaryId", summaryId),
                Map.of("traceparent", context.child().toHeader(), "correlationId", correlationId));
        events.add(event);
        return event;
    }

    public List<OutboxEvent> events() {
        return List.copyOf(events);
    }
}
