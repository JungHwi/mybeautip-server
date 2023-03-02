package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VodStatus implements CodeValue {

    CREATED("초기화 (방송 생성 시 동시 생성)"),
    AVAILABLE("시청 가능"),
    DELETE("삭제됨")
    ;

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
