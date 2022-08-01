package com.jocoos.mybeautip.domain.notification.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationPlatform implements CodeValue {

    WEB("WEB PUSH"),
    ANDROID("Android App Push"),
    IOS("IOS App Push"),
    SMS("문자 메세지"),
    EMAIL("Email");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }

}
