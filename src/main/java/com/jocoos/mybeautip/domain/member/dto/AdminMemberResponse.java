package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public record AdminMemberResponse(Long id,
                                  MemberStatus status,
                                  String avatarUrl,
                                  String username,
                                  String email,
                                  GrantType grantType,
                                  int point,
                                  int communityCount,
                                  int commentCount,
                                  int reportCount,
                                  int orderCount,
                                  Boolean isPushable,
                                  Boolean isAgreeMarketingTerm,
                                  @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt,
                                  @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime modifiedAt) {
}
