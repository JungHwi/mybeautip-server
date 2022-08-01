package com.jocoos.mybeautip.domain.popup.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PopupDisplayType implements CodeValue {

    ONCE("다시 보지 않기"),
    DAILY("매일");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
