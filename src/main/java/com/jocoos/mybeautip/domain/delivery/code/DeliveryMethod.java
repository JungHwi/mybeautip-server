package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryMethod implements CodeValue {
    COURIER("택배");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
