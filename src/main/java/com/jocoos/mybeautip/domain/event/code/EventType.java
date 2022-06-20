package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType implements CodeValue {

    SIGNUP("회원가입"),
    INVITE("초대"),
    ROULETTE("룰렛"),
    JOIN("참가");

    private final String description;
}
