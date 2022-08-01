package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UsePointService implements CodeValue {

    ORDER("주문"),
    EVENT("이벤트");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
