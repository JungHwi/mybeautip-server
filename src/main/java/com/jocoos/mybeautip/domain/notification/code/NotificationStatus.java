package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationStatus implements CodeValue {
    NOT_READ("읽지 않음"),
    READ("읽음"),
    DELETE("삭제");

    private final String description;
}

