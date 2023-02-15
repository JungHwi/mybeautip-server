package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BroadcastCategoryType implements CodeValue {
    REVIEW("솔직 리뷰"),
    SHARE_TIP("꿀팁 공유"),
    CHAT("떠들어보아요"),
    GROUP("그룹")
    ;

    private final String description;

    @Override
    public String getName() {
        return name();
    }
}
