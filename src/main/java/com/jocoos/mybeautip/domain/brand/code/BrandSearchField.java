package com.jocoos.mybeautip.domain.brand.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrandSearchField implements CodeValue {

    NAME("브랜드명 필드"),
    CODE("브랜드 코드");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

}
