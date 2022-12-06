package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

@Data
@Builder
public class ExceptionMemberResponse {
    private long memberId;

    @JsonFormat(pattern = LOCAL_DATE_FORMAT)
    private ZonedDateTime date;
}