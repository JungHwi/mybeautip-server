package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityStatus implements CodeValue {

    NORMAL("일반적인 상태", true),
    DELETE("삭제된 상태", false);

    private final String description;
    private final boolean deletable;

    @Override
    public String getName() {
        return this.name();
    }

}
