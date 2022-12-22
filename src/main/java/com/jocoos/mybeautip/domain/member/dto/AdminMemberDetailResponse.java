package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminMemberDetailResponse {
    private final Long id;
    private final Role role;
    private final GrantType grantType;
    private final String avatarUrl;
    private final String username;
    private final String name;
    private final String email;
    private final String phoneNumber;

    private final Integer ageGroup;
    private final SkinType skinType;
    private final Set<SkinWorry> skinWorry;
    private final String address;

    private final Boolean isPushable;
    private final Boolean isAgreeMarketingTerm;

    private final int point;
    private final int expiryPoint;
    private final int normalCommunityCount;
    private final int normalCommunityCommentCount;
    private final int normalVideoCommentCount;
    private final int totalCommunityCount;
    private final int totalCommunityCommentCount;
    private final int totalVideoCommentCount;
    private final Long invitedFriendCount;
    private final List<MemoResponse> memo;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime modifiedAt;
}
