package com.uala.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TweetResponseDto(UUID userId, String content, LocalDateTime createdAt) {
}
