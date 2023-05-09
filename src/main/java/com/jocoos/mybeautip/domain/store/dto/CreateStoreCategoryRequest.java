package com.jocoos.mybeautip.domain.store.dto;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateStoreCategoryRequest(StoreCategoryStatus status,
                                         String name,
                                         List<StoreCategoryDetailDto> categoryDetailList) {
}
