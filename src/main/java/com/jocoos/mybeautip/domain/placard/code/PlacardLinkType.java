package com.jocoos.mybeautip.domain.placard.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlacardLinkType implements CodeValue {

    EVENT("Event 화면"),
    EXTERNAL("외부 링크");

    private final String description;
}
