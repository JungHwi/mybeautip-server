package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BroadcastViewerStatus implements CodeValue {

    ACTIVE("활성 - 입장"),
    INACTIVE("비활성 - 나감"),
    EXILE("추방");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}


