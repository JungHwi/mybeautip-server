package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminMemberReportResponse {

    private final Long id;
    private final MemberIdAndUsernameResponse accuser;
    private final String reason;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime reportedAt;
}
