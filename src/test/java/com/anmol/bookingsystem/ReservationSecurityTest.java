package com.anmol.bookingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ReservationSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotCreateResource_shouldReturnForbidden() throws Exception {
        setupMockMvc();

        String body = """
                {
                  "name": "Test Room",
                  "type": "Room",
                  "description": "test",
                  "available": true
                }
                """;

        mockMvc.perform(post("/resources")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanCreateResource_shouldReturnOk() throws Exception {
        setupMockMvc();

        String body = """
                {
                  "name": "Admin Test Room",
                  "type": "Room",
                  "description": "created by admin",
                  "available": true
                }
                """;

        mockMvc.perform(post("/resources")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedRequest_shouldReturnUnauthorized() throws Exception {
        setupMockMvc();

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isUnauthorized());
    }
}