package com.jocoos.mybeautip.domain.video.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VideoCategoryType implements CodeValue {
    GROUP("그룹"),
    NORMAL("일반");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
