package com.jocoos.mybeautip.domain.delivery.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryCompanyStatus implements CodeValue {

    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    DELETE("삭제");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
