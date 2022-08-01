package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus implements CodeValue {
    ACTIVE("활동 중인 정상 상태"),
    DORMANT("휴면상태"),
    WITHDRAWAL("탈퇴 상태");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
