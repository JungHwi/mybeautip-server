package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventStatus implements CodeValue {

    WAIT("대기", false),
    PROGRESS("진행중", true),
    HOLD("일시정지", false),
    END("종료", false);

    private final String description;
    private final boolean canJoin;

}
