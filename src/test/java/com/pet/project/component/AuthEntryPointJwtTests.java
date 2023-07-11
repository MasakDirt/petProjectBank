package com.pet.project.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthEntryPointJwtTests {
    private final MockMvc mvc;

    @Autowired
    public AuthEntryPointJwtTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mvc).isNotNull();
    }

    @Test
    public void testCommence_ReturnsUnauthorizedError() throws Exception {
        mvc.perform(get("/error")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertEquals("They must be equal, because there one ex, about not authenticate user",
                        "Error: Unauthorized (Full authentication is required to access this resource)",
                        result.getResponse().getErrorMessage()));
    }
}
