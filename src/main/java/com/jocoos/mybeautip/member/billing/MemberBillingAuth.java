package com.jocoos.mybeautip.member.billing;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_billing_auths")
public class MemberBillingAuth extends CreatedDateAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(length = 10)
    private String username;

    @Column(length = 50)
    private String email;

    @Column(length = 256)
    private String password;

    @Column(length = 20)
    private String salt;

    @Column(nullable = false)
    private Integer errorCount;

    @Column
    private Date resetAt;

    public MemberBillingAuth() {
        this.errorCount = 0;
        this.resetAt = new Date();
    }
}
