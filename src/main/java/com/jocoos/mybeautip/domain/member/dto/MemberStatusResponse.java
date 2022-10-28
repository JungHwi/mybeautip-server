package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class MemberStatusResponse {
    private final MemberStatus status;
    private final long count;

    @QueryProjection
    public MemberStatusResponse(MemberStatus status, long count) {
        this.status = status;
        this.count = count;
    }
}
