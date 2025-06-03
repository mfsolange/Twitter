package com.uala.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FollowRequestDto {
    @NotNull(message = "followedId is required")
    private UUID followedId;
}
