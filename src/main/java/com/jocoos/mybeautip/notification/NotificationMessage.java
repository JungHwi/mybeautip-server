package com.jocoos.mybeautip.notification;

import com.jocoos.mybeautip.support.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.jocoos.mybeautip.support.MybeautipConstants.PROPERTY_JOIN_DELIMITER;

@AllArgsConstructor
@Getter
public enum NotificationMessage {

    VIDEO_UPLOAD("notification.video.upload", 4, "컨텐츠 영상 업로드시 알림"),
    POST_FIRST_LIKE("notification.post.first_like", 4, "글에 첫 하트 받았을 때 알림"),
    POST_TWENTY_LIKE("notification.post.twenty_like", 4, "글에 20번째 하트 받았을 때 알림"),
    POST_COMMENT("notification.post.comment", 4, "글에 댓글 달렸을 때 알림"),
    POST_NESTED_COMMENT("notification.post.nested_comment", 4, "댓글에 대댓글 달렸을 때 알림"),
    LOGIN_OVER_2WEEKS("notification.login.over_2weeks", 4, "로그인 한지 2주 이상 지났을때 알림");

    private final String property;
    private final int countMessage;
    private final String description;

    public boolean isRandomMessage() {
        return this.countMessage > 1;
    }

    public String getProperty() {
        if (this.isRandomMessage()) {
            return this.property +
                    PROPERTY_JOIN_DELIMITER +
                    RandomUtils.getRandomIndex(this.countMessage);
        }

        return this.property;
    }
}