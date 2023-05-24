package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryFeeSearchField implements CodeValue {

    DELIVERY_FEE_NAME("배송비명"),
    DELIVERY_FEE_CODE("배송비 코드"),
    COMPANY_NAME("공급사명"),
    COMPANY_CODE("공급사 코드");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
