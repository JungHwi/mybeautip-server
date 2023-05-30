package com.jocoos.mybeautip.domain.policy.persistence.domain;

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @Column
    private String deliveryPolicy;

    @Column
    private String claimPolicy;

    public Policy edit(EditPolicyRequest request) {
        this.deliveryPolicy = request.deliveryPolicy();
        this.claimPolicy = request.claimPolicy();
        return this;
    }
}