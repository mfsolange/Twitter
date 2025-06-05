package com.uala.service;

import com.uala.repository.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class FollowServiceTest {

    private FollowRepository followRepository;
    private FollowService followService;

    @BeforeEach
    void setUp() {
        followRepository = mock(FollowRepository.class);
        followService = new FollowService(followRepository);
    }

    @Test
    void shouldCallRepositoryToFollow() {
        UUID followerId = UUID.randomUUID();
        UUID followedId = UUID.randomUUID();

        followService.follow(followerId, followedId);

        verify(followRepository).save(followerId, followedId);
        verifyNoMoreInteractions(followRepository);
    }
}
