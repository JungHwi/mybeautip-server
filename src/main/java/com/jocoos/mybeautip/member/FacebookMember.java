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
@Table(name = "facebook_members")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FacebookMember extends CreatedDateAuditable implements SocialMember {

    @Id
    @Column(nullable = false, length = 20)
    private String facebookId;

    @Column(nullable = false)
    private Long memberId;

    public String getSocialId() {
        return facebookId;
    }

    public FacebookMember(SignupRequest request, long memberId) {
        this.facebookId = request.getSocialId();
        this.memberId = memberId;
    }
}
