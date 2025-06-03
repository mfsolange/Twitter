package com.uala.mapper;

import com.uala.dto.TweetResponseDto;
import com.uala.domain.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetMapper {

    public TweetResponseDto toDto(Tweet tweet) {
        return new TweetResponseDto(
                tweet.getUserId(),
                tweet.getContent(),
                tweet.getCreatedAt()
        );
    }
}
