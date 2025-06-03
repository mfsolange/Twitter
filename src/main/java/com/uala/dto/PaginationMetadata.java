package com.uala.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaginationMetadata {
    private int limit;
    private int offset;
    private boolean hasMore;
}

