package com.uala.repository;

import com.uala.model.FollowEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FollowRepository {

    private final SpringDataFollowRepository springDataFollowRepository;

    public FollowRepository(SpringDataFollowRepository springDataFollowRepository) {
        this.springDataFollowRepository = springDataFollowRepository;
    }

    public void save(UUID followerId, UUID followedId) {
        var entity = new FollowEntity(followerId, followedId);
        springDataFollowRepository.save(entity);
    }

    public List<UUID> findFollowedIds(UUID followerId) {
        return springDataFollowRepository.findByFollowerId(followerId)
                .stream()
                .map(FollowEntity::getFollowedId)
                .toList();
    }
}

