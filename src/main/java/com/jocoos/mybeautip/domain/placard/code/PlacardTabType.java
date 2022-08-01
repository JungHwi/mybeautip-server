package com.jocoos.mybeautip.domain.placard.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlacardTabType implements CodeValue {

    M_VIDEO("비디오 화면"),
    COMMUNITY("커뮤니티 화면");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
