package com.jocoos.mybeautip.domain.company.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CompanyStatus implements CodeValue {

    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
