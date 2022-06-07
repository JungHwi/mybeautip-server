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
@Table(name = "kakao_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KakaoMember extends CreatedDateAuditable implements SocialMember {

    @Id
    @Column(nullable = false, length = 30)
    private String kakaoId;

    @Column(nullable = false)
    private Long memberId;

    public String getSocialId() {
        return kakaoId;
    }

    public KakaoMember(SignupRequest request, long memberId) {
        this.kakaoId = request.getSocialId();
        this.memberId = memberId;
    }
}
