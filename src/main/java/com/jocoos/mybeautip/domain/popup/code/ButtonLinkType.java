package com.jocoos.mybeautip.domain.popup.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ButtonLinkType implements CodeValue {

    EVENT("이벤트");

    private final String description;
}