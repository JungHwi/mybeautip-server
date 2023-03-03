package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileOperationType implements CodeValue {

    KEEP("유지"),
    UPLOAD("신규 업로드"),
    DELETE("삭제");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
