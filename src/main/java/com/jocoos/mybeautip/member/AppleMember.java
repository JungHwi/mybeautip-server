package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "apple_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AppleMember extends CreatedDateAuditable implements SocialMember {

    @Id
    @Column
    private String appleId;

    @Column(nullable = false)
    private String email;

    @Column
    private String name;

    @Column(nullable = false)
    private Long memberId;

    @Column (nullable = false)
    private String refreshToken;

    public String getSocialId() {
        return appleId;
    }

    public AppleMember(SignupRequest request, long memberId) {
        this.appleId = request.getSocialId();
        this.email = request.getEmail();
        this.name = request.getUsername();
        this.memberId = memberId;
    }

    public void revoke() {
        this.refreshToken = "";
    }
}
