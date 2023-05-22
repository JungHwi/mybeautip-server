package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryFeeType implements CodeValue {

    FIXED("고정"),
    FREE("무료"),
    AMOUNT("가격별"),
    QUANTITY("수량별"),
    WEIGHT("무게별");

    private final String description;

    public String getName() {
        return this.name();
    }
}
