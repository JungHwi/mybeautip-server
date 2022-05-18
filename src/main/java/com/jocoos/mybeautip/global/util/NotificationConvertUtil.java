package com.jocoos.mybeautip.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jocoos.mybeautip.domain.notification.code.NotificationLinkType;
import com.jocoos.mybeautip.domain.notification.vo.NotificationLink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.constant.SignConstant.SIGN_DOUBLE_LEFT_BRACE;
import static com.jocoos.mybeautip.global.constant.SignConstant.SIGN_DOUBLE_RIGHT_BRACE;

@Slf4j
public class NotificationConvertUtil {

    public static String generateStringByArguments(String template, Map<String, String> arguments) {
        if (StringUtils.isBlank(template) || arguments.isEmpty()) {
            return template;
        }

        String result = template;
        for (String key : arguments.keySet()) {
            result = result.replace(processKey(key), arguments.get(key));
        }

        return result;
    }

    public static NotificationLink generateNotificationLinkByArguments(NotificationLinkType type, Map<String, String> arguments) {
        return NotificationLink.builder()
                .type(type)
                .parameter(arguments.get(type.getParameter().name()))
                .build();
    }

    public static List<NotificationLink> generateNotificationLinkByArguments(List<NotificationLinkType> typeList, Map<String, String> arguments) {
        List<NotificationLink> result = new ArrayList<>();
        for (NotificationLinkType type : typeList) {
            NotificationLink link = generateNotificationLinkByArguments(type, arguments);
            result.add(link);
        }
        return result;
    }

    public static String convertToStringAsLink(List<NotificationLink> notificationLink) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(notificationLink);
        } catch (JsonProcessingException e) {
            log.warn("Failed convert to string as notification link - " + notificationLink);
            return null;
        }
    }

    public static String processKey(String key) {
        return SIGN_DOUBLE_LEFT_BRACE + key + SIGN_DOUBLE_RIGHT_BRACE;
    }
}
