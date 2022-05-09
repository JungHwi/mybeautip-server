package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.global.code.DeviceOs;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class IosDeviceToken extends MobileDeviceToken {

    public IosDeviceToken() {
        super(DeviceOs.IOS);
    }

    public IosDeviceToken(List<DeviceToken> deviceTokenList) {
        super(DeviceOs.IOS, deviceTokenList);
    }
}
