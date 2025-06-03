package com.uala.domain;

import java.util.List;

public record PaginatedResult<T>(List<T> items, boolean hasMore) {

}
