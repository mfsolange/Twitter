package com.uala.service;

import com.uala.repository.FollowRepository;
import com.uala.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TimelineServiceTest {

    private FollowRepository followRepository;
    private TweetRepository tweetRepository;
    private TimelineService timelineService;

    @BeforeEach
    void setUp() {
        followRepository = mock(FollowRepository.class);
        tweetRepository = mock(TweetRepository.class);
        timelineService = new TimelineService(followRepository, tweetRepository);
    }

    @Test
    void shouldCallRepositories() {
        UUID userId = UUID.randomUUID();
        List<UUID> followedIds = List.of(UUID.randomUUID());
        when(followRepository.findFollowedIds(userId)).thenReturn(followedIds);

        timelineService.getTimeline(userId, 10, 0);

        verify(followRepository).findFollowedIds(userId);
        verifyNoMoreInteractions(followRepository);

        verify(tweetRepository).findByUserIds(followedIds, 10, 0);
        verifyNoMoreInteractions(tweetRepository);
    }

}