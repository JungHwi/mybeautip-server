package com.jocoos.mybeautip.domain.store.dto;

import lombok.Builder;

@Builder
public record DeleteCategoryRequest(long newCategoryId) {
}
