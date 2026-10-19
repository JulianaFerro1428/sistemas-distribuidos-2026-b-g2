package com.telemed.intelligentagent.preconsultation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
class TracePropagationTest {

    private static final String TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";
    private static final String INCOMING = "00-" + TRACE_ID + "-00f067aa0ba902b7-01";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryOutbox outbox;

    @Test
    void traceIdReceivedIsTheOneSentToTheOutbox() throws Exception {
        MvcResult result = finish(UUID.randomUUID().toString(), INCOMING)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(traceIdOf(result.getResponse().getHeader("traceparent"))).isEqualTo(TRACE_ID);
        List<OutboxEvent> events = outbox.events();
        OutboxEvent event = events.get(events.size() - 1);
        assertThat(event.type()).isEqualTo("PreConsultationSummaryGenerated");
        assertThat(traceIdOf(event.headers().get("traceparent"))).isEqualTo(TRACE_ID);
    }

    @Test
    void newTraceIsCreatedWhenHeaderIsMissing() throws Exception {
        MvcResult result = finish(UUID.randomUUID().toString(), null).andReturn();

        String traceId = traceIdOf(result.getResponse().getHeader("traceparent"));
        assertThat(traceId).hasSize(32).isNotEqualTo(TRACE_ID);
    }

    @Test
    void finishingTwiceIsRejectedAndCountedInMetrics() throws Exception {
        String conversationId = UUID.randomUUID().toString();
        finish(conversationId, INCOMING).andExpect(status().isOk());
        finish(conversationId, INCOMING).andExpect(status().isConflict());

        String metrics = mockMvc.perform(get("/actuator/prometheus"))
                .andReturn().getResponse().getContentAsString();
        assertThat(metrics).contains("agent_requests_total{method=\"POST\",outcome=\"client_error\"");
        assertThat(metrics).contains("agent_request_duration_seconds_bucket");
    }

    private ResultActions finish(String conversationId, String traceparent) throws Exception {
        var request = post("/api/v1/agent/conversations/{id}/finish", conversationId);
        if (traceparent != null) {
            request.header("traceparent", traceparent);
        }
        return mockMvc.perform(request);
    }

    private static String traceIdOf(String traceparent) {
        return traceparent.split("-")[1];
    }
}
