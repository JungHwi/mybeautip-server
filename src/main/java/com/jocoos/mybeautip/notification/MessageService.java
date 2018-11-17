package com.jocoos.mybeautip.notification;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService {
  private static final String NOTIFICATION_NAME_FORMAT = "notification.%s";
  private static final String GOODS_COMPANY_TEXT = "goods.company_text";
  private static final String MEMBER_NOT_FOUND = "member.not_found";

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
    log.debug("message: {}", message);
    return message;
  }

  public String getGoodsCompanyMessage() {
    return messageSource.getMessage(GOODS_COMPANY_TEXT, null, Locale.KOREAN);
  }

  public String getMemberNotFoundMessage(String lang) {
    return messageSource.getMessage(MEMBER_NOT_FOUND, null, getLocale(lang));
  }

  public String getMessage(String code, Locale locale) {
    return messageSource.getMessage(code, null, locale);
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
}
