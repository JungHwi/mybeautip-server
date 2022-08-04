package com.jocoos.mybeautip.video.scrap;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScrapStatus implements CodeValue {
    SCRAP("스크랩"), NOT_SCRAP("스크랩 취소");
    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
