package com.uala.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TweetRequestDto {
    @NotBlank
    private String content;
}
