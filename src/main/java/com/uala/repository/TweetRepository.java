package com.uala.repository;

import com.uala.domain.PaginatedResult;
import com.uala.domain.Tweet;
import com.uala.model.TweetEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TweetRepository {

    private final SpringDataTweetRepository springDataTweetRepository;

    public TweetRepository(SpringDataTweetRepository springDataTweetRepository) {
        this.springDataTweetRepository = springDataTweetRepository;
    }

    public void save(Tweet tweet) {
        var entity = new TweetEntity(tweet.getId(), tweet.getUserId(), tweet.getContent(), tweet.getCreatedAt());
        springDataTweetRepository.save(entity);
    }

    public PaginatedResult<Tweet> findByUserIds(List<UUID> userIds, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<Tweet> tweets = springDataTweetRepository.findTimelineForUser(userIds, pageable)
                .stream()
                .map(e -> new Tweet(e.getId(), e.getUserId(), e.getContent(), e.getCreatedAt()))
                .toList();
        return new PaginatedResult<>(tweets, tweets.size() == limit);
    }
}

