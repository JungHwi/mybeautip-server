package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class AdminMemberResponse {
    private final Long id;
    private final MemberStatus status;
    private final String avatarUrl;
    private final String username;
    private final String email;
    private final int point;
    private final int reportCount;
    private final int orderCount;
    private final boolean isPushable;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime modifiedAt;
}
