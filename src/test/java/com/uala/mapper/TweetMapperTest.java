package com.uala.mapper;

import com.uala.domain.Tweet;
import com.uala.dto.TweetResponseDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TweetMapperTest {

    @Test
    void shouldMapTweetToDto() {
        UUID userId = UUID.randomUUID();
        String content = "This is a tweet";

        Tweet tweet = new Tweet(userId, content);
        TweetMapper mapper = new TweetMapper();

        TweetResponseDto dto = mapper.toDto(tweet);

        assertEquals(userId, dto.userId());
        assertEquals(content, dto.content());
        assertEquals(tweet.getCreatedAt(), dto.createdAt());
    }
}
