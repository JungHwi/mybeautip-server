package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record AdminMemberReportResponse(String id,
                                        MemberIdAndUsernameResponse accuser,
                                        String reason,
                                        @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime reportedAt) {

}
