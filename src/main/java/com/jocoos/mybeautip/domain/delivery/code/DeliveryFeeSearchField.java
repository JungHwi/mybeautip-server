package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryFeeSearchField implements CodeValue {

    DELIVERY_FEE_NAME("배송비명"),
    COMPANY_NAME("공급사명");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
