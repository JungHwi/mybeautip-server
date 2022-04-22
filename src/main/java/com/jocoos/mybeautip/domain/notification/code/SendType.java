package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SendType implements CodeValue {
    CENTER("알림센터 등록"),
    APP_PUSH("AOS, iOS Push");
    /* TODO 언젠가는 만들겠지
    WEB_PUSH("Web Push"),
    SMS("문자 메세지. SMS / LMS"),
    EMAIL("이메일");
    */

    private final String description;

}
