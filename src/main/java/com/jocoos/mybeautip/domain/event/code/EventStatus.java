package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventStatus implements CodeValue {

    WAIT("대기"),
    PROGRESS("진행중"),
    HOLD("일시정지"),
    END("종료");

    private final String description;
}
