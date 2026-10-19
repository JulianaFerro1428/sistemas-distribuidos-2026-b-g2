package com.telemed.intelligentagent.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Reads or creates the trace context, stores it in the MDC and returns it in the response.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceContextFilter extends OncePerRequestFilter {

    public static final String CORRELATION_HEADER = "X-Correlation-Id";
    public static final String CONTEXT_ATTRIBUTE = TraceContext.class.getName();
    public static final String CORRELATION_ATTRIBUTE = "correlationId";

    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9._-]{1,128}");
    private static final Logger log = LoggerFactory.getLogger(TraceContextFilter.class);

    private final AgentMetrics metrics;

    public TraceContextFilter(AgentMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        TraceContext context = TraceContext.fromHeader(request.getHeader(TraceContext.HEADER));
        String incomingId = request.getHeader(CORRELATION_HEADER);
        String correlationId = incomingId != null && SAFE_ID.matcher(incomingId).matches()
                ? incomingId
                : UUID.randomUUID().toString();

        MDC.put("trace_id", context.traceId());
        MDC.put("span_id", context.spanId());
        MDC.put("correlation_id", correlationId);
        request.setAttribute(CONTEXT_ATTRIBUTE, context);
        request.setAttribute(CORRELATION_ATTRIBUTE, correlationId);
        response.setHeader(TraceContext.HEADER, context.toHeader());
        response.setHeader(CORRELATION_HEADER, correlationId);

        long started = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationNanos = System.nanoTime() - started;
            Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String route = pattern != null ? pattern.toString() : "unmatched";
            metrics.record(route, request.getMethod(), response.getStatus(), durationNanos);
            long durationMs = durationNanos / 1_000_000;
            log.atInfo()
                    .addKeyValue("event", "http.request.completed")
                    .addKeyValue("duration_ms", durationMs)
                    .log("{} {} -> {}", request.getMethod(), route, response.getStatus());
            MDC.clear();
        }
    }
}
