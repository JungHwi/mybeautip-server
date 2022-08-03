package com.jocoos.mybeautip.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileOperationType implements CodeValue {

    UPLOAD("신규 업로드"),
    DELETE("삭제");

    private String description;

    @Override
    public String getName() {
        return this.name();
    }
}
