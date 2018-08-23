package com.jocoos.mybeautip.notification;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService {
  private static final String NOTIFICATION_NAME_FORMAT = "notification.%s";
  private static final String GOODS_DELIVERY_TEXT = "goods.delevery_text";

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

  public String getGoodsDeliveryMessage() {
    return messageSource.getMessage(GOODS_DELIVERY_TEXT, null, Locale.KOREAN);
  }
}
