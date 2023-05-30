package com.jocoos.mybeautip.domain.policy.persistence.domain;

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "policy_history")
public class PolicyHistory extends CreatedAtBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @Column
    private String beforeDeliveryPolicy;

    @Column
    private String beforeClaimPolicy;

    @Column
    private String afterDeliveryPolicy;

    @Column
    private String afterClaimPolicy;

    @Builder
    public PolicyHistory(Policy policy, EditPolicyRequest request) {
        this.countryCode = policy.getCountryCode();
        this.beforeDeliveryPolicy = policy.getDeliveryPolicy();
        this.beforeClaimPolicy = policy.getClaimPolicy();
        this.afterDeliveryPolicy = request.deliveryPolicy();
        this.afterClaimPolicy = request.claimPolicy();
    }
}