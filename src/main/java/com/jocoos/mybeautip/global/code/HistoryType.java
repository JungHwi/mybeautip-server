package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType implements CodeValue {

    INSERT("삽입"),
    UPDATE("수정"),
    DELETE("삭제");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
