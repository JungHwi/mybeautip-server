package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

public record BroadcastStartedAtResponse(
        @JsonFormat(pattern = LOCAL_DATE_FORMAT) List<ZonedDateTime> startedAt
) {

}
