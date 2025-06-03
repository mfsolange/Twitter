package com.uala.controller;

import com.uala.domain.PaginatedResult;
import com.uala.domain.Tweet;
import com.uala.dto.ApiResponse;
import com.uala.dto.PaginatedTweetsResponse;
import com.uala.dto.PaginationMetadata;
import com.uala.dto.TweetResponseDto;
import com.uala.mapper.TweetMapper;
import com.uala.service.TimelineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/timeline")
public class TimelineController {

    private final TimelineService timelineService;
    private final TweetMapper tweetMapper;

    public TimelineController(TimelineService timelineService, TweetMapper tweetMapper) {
        this.timelineService = timelineService;
        this.tweetMapper = tweetMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedTweetsResponse>> getTimeline(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        PaginatedResult<Tweet> timeline = timelineService.getTimeline(userId, limit, offset);
        List<TweetResponseDto> dtoList = timeline.items().stream()
                .map(tweetMapper::toDto)
                .toList();
        PaginationMetadata pagination = new PaginationMetadata(limit, offset, timeline.hasMore());
        return ResponseEntity.ok(ApiResponse.success(new PaginatedTweetsResponse(dtoList, pagination)));
    }
}

