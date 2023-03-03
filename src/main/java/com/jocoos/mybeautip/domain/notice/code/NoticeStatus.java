package com.jocoos.mybeautip.domain.notice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeStatus implements CodeValue {

    NORMAL("일반 상태"),
    DELETE("삭제 상태");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
