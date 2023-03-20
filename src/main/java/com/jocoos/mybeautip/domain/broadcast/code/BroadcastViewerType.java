package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BroadcastViewerType implements CodeValue {

    OWNER("방송 진행자", false, false),
    ADMIN("마이뷰팁 어드민", false, false),
    MANAGER("채팅 관리자", false, true),
    MEMBER("회원", true, true),
    GUEST("비회원", false, true);

    private final String description;
    private final boolean availableManager;
    private final boolean searchable;

    public static final List<BroadcastViewerType> defaultSearchType = getDefaultSearchType();

    private static List<BroadcastViewerType> getDefaultSearchType() {
        return Arrays.stream(BroadcastViewerType.values())
                .filter(type -> type.searchable)
                .toList();
    }

    @Override
    public String getName() {
        return this.name();
    }
}
