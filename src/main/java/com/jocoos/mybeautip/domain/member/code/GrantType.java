package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrantType implements CodeValue {
    NAVER("네이버"),
    KAKAO("카카오"),
    FACEBOOK("페이스북"),
    APPLE("애플");

    private final String description;


    @Override
    public String getName() {
        return this.name();
    }
}
