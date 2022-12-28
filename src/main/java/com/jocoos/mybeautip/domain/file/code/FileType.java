package com.jocoos.mybeautip.domain.file.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType implements CodeValue {

    IMAGE("이미지"),
    VIDEO("비디오");

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}