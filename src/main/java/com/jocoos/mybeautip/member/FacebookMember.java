package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
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
public class FacebookMember extends CreatedDateAuditable {

    @Id
    @Column(nullable = false, length = 20)
    private String facebookId;

    @Column(nullable = false)
    private Long memberId;

    public FacebookMember(String facebookId, Long memberId) {
        this.facebookId = facebookId;
        this.memberId = memberId;
    }
}
