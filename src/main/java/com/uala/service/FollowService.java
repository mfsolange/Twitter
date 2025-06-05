package com.uala.service;

import com.uala.repository.FollowRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;

    public void follow(UUID followerId, UUID followedId) {
        followRepository.save(followerId, followedId);
    }
}
