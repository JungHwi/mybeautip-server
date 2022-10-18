package com.jocoos.mybeautip.domain.video.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoMaskType implements CodeValue {

    LAUGH("웃겨주마"),
    INFO("알려주마"),
    SHOW("보여주마"),
    FILL("채워주마"),
    SLEEP("재워주마");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
