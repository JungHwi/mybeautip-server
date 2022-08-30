package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType implements CodeValue {

    SIGNUP("회원가입", false),
    INVITE("초대", false),
    ROULETTE("룰렛", true),
    JOIN("참가", true),
    DRIP("드립N드림", true);

    private final String description;
    private final boolean directJoin;

    @Override
    public String getName() {
        return this.name();
    }
}
