package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityMemberResponse {

    private Long id;

    private MemberStatus status;

    private String username;

    private String avatarUrl;

    public void blind() {
        this.id = null;
        this.username = null;
        this.avatarUrl = null;
    }

    @QueryProjection
    public CommunityMemberResponse(Long id, MemberStatus status, String username, String avatarUrl) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
}
