package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static com.jocoos.mybeautip.global.util.SetUtil.newHashSet;

@Getter
@AllArgsConstructor
public enum TemplateType implements CodeValue {
    VIDEO_UPLOAD("동영상 업로드 시, 모든 유저에게", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME, NotificationArgument.VIDEO_ID)),
    COMMUNITY_COMMENT("글에 댓글이 달렸을때, 글 작성자에게", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME, NotificationArgument.POST_ID, NotificationArgument.COMMENT_ID)),
    COMMUNITY_LIKE_1("글에 하트가 처음 달렸을 때, 글 작성자에게", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME, NotificationArgument.POST_ID)),
    COMMUNITY_LIKE_20("글에 하트가 20개 달렸을 때, 글 작성자에게", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME, NotificationArgument.POST_ID)),
    COMMUNITY_COMMENT_REPLY("댓글에 대댓글이 달렸을 때, 댓글 작성자에게", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME, NotificationArgument.POST_ID, NotificationArgument.COMMENT_ID)),
    NO_LOGIN_2WEEKS("로그인 안 한지 2주째...", newHashSet(SendType.CENTER, SendType.APP_PUSH), newHashSet(NotificationArgument.USER_NICKNAME));

    private final String description;
    private final Set<SendType> sendTypes;
    private final Set<NotificationArgument> arguments;

    @Override
    public String getName() {
        return this.name();
    }
}


