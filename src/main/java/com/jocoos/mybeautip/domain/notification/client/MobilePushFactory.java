package com.jocoos.mybeautip.domain.notification.client;

import com.jocoos.mybeautip.domain.notification.client.impl.AndroidPushService;
import com.jocoos.mybeautip.domain.notification.client.impl.IosPushService;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.global.code.DeviceOs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MobilePushFactory {

    private final AndroidPushService androidPushService;
    private final IosPushService iosPushService;

    public MobilePushService getMobilePushService(DeviceOs os) {
        switch (os) {
            case ANDROID:
                return androidPushService;
            case IOS:
                return iosPushService;
            default:
                throw new BadRequestException(os + " is not supported.");
        }
    }
}
