package com.jocoos.mybeautip.domain.store.dto;

import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;

import java.util.List;

public record StoreCategoryResponse(long id,
                                    String code,
                                    StoreCategoryStatus status,
                                    String name,
                                    List<StoreCategoryDetailDto> categoryDetailList) {
}
