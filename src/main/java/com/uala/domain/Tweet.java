package com.uala.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Tweet {
    private final UUID id;
    private final UUID userId;
    private final String content;
    private final LocalDateTime createdAt;

    public Tweet(UUID userId, String content) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
