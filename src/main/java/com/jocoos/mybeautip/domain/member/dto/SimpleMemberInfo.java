package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleMemberInfo {
    private Long id;
    private String email;
    private String username;
    private String avatarUrl;

    public SimpleMemberInfo(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.avatarUrl = member.getAvatarUrl();
    }

    public SimpleMemberInfo(Long id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
}
