package com.jocoos.mybeautip.domain.notification.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeviceToken {
    private String token;
    private String arn;
}
