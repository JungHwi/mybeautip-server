package com.jocoos.mybeautip.domain.video.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoStatus implements CodeValue {
    RESERVE("예약"),
    OPEN("공개"),
    DELETE("삭제")
    ;

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
