package com.jocoos.mybeautip.domain.delivery.vo;

import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicyDetail;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class DeliveryFeePolicyDetailSearchResult {
    private CountryCode countryCode;
    private Integer threshold;
    private int feeBelowThreshold;
    private int feeAboveThreshold;

    @QueryProjection
    public DeliveryFeePolicyDetailSearchResult(DeliveryFeePolicyDetail detail) {
        this.countryCode = detail.getCountryCode();
        this.threshold = detail.getThreshold();
        this.feeBelowThreshold = detail.getFeeBelowThreshold();
        this.feeAboveThreshold = detail.getFeeAboveThreshold();
    }
}
