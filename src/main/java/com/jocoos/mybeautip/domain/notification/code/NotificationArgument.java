package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationArgument implements CodeValue {

    USER_NICKNAME("회원 닉네임. members.username"),
    CONTENT_ID("비디오 ID. video.id"),
    POST_ID("커뮤니티 글의 ID. posts.id"),
    COMMENT_ID("댓글의 ID. comments.parentId"),
    REPLY_ID("대댓글의 ID. comments.id");

    private final String description;
}
