package com.jocoos.mybeautip.domain.community.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommunityCategoryType implements CodeValue {

    TOTAL("전체"),
    NORMAL("일반"),
    BLIND("블라인드"),
    DRIP("드립"),
    EVENT("이벤트"),
    VOTE("결정픽");

    private String description;

    @Override
    public String getName() {
        return this.name();
    }
}
