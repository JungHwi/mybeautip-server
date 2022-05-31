package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.security.SocialMember;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "naver_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NaverMember extends CreatedDateAuditable {

    @Id
    @Column(nullable = false, length = 30)
    private String naverId;

    @Column(length = 30)
    private String nickname;

    @Column(nullable = false)
    private Long memberId;

    public NaverMember(String naverId, String nickname, Long memberId) {
        this.naverId = naverId;
        this.nickname = nickname;
        this.memberId = memberId;
    }

    public NaverMember(SocialMember socialMember, long memberId) {
        this.naverId = socialMember.getId();
        this.nickname = socialMember.getName();
        this.memberId = memberId;
    }
}
