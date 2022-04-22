package com.jocoos.mybeautip.domain.notification.converter;

import com.jocoos.mybeautip.devices.Device;
import com.jocoos.mybeautip.domain.notification.vo.AndroidDeviceToken;
import com.jocoos.mybeautip.domain.notification.vo.DeviceToken;
import com.jocoos.mybeautip.domain.notification.vo.IosDeviceToken;
import com.jocoos.mybeautip.global.code.DeviceOs;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceTokenConvert {

    default IosDeviceToken convertIosToken(List<Device> devices) {
        List<DeviceToken> ios = new ArrayList<>();
        for (Device device : devices) {
            if (DeviceOs.IOS.equal(device.getOs())) {
                DeviceToken deviceToken = DeviceToken.builder()
                        .token(device.getId())
                        .arn(device.getArn())
                        .build();
                ios.add(deviceToken);
            }
        }
        return IosDeviceToken.builder()
                .os(DeviceOs.IOS)
                .deviceTokenList(ios)
                .build();
    }

    default AndroidDeviceToken convertAndroidToken(List<Device> devices) {
        List<DeviceToken> android = new ArrayList<>();
        for (Device device : devices) {
            if (DeviceOs.ANDROID.equal(device.getOs())) {
                DeviceToken deviceToken = DeviceToken.builder()
                        .token(device.getId())
                        .arn(device.getArn())
                        .build();
                android.add(deviceToken);
            }
        }
        return AndroidDeviceToken.builder()
                .os(DeviceOs.ANDROID)
                .deviceTokenList(android)
                .build();
    }
}
