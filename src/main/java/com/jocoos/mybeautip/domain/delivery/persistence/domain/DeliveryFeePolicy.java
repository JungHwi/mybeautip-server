package com.jocoos.mybeautip.domain.delivery.persistence.domain;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeStatus;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod;
import com.jocoos.mybeautip.domain.delivery.code.PaymentOption;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyDetailRequest;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest;
import com.jocoos.mybeautip.global.config.jpa.CreatedAtBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "delivery_fee_policy")
public class DeliveryFeePolicy extends CreatedAtBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Long id;

    @Column
    private String code;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private DeliveryFeeStatus status;

    @Enumerated(EnumType.STRING)
    private DeliveryFeeType type;

    @Column
    private boolean isDefault;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentOption;

    @OneToMany(mappedBy = "deliveryFeePolicy", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    List<DeliveryFeePolicyDetail> details = new ArrayList<>();

    @PostPersist
    public void postPersist() {
        details.forEach(detail -> detail.mapping(this));
    }

    public DeliveryFeePolicy(Company company, CreateDeliveryFeePolicyRequest request) {
        this.company = company;
        this.code = request.getCode();
        this.name = request.getName();
        this.status = request.getStatus();
        this.type = request.getType();
        this.isDefault = false;
        this.deliveryMethod = request.getDeliveryMethod();
        this.paymentOption = request.getPaymentOption();

        for (CreateDeliveryFeePolicyDetailRequest detailRequest : request.getDetails()) {
            DeliveryFeePolicyDetail detail = new DeliveryFeePolicyDetail(detailRequest);
            this.details.add(detail);
        }
    }
}
