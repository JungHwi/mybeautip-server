package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BroadcastViewerType implements CodeValue {

    MANAGER("채팅 관리자"),
    MEMBER("회원"),
    GUEST("비회원");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
