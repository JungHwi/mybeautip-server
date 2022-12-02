package com.jocoos.mybeautip.domain.member.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.jocoos.mybeautip.member.Member.*;

@Getter
@RequiredArgsConstructor
public enum GrantType implements CodeValue {
    NAVER("네이버", LINK_NAVER),
    KAKAO("카카오", LINK_KAKAO),
    FACEBOOK("페이스북", LINK_FACEBOOK),
    APPLE("애플", LINK_APPLE);

    private final String description;
    private final Integer link;

    @Override
    public String getName() {
        return this.name();
    }
}
