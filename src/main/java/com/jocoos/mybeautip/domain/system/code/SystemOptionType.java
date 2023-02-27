package com.jocoos.mybeautip.domain.system.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemOptionType implements CodeValue {

    FREE_LIVE_PERMISSION("라이브 권한 옵션");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
