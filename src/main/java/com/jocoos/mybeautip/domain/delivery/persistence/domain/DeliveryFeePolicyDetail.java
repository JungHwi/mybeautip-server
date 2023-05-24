package com.jocoos.mybeautip.domain.delivery.persistence.domain;

import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyDetailRequest;
import com.jocoos.mybeautip.domain.delivery.dto.EditDeliveryFeePolicyDetailRequest;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "delivery_fee_policy_detail")
public class DeliveryFeePolicyDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_fee_policy_id")
    private DeliveryFeePolicy deliveryFeePolicy;

    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @Column
    private String name;

    @Column
    private Integer threshold;

    @Column
    private int feeBelowThreshold;

    @Column
    private int feeAboveThreshold;

    public void mapping(DeliveryFeePolicy deliveryFeePolicy) {
        this.deliveryFeePolicy = deliveryFeePolicy;
    }

    public DeliveryFeePolicyDetail(CreateDeliveryFeePolicyDetailRequest request) {
        this.countryCode = request.countryCode();
        this.name = request.name();
        this.threshold = request.threshold();
        this.feeBelowThreshold = request.feeBelowThreshold();
        this.feeAboveThreshold = request.feeAboveThreshold();
    }

    public void edit(EditDeliveryFeePolicyDetailRequest request) {
        this.name = request.name();
        this.threshold = request.threshold();
        this.feeBelowThreshold = request.feeBelowThreshold();
        this.feeAboveThreshold = request.feeAboveThreshold();
    }
}
