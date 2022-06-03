package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
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
public class NaverMember extends CreatedDateAuditable implements SocialMember {

    @Id
    @Column(nullable = false, length = 30)
    private String naverId;

    @Column(length = 30)
    private String nickname;

    @Column(nullable = false)
    private Long memberId;

    public NaverMember(SignupRequest signupRequest, long memberId) {
        this.naverId = signupRequest.getSocialId();
        this.nickname = signupRequest.getUsername();
        this.memberId = memberId;
    }

    public String getSocialId() {
        return naverId;
    }
}
