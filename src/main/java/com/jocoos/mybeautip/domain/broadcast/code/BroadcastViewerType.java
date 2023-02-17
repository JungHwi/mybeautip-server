package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BroadcastViewerType implements CodeValue {

    MANAGER("채팅 관리자", false),
    MEMBER("회원", true),
    GUEST("비회원", false);

    private final String description;
    private final boolean availableManager;

    @Override
    public String getName() {
        return this.name();
    }
}
