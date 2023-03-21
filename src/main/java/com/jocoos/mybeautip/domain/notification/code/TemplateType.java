package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateType implements CodeValue {
    VIDEO_UPLOAD("동영상 업로드 시, 모든 유저에게"),
    COMMUNITY_COMMENT("글에 댓글이 달렸을때, 글 작성자에게"),
    COMMUNITY_LIKE_1("글에 하트가 처음 달렸을 때, 글 작성자에게"),
    COMMUNITY_LIKE_20("글에 하트가 20개 달렸을 때, 글 작성자에게"),
    COMMUNITY_COMMENT_REPLY("댓글에 대댓글이 달렸을 때, 댓글 작성자에게"),
    NO_LOGIN_2WEEKS("로그인 안 한지 2주째..."),
    DORMANT_MEMBER("휴면 회원 예정자에게(30/7/1 일전)"),
    BROADCAST_CHANGE_SCHEDULE("방송 일정 변경시, 알림 설정자에게"),
    BROADCAST_READY_TO_OWNER("방송 준비시, 진행자에게"),
    BROADCAST_CANCEL_TO_OWNER("방송 취소시, 진행자에게"),
    BROADCAST_READY_TO_FOLLOWER("방송 준비시, 알림 설정자에게"),
    BROADCAST_LIVE_TO_FOLLOWER("방송 시작시, 알림 설정자에게"),
    BROADCAST_CANCEL_TO_FOLLOWER("방송 취소시, 알림 설정자에게"),
    ;

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}


