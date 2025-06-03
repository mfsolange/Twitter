package com.uala.service;

import com.uala.repository.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void follow(UUID followerId, UUID followedId) {
        followRepository.save(followerId, followedId);
    }
}
