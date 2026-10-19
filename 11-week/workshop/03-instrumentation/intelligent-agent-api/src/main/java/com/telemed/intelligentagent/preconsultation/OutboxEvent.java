package com.telemed.intelligentagent.preconsultation;

import java.time.Instant;
import java.util.Map;

/**
 * Document stored in the `outbox_events` collection of MongoDB `intelligent_agent`.
 */
public record OutboxEvent(
        String eventId,
        String type,
        Instant occurredAt,
        Map<String, String> payload,
        Map<String, String> headers
) {
}
