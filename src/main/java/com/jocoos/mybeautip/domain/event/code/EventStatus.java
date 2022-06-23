package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum EventStatus implements CodeValue {

    WAIT("대기", false, false),
    PROGRESS("진행중", true, true),
    HOLD("일시정지", false, false),
    END("종료", false, true);

    private final String description;
    private final boolean canJoin;
    private final boolean visible;

    public static final Set<EventStatus> visibleEventStatus = Arrays.stream(EventStatus.values()).filter(EventStatus::isVisible).collect(Collectors.toSet());

}
