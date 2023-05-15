package com.jocoos.mybeautip.domain.store.dto;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
public record SearchStoreCategoryRequest(List<StoreCategoryStatus> statuses,
                                         Pageable pageable) {
}
