package com.jocoos.mybeautip.domain.point.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DateLimit implements CodeValue {
    ALL_TIME_ONCE("최초 1회"), DAY("1일 최대"), NO_LIMIT("무한정");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
