package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventProductType implements CodeValue {

    PRODUCT("배송 상품"),
    GIFT_CARD("전자상품권"),
    POINT("마이뷰팁 포인트");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
