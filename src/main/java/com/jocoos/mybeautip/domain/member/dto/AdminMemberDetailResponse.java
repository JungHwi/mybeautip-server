package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminMemberDetailResponse {
    private final Long id;
    private final String avatarUrl;
    private final String username;
    private final String email;
    private final int point;
    private final Boolean isPushable;
    private final GrantType grantType;
    private final Integer ageGroup;
    private final SkinType skinType;
    private final Set<SkinWorry> skinWorry;
    private final String address;

    @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT)
    private final ZonedDateTime createdAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT)
    private final ZonedDateTime modifiedAt;

    private final int expiryPoint;
    private final Boolean isAgreeMarketingTerm;
    private final Long communityCount;
    private final Long communityCommentCount;
    private final Long videoCommentCount;
    private final Long invitedFriendCount;
}
