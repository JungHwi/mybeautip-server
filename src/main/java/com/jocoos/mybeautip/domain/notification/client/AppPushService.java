package com.jocoos.mybeautip.domain.notification.client;

import com.amazonaws.services.sns.AmazonSNS;
import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.vo.NotificationTargetInfo;
import com.jocoos.mybeautip.global.code.DeviceOs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppPushService {
    private static final String MESSAGE_STRUCTURE = "json";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_DATA = "data";
    private final MobilePushFactory mobilePushFactory;
    private final AmazonSNS amazonSNS;
    @Value("${mybeautip.aws.sns.application.gcm-arn}")
    private String gcmArn;

    @Autowired
    public AppPushService(MobilePushFactory mobilePushFactory,
                          AmazonSNS amazonSNS) {
        this.mobilePushFactory = mobilePushFactory;
        this.amazonSNS = amazonSNS;
    }

    public void send(NotificationTargetInfo info, AppPushMessage pushMessage) {
        mobilePushFactory.getMobilePushService(DeviceOs.ANDROID).push(info.getAndroidDeviceToken(), pushMessage);
        mobilePushFactory.getMobilePushService(DeviceOs.IOS).push(info.getIosDeviceToken(), pushMessage);
    }
}
