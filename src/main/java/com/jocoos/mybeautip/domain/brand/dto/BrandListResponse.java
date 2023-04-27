package com.jocoos.mybeautip.domain.brand.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.brand.code.BrandStatus;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record BrandListResponse(long id,
                                BrandStatus status,
                                String code,
                                String name,
                                @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt) {
}
