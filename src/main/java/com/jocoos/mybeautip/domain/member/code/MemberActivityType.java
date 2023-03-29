package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberActivityType implements CodeValue {

    COMMUNITY("커뮤니티"),
    COMMUNITY_COMMENT("댓글"),
    BROADCAST("방송"),
    VOD("VOD")
    ;

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
