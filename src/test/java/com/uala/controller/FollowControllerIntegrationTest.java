package com.uala.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uala.Application;
import com.uala.dto.FollowRequestDto;
import com.uala.model.FollowEntity;
import com.uala.repository.SpringDataFollowRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class FollowControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("uala")
            .withUsername("user")
            .withPassword("pass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataFollowRepository springDataFollowRepository;

    @Test
    void shouldFollowUserAndPersistInDB() throws Exception {
        UUID followerId = UUID.randomUUID();
        UUID followedId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());
        FollowRequestDto body = new FollowRequestDto(followedId);
        mockMvc.perform(post("/follows")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<FollowEntity> followEntities = springDataFollowRepository.findByFollowerId(followerId);
        assertThat(followEntities).size().isEqualTo(1);
        assertThat(followEntities.get(0).getFollowedId()).isEqualTo(followedId);
    }

    @Test
    void shouldReturn400WhenFollowedIdIsMissing() throws Exception {

        UUID followerId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());
        mockMvc.perform(post("/follows")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("followedId: followedId is required"));
    }

    @Test
    void shouldReturn400WhenHeaderIsMissing() throws Exception {
        UUID followedId = UUID.randomUUID();
        FollowRequestDto body = new FollowRequestDto(followedId);
        mockMvc.perform(post("/follows")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Missing required header: X-User-Id"));
    }

    @Test
    void shouldReturn400WhenHeaderIsInvalidUUID() throws Exception {
        UUID followedId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "not-a-uuid");
        FollowRequestDto body = new FollowRequestDto(followedId);
        mockMvc.perform(post("/follows")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid value for parameter 'X-User-Id'"));
    }

    @Test
    void shouldReturn400WhenBodyIsMalformedJson() throws Exception {
        String malformedJson = "{ followedId: ";
        UUID followerId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());

        mockMvc.perform(post("/follows")
                        .content(malformedJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Malformed request body"));
    }
}
