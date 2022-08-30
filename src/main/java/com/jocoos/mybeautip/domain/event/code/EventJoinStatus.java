package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventJoinStatus implements CodeValue {

    JOIN("응모"),
    WIN("이벤트 당첨");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

}
