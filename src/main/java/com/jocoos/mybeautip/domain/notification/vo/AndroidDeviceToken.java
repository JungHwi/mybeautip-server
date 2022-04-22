package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.global.code.DeviceOs;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class AndroidDeviceToken extends MobileDeviceToken {

    public AndroidDeviceToken() {
        super(DeviceOs.ANDROID);
    }

    public AndroidDeviceToken(List<DeviceToken> deviceTokenList) {
        super(DeviceOs.ANDROID, deviceTokenList);
    }
}
