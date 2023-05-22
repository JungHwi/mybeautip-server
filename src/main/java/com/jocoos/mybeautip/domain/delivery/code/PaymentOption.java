package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentOption implements CodeValue {

    PREPAID("선불"),
    POSTPAID("후불");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
