package com.uala.controller;

import com.uala.dto.ApiResponse;
import com.uala.dto.FollowRequestDto;
import com.uala.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/follows")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> followUser(
            @RequestHeader("X-User-Id") UUID followerId,
            @Valid @RequestBody FollowRequestDto request) {

        followService.follow(followerId, request.getFollowedId());
        return ResponseEntity.ok().build();
    }
}

