package com.jocoos.mybeautip.domain.delivery.vo;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
public class DeliveryFeePolicySearchResult {
    private long id;
    private String name;
    private String companyCode;
    private String companyName;
    private boolean isDefault;
    private DeliveryMethod deliveryMethod;
    private DeliveryFeeType type;
    private Set<DeliveryFeePolicyDetailSearchResult> details;
    private ZonedDateTime createdAt;

    @QueryProjection
    public DeliveryFeePolicySearchResult(DeliveryFeePolicy deliveryFeePolicy,
                                         Company company,
                                         Set<DeliveryFeePolicyDetailSearchResult> details) {

        this.id = deliveryFeePolicy.getId();
        this.name = deliveryFeePolicy.getName();
        this.companyCode = company.getCode();
        this.companyName = company.getName();
        this.isDefault = deliveryFeePolicy.isDefault();
        this.deliveryMethod = deliveryFeePolicy.getDeliveryMethod();
        this.type = deliveryFeePolicy.getType();
        this.createdAt = deliveryFeePolicy.getCreatedAt();
        this.details = details;

    }
}
