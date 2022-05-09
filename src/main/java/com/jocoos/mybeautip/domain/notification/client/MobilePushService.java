package com.jocoos.mybeautip.domain.notification.client;

import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.vo.MobileDeviceToken;

public interface MobilePushService {

    void push(MobileDeviceToken deviceToken, AppPushMessage message);
}
