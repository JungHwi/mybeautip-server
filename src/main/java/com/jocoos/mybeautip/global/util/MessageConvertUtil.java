package com.jocoos.mybeautip.global.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.jocoos.mybeautip.global.constant.SignConstant.SIGN_DOUBLE_LEFT_BRACE;
import static com.jocoos.mybeautip.global.constant.SignConstant.SIGN_DOUBLE_RIGHT_BRACE;

public class MessageConvertUtil {

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

    public static String processKey(String key) {
        return SIGN_DOUBLE_LEFT_BRACE + key + SIGN_DOUBLE_RIGHT_BRACE;
    }
}
