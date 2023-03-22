package com.jocoos.mybeautip.domain.broadcast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.member.dto.MemberIdAndUsernameResponse;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
public class BroadcastReportResponse {

    private final Long id;
    private final MemberIdAndUsernameResponse reporter;
    private final MemberIdAndUsernameResponse reported;
    private final String description;
    private final String reason;
    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @QueryProjection
    public BroadcastReportResponse(BroadcastReport report,
                                   MemberIdAndUsernameResponse reporter,
                                   MemberIdAndUsernameResponse reported) {
        this.id = report.getId();
        this.reporter = reporter;
        this.reported = reported;
        this.description = report.getDescription();
        this.reason = report.getReason();
        this.createdAt = report.getCreatedAt();
    }
}
