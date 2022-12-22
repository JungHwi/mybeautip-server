package com.jocoos.mybeautip.domain.placard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.placard.code.PlacardLinkType;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_TIME_FORMAT;

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
    @JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
    private final LocalDateTime startedAt;

    @NotNull
    @JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
    private final LocalDateTime endedAt;

    public ZonedDateTime startedAtToUTCZoned() {
        return toUTCZoned();
    }

    public ZonedDateTime endedAtToUTCZoned() {
        return toUTCZoned();
    }

    private ZonedDateTime toUTCZoned() {
        return ZonedDateTimeUtil.toUTCZoned(startedAt, ZoneId.of("Asia/Seoul"));
    }
}
