package com.uala.service;

import com.uala.domain.PaginatedResult;
import com.uala.domain.Tweet;
import com.uala.repository.FollowRepository;
import com.uala.repository.TweetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TimelineService {
    private final FollowRepository followRepository;
    private final TweetRepository tweetRepository;

    public PaginatedResult<Tweet> getTimeline(UUID userId, int limit, int offset) {
        List<UUID> followedIds = followRepository.findFollowedIds(userId);
        return tweetRepository.findByUserIds(followedIds, limit, offset);
    }
}
