package com.sporty.service.feed.standardization.controller;

import com.sporty.service.feed.standardization.messaging.Messenger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FeedNormalizerControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    Messenger messenger;

    @Test
    void alphaOddsUpdateReturns202() throws Exception {
        String body = """
                {
                  "msg_type": "odds_update",
                  "event_id": "ev123",
                  "values": { "1": 2.0, "X": 3.1, "2": 3.8 }
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void betaSettlementReturns202() throws Exception {
        String body = """
                {
                  "type": "SETTLEMENT",
                  "event_id": "ev456",
                  "result": "away"
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void unknownMessageTypeReturns400() throws Exception {
        String body = """
                {
                  "msg_type": "unknown_type",
                  "event_id": "ev123"
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("unknown_type")));
    }

    @Test
    void missingRequiredFieldReturns400() throws Exception {
        String body = """
                {
                  "msg_type": "odds_update",
                  "values": { "1": 2.0, "X": 3.1, "2": 3.8 }
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("event_id")));
    }
}
