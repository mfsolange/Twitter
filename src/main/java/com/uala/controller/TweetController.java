package com.uala.controller;

import com.uala.dto.ApiResponse;
import com.uala.dto.TweetRequestDto;
import com.uala.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> publishTweet(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody TweetRequestDto request) {

        tweetService.publish(userId, request.getContent());
        return ResponseEntity.ok().build();
    }
}
