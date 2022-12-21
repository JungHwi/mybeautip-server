package com.jocoos.mybeautip.domain.community.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AdminMemberResponse {

    @JsonUnwrapped
    private final MemberResponse memberResponse;
    private final Role role;

    @QueryProjection
    public AdminMemberResponse(MemberResponse memberResponse, Integer link) {
        this.memberResponse = memberResponse;
        this.role = Role.from(link);
    }

    public static AdminMemberResponse from(Member member) {
        MemberResponse memberResponse = MemberResponse.from(member);
        return new AdminMemberResponse(memberResponse, member.getLink());
    }
}
