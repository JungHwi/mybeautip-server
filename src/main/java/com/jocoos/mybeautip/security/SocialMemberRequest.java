package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.global.exception.MybeautipException;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialMemberRequest {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String provider;
    private String refreshToken;

    public Member toMember() {
        Member member = new Member();

        member.setLink(getLinkType());
        member.setEmail(this.email);
        member.setUsername(this.name);
        member.setPushable(true);
        member.setAvatarFilenameFromUrl(this.picture);
        member.setPermission(Member.PERMISSION_ALL);

        return member;
    }

    public void changePictureUrl(String uploadAvatarUrl) {
        this.picture = uploadAvatarUrl;
    }

    private int getLinkType() {
        switch (provider) {
            case "kakao":
                return Member.LINK_KAKAO;
            case "naver":
                return Member.LINK_NAVER;
            case "facebook":
                return Member.LINK_FACEBOOK;
            case "apple":
                return Member.LINK_APPLE;
            default:
                throw new MybeautipException("Unsupported provider type");
        }
    }
}
