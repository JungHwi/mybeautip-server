package com.jocoos.mybeautip.domain.notification.vo;

import com.jocoos.mybeautip.global.code.DeviceOs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
public class MobileDeviceToken {
    private final DeviceOs os;

    private List<DeviceToken> deviceTokenList;

    public MobileDeviceToken(DeviceOs os) {
        this.os = os;
    }
}
