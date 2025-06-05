package com.uala.service;

import com.uala.domain.Tweet;
import com.uala.repository.TweetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TweetService {
    private final TweetRepository tweetRepository;

    public void publish(UUID userId, String content) {
        if (content.length() > 280) throw new IllegalArgumentException("Tweet too long");
        if (content.isEmpty()) throw new IllegalArgumentException("Empty tweet");
        Tweet tweet = new Tweet(userId, content);
        tweetRepository.save(tweet);
    }
}
