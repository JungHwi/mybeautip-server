package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminPlacardResponse {

    private final Long id;
    private final PlacardStatus status;
    private final PlacardLinkType linkType;
    private final String imageUrl;
    private final String description;
    private final Boolean isTopFix;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime endAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @QueryProjection
    public AdminPlacardResponse(Placard placard) {
        this.id = placard.getId();
        this.status = placard.getStatus();
        this.linkType = placard.getLinkType();
        this.imageUrl = placard.getImageUrl();
        this.description = placard.getDescription();
        this.startAt = placard.getStartedAt();
        this.endAt = placard.getEndedAt();
        this.createdAt = placard.getCreatedAtZoned();
        this.isTopFix = placard.isTopFixTrueOrNull();
    }
}
