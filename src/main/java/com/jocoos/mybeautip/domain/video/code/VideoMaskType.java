package com.jocoos.mybeautip.domain.video.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoMaskType implements CodeValue {

    CLOVER("클로버"),
    SQUARE("사각형"),
    SEMI_CIRCLE("반원"),
    HEART("하트"),
    CLOUD("구름");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
