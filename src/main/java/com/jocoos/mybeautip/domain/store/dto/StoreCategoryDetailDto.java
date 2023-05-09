package com.jocoos.mybeautip.domain.store.dto;

import com.jocoos.mybeautip.global.code.CountryCode;

public record StoreCategoryDetailDto(CountryCode country,
                                     String name) {
}
