package com.uala.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uala.Application;
import com.uala.model.FollowEntity;
import com.uala.model.TweetEntity;
import com.uala.repository.SpringDataFollowRepository;
import com.uala.repository.SpringDataTweetRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class TimelineControllerIntegrationTest {

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
    private TestEntityManager entityManager;

    @Autowired
    private SpringDataTweetRepository springDataTweetRepository;

    @Autowired
    private SpringDataFollowRepository springDataFollowRepository;

    private UUID userId;
    private UUID followed1;
    private UUID followed2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        followed1 = UUID.randomUUID();
        followed2 = UUID.randomUUID();

        createFollowRelationships();
        createTestTweets();
    }

    @Test
    void shouldGetTimelineWithDefaultPagination() throws Exception {
        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tweets").isArray())
                .andExpect(jsonPath("$.data.tweets", hasSize(lessThanOrEqualTo(10))))
                .andExpect(jsonPath("$.data.pagination.limit").value(10))
                .andExpect(jsonPath("$.data.pagination.offset").value(0))
                .andExpect(jsonPath("$.data.pagination.hasMore").exists())
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void shouldGetTimelineWithCustomPagination() throws Exception {
        int customLimit = 5;
        int customOffset = 2;

        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .param("limit", String.valueOf(customLimit))
                        .param("offset", String.valueOf(customOffset))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tweets").isArray())
                .andExpect(jsonPath("$.data.tweets", hasSize(lessThanOrEqualTo(customLimit))))
                .andExpect(jsonPath("$.data.pagination.limit").value(customLimit))
                .andExpect(jsonPath("$.data.pagination.offset").value(customOffset))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void shouldReturnEmptyTimelineForUserWithNoFollows() throws Exception {
        UUID userWithNoFollows = UUID.randomUUID();

        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userWithNoFollows.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tweets").isArray())
                .andExpect(jsonPath("$.data.tweets", hasSize(0)))
                .andExpect(jsonPath("$.data.pagination.hasMore").value(false))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenUserIdHeaderMissing() throws Exception {
        mockMvc.perform(get("/timeline")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidUserIdFormat() throws Exception {
        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleNegativePaginationParameters() throws Exception {
        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .param("limit", "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("Page size must not be less than one"));
    }

    @Test
    void shouldHandleInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .param("limit", "asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("TYPE_MISMATCH"))
                .andExpect(jsonPath("$.error.message").value("Invalid value for parameter 'limit'"));
    }

    @Test
    void shouldVerifyTweetStructureInResponse() throws Exception {
        mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .param("limit", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tweets[0].content").exists())
                .andExpect(jsonPath("$.data.tweets[0].userId").exists())
                .andExpect(jsonPath("$.data.tweets[0].createdAt").exists())
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void shouldRespectTimelineOrderingByCreationTime() throws Exception {
        MvcResult result = mockMvc.perform(get("/timeline")
                        .header("X-User-Id", userId.toString())
                        .param("limit", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseBody);
        JsonNode tweets = jsonResponse.get("data").get("tweets");

        if (tweets.size() > 1) {
            for (int i = 0; i < tweets.size() - 1; i++) {
                String currentTweetTime = tweets.get(i).get("createdAt").asText();
                String nextTweetTime = tweets.get(i + 1).get("createdAt").asText();
                assertTrue(currentTweetTime.compareTo(nextTweetTime) >= 0);
            }
        }
    }

    private void createFollowRelationships() {
        springDataFollowRepository.save(new FollowEntity(userId, followed1));
        springDataFollowRepository.save(new FollowEntity(userId, followed2));
    }

    private void createTestTweets() {
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 15; i++) {
            TweetEntity tweet1 = new TweetEntity(UUID.randomUUID(), followed1,
                    "Test tweet " + i + " from user 1", now.minusDays(i));
            TweetEntity tweet2 = new TweetEntity(UUID.randomUUID(), followed2,
                    "Test tweet " + i + " from user 2", now.minusDays(i).minusHours(1));
            springDataTweetRepository.save(tweet1);
            springDataTweetRepository.save(tweet2);
        }

        entityManager.flush();
    }
}