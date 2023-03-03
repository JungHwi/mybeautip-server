package com.jocoos.mybeautip.domain.popupnotice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PopupNoticeStatus implements CodeValue {

    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

}
