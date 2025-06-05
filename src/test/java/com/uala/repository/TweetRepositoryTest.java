package com.uala.repository;

import com.uala.domain.PaginatedResult;
import com.uala.domain.Tweet;
import com.uala.model.TweetEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TweetRepositoryTest {

    private SpringDataTweetRepository springDataTweetRepository;
    private TweetRepository tweetRepository;

    @BeforeEach
    void setUp() {
        springDataTweetRepository = mock(SpringDataTweetRepository.class);
        tweetRepository = new TweetRepository(springDataTweetRepository);
    }

    @Test
    void shouldSaveTweetAsEntity() {
        UUID userId = UUID.randomUUID();
        String content = "This is a tweet";

        Tweet tweet = new Tweet(userId, content);
        tweetRepository.save(tweet);

        verify(springDataTweetRepository).save(argThat(entity ->
                entity.getId().equals(tweet.getId()) &&
                entity.getUserId().equals(userId) &&
                entity.getContent().equals(content) &&
                entity.getCreatedAt().equals(tweet.getCreatedAt())
        ));
    }

    @Test
    void shouldReturnPaginatedTweets() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        TweetEntity e1 = new TweetEntity(UUID.randomUUID(), userId1, "Tweet 1", LocalDateTime.now());
        TweetEntity e2 = new TweetEntity(UUID.randomUUID(), userId2, "Tweet 2", LocalDateTime.now());

        List<TweetEntity> entities = List.of(e1, e2);
        when(springDataTweetRepository.findTimelineForUser(anyList(), any(Pageable.class)))
                .thenReturn(entities);

        PaginatedResult<Tweet> result = tweetRepository.findByUserIds(List.of(userId1, userId2), 2, 0);

        assertEquals(2, result.items().size());
        assertTrue(result.hasMore());
        assertEquals(e1.getContent(), result.items().get(0).getContent());
    }

    @Test
    void shouldIndicateNoMoreResultsWhenLessThanLimit() {
        UUID userId = UUID.randomUUID();
        TweetEntity e1 = new TweetEntity(UUID.randomUUID(), userId, "Tweet 1", LocalDateTime.now());

        when(springDataTweetRepository.findTimelineForUser(anyList(), any(Pageable.class)))
                .thenReturn(List.of(e1));

        PaginatedResult<Tweet> result = tweetRepository.findByUserIds(List.of(userId), 10, 0);

        assertEquals(1, result.items().size());
        assertFalse(result.hasMore());
    }
}
