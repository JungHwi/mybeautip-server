package com.jocoos.mybeautip.domain.popupnotice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PopupNoticeLinkType implements CodeValue {
    HOME("홈 화면"),
    EVENT("이벤트"),
    NOTICE("공지 사항");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
