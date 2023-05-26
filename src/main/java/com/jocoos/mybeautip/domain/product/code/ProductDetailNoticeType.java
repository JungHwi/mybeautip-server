package com.jocoos.mybeautip.domain.product.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductDetailNoticeType implements CodeValue {

    ;
    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
