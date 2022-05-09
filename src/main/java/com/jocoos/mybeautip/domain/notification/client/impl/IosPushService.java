package com.jocoos.mybeautip.domain.notification.client.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.domain.notification.client.MobilePushService;
import com.jocoos.mybeautip.domain.notification.client.vo.AppPushMessage;
import com.jocoos.mybeautip.domain.notification.vo.DeviceToken;
import com.jocoos.mybeautip.domain.notification.vo.MobileDeviceToken;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.constant.NotificationConstant.*;

@Component
@RequiredArgsConstructor
public class IosPushService implements MobilePushService {
    private final ObjectMapper objectMapper;
    private final AmazonSNS amazonSNS;

    @Override
    public void push(MobileDeviceToken deviceToken, AppPushMessage message) {
        if (deviceToken == null || Collections.isEmpty(deviceToken.getDeviceTokenList())) {
            return;
        }

        List<DeviceToken> deviceTokenList = deviceToken.getDeviceTokenList();
        String pushString = generatePushString(message);
        for (DeviceToken token : deviceTokenList) {
            push(token.getArn(), pushString);
        }
    }

    private boolean push(String arn, String message) {
        try {
            PublishRequest request = new PublishRequest()
                    .withTargetArn(arn)
                    .withMessage(message)
                    .withMessageStructure(MESSAGE_STRUCTURE);

            PublishResult result = amazonSNS.publish(request);
            return true;
        } catch (AmazonSNSException e) {
            if (disabled(arn)) {
                // TODO device 정보 update
            }
        }
        return false;
    }

    private String generatePushString(AppPushMessage message) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> messageMap = objectMapper.convertValue(message, Map.class);
        Map<String, Map<String, String>> data = new HashMap<>();
        Map<String, String> notification = new HashMap<>();

        notification.put(KEY_TITLE, message.getTitle());
        notification.put(KEY_BODY, message.getMessage());
        notification.put(KEY_IMAGE, message.getImageUrl());

        data.put(KEY_NOTIFICATION, notification);
        data.put(KEY_DATA, messageMap);

        map.put(KEY_GCM, StringConvertUtil.convertDoubleMapToJson(data));
        return StringConvertUtil.convertMapToJson(map);
    }

    private boolean disabled(String arn) {
        GetEndpointAttributesRequest request = new GetEndpointAttributesRequest().withEndpointArn(arn);
        GetEndpointAttributesResult result = amazonSNS.getEndpointAttributes(request);

        if (result != null && result.getAttributes() != null) {
            String value = result.getAttributes().get("Enabled");
            return "false".equalsIgnoreCase(value);
        }
        return false;
    }
}
