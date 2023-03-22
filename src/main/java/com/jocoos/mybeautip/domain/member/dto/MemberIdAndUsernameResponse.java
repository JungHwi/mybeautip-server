package com.jocoos.mybeautip.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MemberIdAndUsernameResponse {
    private final Long id;
    private final String username;

    @QueryProjection
    public MemberIdAndUsernameResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
