package com.jocoos.mybeautip.domain.notification.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationTargetInfo {

    private long memberId;

    private String nickname;

    private IosDeviceToken iosDeviceToken;

    private AndroidDeviceToken androidDeviceToken;

    private String phone;

    private String email;
}
