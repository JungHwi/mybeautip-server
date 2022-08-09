package com.jocoos.mybeautip.member.block;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockStatus implements CodeValue {
    BLOCK("블락"),
    UNBLOCK("언블락");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
