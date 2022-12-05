package com.jocoos.mybeautip.domain.popup.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ButtonLinkType implements CodeValue {

    EVENT("이벤트"),
    PREVIOUS("이전 화면");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

}