package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommunityStatus implements CodeValue {

    NORMAL("일반적인 상태"),
    DELETE("삭제된 상태");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
