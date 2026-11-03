package com.akabli.booking.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test de bout en bout : login JWT -> consultation -> réservation -> sécurité des rôles. */
@SpringBootTest
@AutoConfigureMockMvc
class BookingFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String login(String email, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void offersArePublic() throws Exception {
        mockMvc.perform(get("/api/offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void bookingWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"offerId\":1,\"seats\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clientCanBookAndSeeHisBooking() throws Exception {
        String token = login("client@demo.ma", "Client123!");
        JsonNode offer = objectMapper.readTree(mockMvc.perform(get("/api/offers?type=VOYAGE"))
                .andReturn().getResponse().getContentAsString()).get(0);
        long offerId = offer.get("id").asLong();
        int seatsBefore = offer.get("availableSeats").asInt();

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"offerId\":" + offerId + ",\"seats\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/offers/" + offerId))
                .andExpect(jsonPath("$.availableSeats").value(seatsBefore - 2));

        mockMvc.perform(get("/api/bookings/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].offerId").value(offerId));
    }

    @Test
    void clientCannotAccessAdminEndpoints() throws Exception {
        String token = login("client@demo.ma", "Client123!");

        mockMvc.perform(get("/api/admin/bookings").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"client@demo.ma\",\"password\":\"mauvais\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidBookingReturns400() throws Exception {
        String token = login("client@demo.ma", "Client123!");

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"offerId\":1,\"seats\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.seats").exists());
    }
}
