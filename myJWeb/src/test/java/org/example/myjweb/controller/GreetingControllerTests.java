package org.example.myjweb.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.myjweb.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class GreetingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GreetingService greetingService;

    @Test
    @WithMockUser
    void shouldReturnGreeting() throws Exception {
        when(greetingService.greet("Bob")).thenReturn("Hello, Bob");

        mockMvc.perform(get("/api/hello").param("name", "Bob"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Hello, Bob\"}"));
    }
}
