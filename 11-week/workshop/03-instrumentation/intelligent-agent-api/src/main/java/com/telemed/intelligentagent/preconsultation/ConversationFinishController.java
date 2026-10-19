package com.telemed.intelligentagent.preconsultation;

import com.telemed.intelligentagent.observability.TraceContext;
import com.telemed.intelligentagent.observability.TraceContextFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POST /api/v1/agent/conversations/{id}/finish from agent-service.yaml (HU-06 AC5).
 */
@RestController
public class ConversationFinishController {

    private static final Logger log = LoggerFactory.getLogger(ConversationFinishController.class);

    private final InMemoryOutbox outbox;
    private final Set<String> finishedConversations = ConcurrentHashMap.newKeySet();

    public ConversationFinishController(InMemoryOutbox outbox) {
        this.outbox = outbox;
    }

    @PostMapping("/api/v1/agent/conversations/{id}/finish")
    public ResponseEntity<Map<String, Object>> finish(@PathVariable String id, HttpServletRequest request) {
        TraceContext context = (TraceContext) request.getAttribute(TraceContextFilter.CONTEXT_ATTRIBUTE);
        String correlationId = (String) request.getAttribute(TraceContextFilter.CORRELATION_ATTRIBUTE);

        if (!finishedConversations.add(id)) {
            log.atWarn()
                    .addKeyValue("event", "conversation.finish.rejected")
                    .addKeyValue("conversation_id", id)
                    .addKeyValue("conversation_status", "FINISHED")
                    .addKeyValue("error_code", "CONFLICT")
                    .log("conversation is already finished");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "CONFLICT",
                    "message", "The conversation is already finished.",
                    "traceId", correlationId));
        }

        String summaryId = UUID.randomUUID().toString();
        OutboxEvent event = outbox.addSummaryGenerated(context, correlationId, id, summaryId);
        log.atInfo()
                .addKeyValue("event", "preconsultation.summary.generated")
                .addKeyValue("conversation_id", id)
                .addKeyValue("conversation_status", "FINISHED")
                .addKeyValue("summary_id", summaryId)
                .addKeyValue("outbox_event_id", event.eventId())
                .log("summary stored with outbox event");
        return ResponseEntity.ok(Map.of(
                "conversation", Map.of("id", id, "status", "FINISHED"),
                "preconsultationSummary", Map.of("id", summaryId, "conversationId", id)));
    }
}
