package com.jocoos.mybeautip.domain.notification.code;


import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationLinkType implements CodeValue {
    BROADCAST("방송 메뉴", NotificationArgument.BROADCAST_ID),
    HOME("Home 화면", NotificationArgument.NONE),
    VIDEO("비디오 메뉴", NotificationArgument.VIDEO_ID),
    COMMUNITY("게시물 메뉴", NotificationArgument.COMMUNITY_ID),
    COMMENT("댓글", NotificationArgument.COMMENT_ID);

    private final String description;
    private final NotificationArgument parameter;

    @Override
    public String getName() {
        return this.name();
    }
}
