package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfluencerStatus implements CodeValue {

    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
