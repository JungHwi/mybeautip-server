package com.jocoos.mybeautip.domain.brand.code;


import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrandStatus implements CodeValue {

    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETE("삭제");

    private final String description;

    public String getName() {
        return this.name();
    }
}
