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

    @NotNull
    private final PlacardStatus status;

    @NotNull
    private final String imageUrl;

    @NotNull
    private final String title;

    @NotNull
    private final PlacardLinkType linkType;

    private final String linkArgument;

    private final String description;

    @NotNull
    private final String color;

    @NotNull
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime startedAt;

    @NotNull
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime endedAt;
}
