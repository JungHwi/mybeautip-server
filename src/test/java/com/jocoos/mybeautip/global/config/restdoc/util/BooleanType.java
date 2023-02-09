package com.jocoos.mybeautip.global.config.restdoc.util;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BooleanType implements CodeValue {

    TRUE("true"),
    FALSE("false");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
