package com.uala.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedTweetsResponse {
    private List<TweetResponseDto> tweets;
    private PaginationMetadata pagination;
}

