package com.uala.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uala.Application;
import com.uala.dto.TweetRequestDto;
import com.uala.model.TweetEntity;
import com.uala.repository.SpringDataTweetRepository;
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
class TweetControllerIntegrationTest {

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
    private SpringDataTweetRepository springDataTweetRepository;

    @Test
    void shouldPersistTweetInDB() throws Exception {
        UUID followerId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());
        String content = "This is a tweet";
        TweetRequestDto body = new TweetRequestDto(content);
        mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<TweetEntity> all = springDataTweetRepository.findAll();
        assertThat(all).size().isEqualTo(1);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    void shouldReturn400WhenContentIsMissing() throws Exception {

        UUID followerId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());
        mockMvc.perform(post("/tweets")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("content: must not be blank"));
    }

    @Test
    void shouldReturn400WhenContentIsEmpty() throws Exception {

        UUID followerId = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());
        String content = "";
        TweetRequestDto body = new TweetRequestDto(content);
        mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("content: must not be blank"));
    }

    @Test
    void shouldReturn400WhenHeaderIsMissing() throws Exception {
        TweetRequestDto body = new TweetRequestDto("This is a tweet");
        mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Missing required header: X-User-Id"));
    }

    @Test
    void shouldReturn400WhenHeaderIsInvalidUUID() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "not-a-uuid");
        TweetRequestDto body = new TweetRequestDto("This is a tweet");
        mockMvc.perform(post("/tweets")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid value for parameter 'X-User-Id'"));
    }

    @Test
    void shouldReturn400WhenBodyIsMalformedJson() throws Exception {
        String malformedJson = "{ content: ";
        UUID followerId = UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", followerId.toString());

        mockMvc.perform(post("/tweets")
                        .content(malformedJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Malformed request body"));
    }
}
