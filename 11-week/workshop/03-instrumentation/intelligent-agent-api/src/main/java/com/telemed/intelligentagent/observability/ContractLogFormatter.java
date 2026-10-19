package com.telemed.intelligentagent.observability;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.springframework.boot.json.JsonWriter;
import org.springframework.boot.logging.structured.StructuredLogFormatter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes every log line with the CP2 log contract fields.
 */
public class ContractLogFormatter implements StructuredLogFormatter<ILoggingEvent> {

    private static final String SERVICE = "intelligent-agent-api";
    private static final String VERSION = System.getenv().getOrDefault("SERVICE_VERSION", "0.1.0");
    private static final String ENVIRONMENT = System.getenv().getOrDefault("ENVIRONMENT", "dev");

    private final JsonWriter<Map<String, Object>> writer =
            JsonWriter.<Map<String, Object>>standard().withNewLineAtEnd();

    @Override
    public String format(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("timestamp", Instant.ofEpochMilli(event.getTimeStamp()).toString());
        line.put("level", event.getLevel().toString());
        line.put("service", SERVICE);
        line.put("version", VERSION);
        line.put("environment", ENVIRONMENT);
        line.put("trace_id", mdc.get("trace_id"));
        line.put("span_id", mdc.get("span_id"));
        line.put("correlation_id", mdc.get("correlation_id"));
        line.put("event", "log");
        line.put("message", event.getFormattedMessage());
        line.put("duration_ms", 0);
        if (event.getKeyValuePairs() != null) {
            for (KeyValuePair pair : event.getKeyValuePairs()) {
                line.put(pair.key, pair.value);
            }
        }
        return writer.writeToString(line);
    }
}
