package com.jocoos.mybeautip.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class MessageService {
    private static final String JOIN_DELIMITER = ".";
    private static final String NOTIFICATION_NAME_FORMAT = "notification.%s";
    private static final String GOODS_COMPANY_TEXT = "goods.company_text";

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // see getNotificationMessage(NotificationMessage message, Object[] args)
    @Deprecated
    public String getNotificationMessage(String code, Object[] args) {
        return getNotificationMessage(code, args, Locale.KOREAN);
    }

    // see getNotificationMessage(NotificationMessage message, Object[] args, Locale locale)
    @Deprecated
    private String getNotificationMessage(String code, Object[] args, Locale locale) {
        String message = messageSource.getMessage(
                String.format(NOTIFICATION_NAME_FORMAT, code), args, locale);
        return message;
    }

    public String getNotificationMessage(NotificationMessage message, Object[] args) {
        return getNotificationMessage(message, args, Locale.KOREAN);
    }

    private String getNotificationMessage(NotificationMessage message, Object[] args, Locale locale) {
        return messageSource.getMessage(message.getProperty(), args, locale);
    }

    public String getGoodsCompanyMessage() {
        return messageSource.getMessage(GOODS_COMPANY_TEXT, null, Locale.KOREAN);
    }

    // TODO 다국어 지원하게 되면 받은 locale 로 수정.
    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, Locale.KOREAN);
    }

    // TODO 다국어 지원하게 되면 받은 locale 로 수정.
    public String getMessage(String code, String lang) {
        return messageSource.getMessage(code, null, getLocale("ko"));
    }

    private Locale getLocale(String lang) {
        switch (lang) {
            case "ko":
                return Locale.KOREAN;
            case "en":
            default:
                return Locale.ENGLISH;
        }
    }

    public String getSystemMessage(Notification n) {
        String type = Notification.SYSTEM_MESSAGE.equals(n.getType()) && n.getCustom() != null ?
                String.join(JOIN_DELIMITER, n.getType(), n.getCustom().get("system_detail")) :
                n.getType();

        return getNotificationMessage(type, n.getArgs().toArray());
    }

    public String getMessage(Notification n) {
        return n.isSystemMessage() ?
                getSystemMessage(n) :
                getNotificationMessage(n.getType(), n.getArgs().toArray());
    }
}
