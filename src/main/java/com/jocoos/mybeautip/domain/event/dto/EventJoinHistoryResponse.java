package com.jocoos.mybeautip.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_TIME_FORMAT;

@Getter
@Builder
public class EventJoinHistoryResponse {
    private String title;

    private String description;

    @JsonFormat(pattern = LOCAL_DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
}
