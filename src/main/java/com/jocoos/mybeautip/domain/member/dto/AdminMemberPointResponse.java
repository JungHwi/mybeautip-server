package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.point.code.PointStatus;
import com.jocoos.mybeautip.member.point.MemberPoint;
import lombok.Builder;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.point.code.PointStatus.getPointStatus;
import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record AdminMemberPointResponse(Long id,
                                       PointStatus status,
                                       String reason,
                                       int point,
                                       @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime earnedAt,
                                       @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime expiryAt) {

    public static AdminMemberPointResponse from(MemberPoint memberPoint) {
        return AdminMemberPointResponse.builder()
                .id(memberPoint.getId())
                .status(getPointStatus(memberPoint.getState()))
                .reason(memberPoint.getReason())
                .point(memberPoint.getPoint())
                .earnedAt(memberPoint.getCreatedAtZoned())
                .expiryAt(memberPoint.getExpiryAtZoned())
                .build();
    }
}
