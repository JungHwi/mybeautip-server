package com.jocoos.mybeautip.global.converter;

import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageConverter {
    private final MessageSource messageSource;
    private final HttpServletRequest request;

    public String converts(ErrorCode errorCode) {
        if (errorCode == null) {
            return getMessage("common_error_message");
        }
        return getMessage(errorCode.getKey());
    }

    public String converts(ErrorCode errorCode, String... args) {
        return getMessage(errorCode.getKey(), args);
    }

    private String getMessage(String key) {
        return getMessage(key, null);
    }

    private String getMessage(String key, String... args) {
        Locale locale = LocaleUtils.toLocale(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
        return messageSource.getMessage(key, args, locale);
    }
}
