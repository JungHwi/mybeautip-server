package com.jocoos.mybeautip.domain.banner.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TabType implements CodeValue {

    TOTAL("Total Tab"),
    VIDEO("Video Tab"),
    COMMUNITY("Community Tab");

    private String description;
}
