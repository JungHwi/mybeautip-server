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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AndroidPushService implements MobilePushService {
    private final ObjectMapper objectMapper;
    private final AmazonSNS amazonSNS;

    private static final String KEY_DATA = "data";
    private static final String MESSAGE_STRUCTURE = "json";

    @Value("${mybeautip.aws.sns.application.gcm-arn}")
    private String gcmArn;

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
        Map<String, String> messageMap = new HashMap<>();
        Map<String, Map<String, String>> data = new HashMap<>();

        messageMap.put("messageType", message.getMessageType().name());
        messageMap.put("title", message.getTitle());
        messageMap.put("body", message.getMessage());
        messageMap.put("image", message.getImageUrl());
        messageMap.put("deepLink", message.getDeepLink());

        data.put(KEY_DATA, messageMap);

        map.put("GCM", StringConvertUtil.convertDoubleMapToJson(data));
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
