package com.jocoos.mybeautip.domain.scrap.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record ScrapResponseV2(Long id,
                              Long relationId,
                              Boolean isScrap,
                              @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt) {

    public static ScrapResponseV2 from(Scrap scrap) {
        return ScrapResponseV2.builder()
                .id(scrap.getId())
                .relationId(scrap.getRelationId())
                .isScrap(scrap.getIsScrap())
                .createdAt(scrap.getCreatedAt())
                .build();
    }
}
