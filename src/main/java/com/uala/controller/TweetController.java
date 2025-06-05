package com.uala.controller;

import com.uala.dto.ApiResponse;
import com.uala.dto.TweetRequestDto;
import com.uala.service.TweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> publishTweet(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody TweetRequestDto request) {

        tweetService.publish(userId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
