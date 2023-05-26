package com.jocoos.mybeautip.domain.product.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus implements CodeValue {
    NORMAL("일반"),
    TEMP("임시저장"),
    DELETE("삭제")
    ;

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
