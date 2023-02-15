package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BroadcastStatus implements CodeValue {
    SCHEDULED("예정"),
    READY("준비"),
    LIVE("방송중"),
    END("종료"),
    CANCEL("취소");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
