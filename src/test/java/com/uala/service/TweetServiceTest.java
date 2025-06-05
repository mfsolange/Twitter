package com.uala.service;

import com.uala.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TweetServiceTest {

    private TweetRepository tweetRepository;
    private TweetService tweetService;

    @BeforeEach
    void setUp() {
        tweetRepository = mock(TweetRepository.class);
        tweetService = new TweetService(tweetRepository);
    }

    @Test
    void shouldPublishTweetWhenValid() {
        UUID userId = UUID.randomUUID();
        String content = "This is a valid tweet";

        tweetService.publish(userId, content);

        verify(tweetRepository).save(argThat(tweet ->
                tweet.getUserId().equals(userId) &&
                tweet.getContent().equals(content)
        ));
    }

    @Test
    void shouldThrowWhenTweetTooLong() {
        UUID userId = UUID.randomUUID();
        String content = "x".repeat(281);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tweetService.publish(userId, content)
        );

        assertEquals("Tweet too long", exception.getMessage());
        verifyNoInteractions(tweetRepository);
    }

    @Test
    void shouldThrowWhenTweetIsEmpty() {
        UUID userId = UUID.randomUUID();
        String content = "";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tweetService.publish(userId, content)
        );

        assertEquals("Empty tweet", exception.getMessage());
        verifyNoInteractions(tweetRepository);
    }
}
