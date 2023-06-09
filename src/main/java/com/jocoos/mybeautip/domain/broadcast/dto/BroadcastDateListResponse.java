package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

public record BroadcastDateListResponse(
        @JsonFormat(pattern = LOCAL_DATE_FORMAT) List<LocalDate> dates
) {

}
