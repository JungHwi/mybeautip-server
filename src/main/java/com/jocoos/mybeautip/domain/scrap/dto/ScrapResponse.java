package com.jocoos.mybeautip.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@RequiredArgsConstructor
@Getter
public class ScrapResponse {
    private final Long id;
    private final ScrapType type;
    private final Long communityId;
    private final Boolean isScrap;
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;
}
