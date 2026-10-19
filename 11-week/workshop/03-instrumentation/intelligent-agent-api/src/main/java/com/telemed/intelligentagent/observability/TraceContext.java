package com.telemed.intelligentagent.observability;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * W3C trace context (traceparent: 00-traceId-spanId-flags).
 */
public record TraceContext(String traceId, String spanId, String parentSpanId) {

    public static final String HEADER = "traceparent";

    private static final Pattern TRACEPARENT =
            Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-([0-9a-f]{2})$");
    private static final String INVALID_TRACE_ID = "0".repeat(32);
    private static final SecureRandom RANDOM = new SecureRandom();

    public static TraceContext newTrace() {
        return new TraceContext(randomHex(16), randomHex(8), null);
    }

    /**
     * Continues the caller's trace, or starts a new one when the header is missing or invalid.
     */
    public static TraceContext fromHeader(String traceparent) {
        if (traceparent == null) {
            return newTrace();
        }
        Matcher matcher = TRACEPARENT.matcher(traceparent);
        if (!matcher.matches() || INVALID_TRACE_ID.equals(matcher.group(1))) {
            return newTrace();
        }
        return new TraceContext(matcher.group(1), randomHex(8), matcher.group(2));
    }

    public TraceContext child() {
        return new TraceContext(traceId, randomHex(8), spanId);
    }

    public String toHeader() {
        return "00-" + traceId + "-" + spanId + "-01";
    }

    private static String randomHex(int bytes) {
        byte[] value = new byte[bytes];
        RANDOM.nextBytes(value);
        return HexFormat.of().formatHex(value);
    }
}
