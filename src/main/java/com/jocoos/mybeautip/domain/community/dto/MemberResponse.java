package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.jocoos.mybeautip.global.code.UrlDirectory.AVATAR;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponse {

    private Long id;

    private MemberStatus status;

    private String username;

    private String avatarUrl;

    @QueryProjection
    public MemberResponse(Long id,
                          MemberStatus status,
                          String username,
                          String avatarFilename) {
        this.id = id;
        this.status = status;
        this.username = username;
        this.avatarUrl = toUrl(avatarFilename, AVATAR);
    }

    @QueryProjection
    public MemberResponse(Long id, String username) {
        this(id, null, username, null);
    }


    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getStatus(),
                member.getUsername(),
                member.getAvatarFilename());
    }

    public void blind() {
        this.id = null;
        this.username = null;
        this.avatarUrl = null;
    }
}
