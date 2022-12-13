package com.jocoos.mybeautip.domain.notice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeLinkType implements CodeValue {
    EVENT("이벤트"),
    HOME("홈 화면"),
    QUIT("종료");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
