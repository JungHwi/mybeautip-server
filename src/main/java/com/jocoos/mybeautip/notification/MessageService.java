package com.jocoos.mybeautip.notification;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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

  public String getNotificationMessage(String code, Object[] args) {
    return getNotificationMessage(code, args, Locale.KOREAN);
  }

  private String getNotificationMessage(String code, Object[] args, Locale locale) {
    String message = messageSource.getMessage(
       String.format(NOTIFICATION_NAME_FORMAT, code), args, locale);
    return message;
  }

  public String getGoodsCompanyMessage() {
    return messageSource.getMessage(GOODS_COMPANY_TEXT, null, Locale.KOREAN);
  }

  public String getMessage(String code, Locale locale) {
    return messageSource.getMessage(code, null, locale);
  }

  public String getMessage(String code, String lang) {
    return messageSource.getMessage(code, null, getLocale(lang));
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
       String.join(JOIN_DELIMITER, n.getType(), n.getCustom().get("system_detail")):
       n.getType();

    return getNotificationMessage(type, n.getArgs().toArray());
  }

  public String getMessage(Notification n) {
    return n.isSystemMessage() ?
       getSystemMessage(n) :
       getNotificationMessage(n.getType(), n.getArgs().toArray());
  }
}
