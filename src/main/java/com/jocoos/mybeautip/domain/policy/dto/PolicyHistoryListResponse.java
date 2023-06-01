package com.jocoos.mybeautip.domain.policy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.global.code.CountryCode;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record PolicyHistoryListResponse(long id,
                                        CountryCode countryCode,
                                        @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt) {
}
