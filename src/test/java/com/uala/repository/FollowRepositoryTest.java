package com.uala.repository;

import com.uala.model.FollowEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class FollowRepositoryTest {

    private SpringDataFollowRepository springDataFollowRepository;
    private FollowRepository followRepository;

    @BeforeEach
    void setUp() {
        springDataFollowRepository = mock(SpringDataFollowRepository.class);
        followRepository = new FollowRepository(springDataFollowRepository);
    }

    @Test
    void shouldSaveFollowAsEntity() {
        UUID followerId = UUID.randomUUID();
        UUID followedId = UUID.randomUUID();

        followRepository.save(followerId, followedId);

        verify(springDataFollowRepository).save(argThat(entity ->
                entity.getFollowerId().equals(followerId) &&
                entity.getFollowedId().equals(followedId))
        );
    }

    @Test
    void shouldReturnFollowers() {
        UUID userId = UUID.randomUUID();

        FollowEntity e1 = new FollowEntity(userId, UUID.randomUUID());
        FollowEntity e2 = new FollowEntity(userId, UUID.randomUUID());

        List<FollowEntity> entities = List.of(e1, e2);
        when(springDataFollowRepository.findByFollowerId(any()))
                .thenReturn(entities);

        List<UUID> followedIds = followRepository.findFollowedIds(userId);

        assertEquals(2, followedIds.size());
        assertEquals(e1.getFollowedId(), followedIds.get(0));
        assertEquals(e2.getFollowedId(), followedIds.get(1));
    }
}