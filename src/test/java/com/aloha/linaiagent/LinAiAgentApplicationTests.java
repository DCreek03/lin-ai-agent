package com.aloha.linaiagent;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LinAiAgentApplicationTests {

    private static final String API_CONTEXT_PATH = "/api";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointStillWorks() throws Exception {
        mockMvc.perform(api(get("/api/health")))
            .andExpect(status().isOk())
            .andExpect(content().string("OK"));
    }

    @Test
    void successEnvelopeWorks() throws Exception {
        mockMvc.perform(api(get("/api/test/ok")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value("20000"))
            .andExpect(jsonPath("$.msg").value("success"))
            .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    void bizExceptionIsWrapped() throws Exception {
        mockMvc.perform(api(get("/api/test/biz-error")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("40000"))
            .andExpect(jsonPath("$.msg").value("business failure"));
    }

    @Test
    void validationErrorReturnsBadRequest() throws Exception {
        mockMvc.perform(api(post("/api/test/validate"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("40001"))
            .andExpect(jsonPath("$.msg").value("name is required"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(api(post("/api/test/validate"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("40001"))
            .andExpect(jsonPath("$.msg", containsString("request body is malformed")));
    }

    @Test
    void typeMismatchReturnsBadRequest() throws Exception {
        mockMvc.perform(api(get("/api/test/type-mismatch"))
                .param("age", "abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("40001"))
            .andExpect(jsonPath("$.msg", containsString("parameter age cannot be converted")));
    }

    @Test
    void requestBodyCanBeReadMoreThanOnce() throws Exception {
        mockMvc.perform(api(post("/api/test/body"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"alice\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.first").value("{\"name\":\"alice\"}"))
            .andExpect(jsonPath("$.data.second").value("{\"name\":\"alice\"}"));
    }

    @Test
    void corsPreflightIsAllowed() throws Exception {
        mockMvc.perform(api(options("/api/test/ok"))
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    private MockHttpServletRequestBuilder api(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.contextPath(API_CONTEXT_PATH);
    }
}
