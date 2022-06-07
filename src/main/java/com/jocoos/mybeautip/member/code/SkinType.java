package com.jocoos.mybeautip.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinType implements CodeValue {
    NEUTRAL("중성"),
    DRY("건성"),
    OILY("지성"),
    COMBINATION("복합성");

    private final String description;
}
