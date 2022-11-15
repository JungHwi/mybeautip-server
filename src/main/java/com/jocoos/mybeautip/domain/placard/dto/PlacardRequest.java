package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class PlacardRequest {

    private final PlacardStatus status;
    private final String imageUrl;
    private final String title;
    private final PlacardLinkType linkType;
    private final String linkArgument;
    private final String description;
    private final String color;
    private final @NotNull(message = "startAt must not be null") @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime startedAt;
    private final @NotNull(message = "endAt must not be null") @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime endedAt;

}
