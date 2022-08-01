package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType implements CodeValue {

    COMMUNITY("커뮤니티"),
    CONTENT("컨텐츠"),
    LOGIN("로그인");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
