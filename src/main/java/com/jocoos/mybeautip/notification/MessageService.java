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
  private static final String VIDEO_NOT_FOUND = "video.not_found";
  private static final String POST_NOT_FOUND = "post.not_found";
  private static final String BANNER_NOT_FOUND = "banner.not_found";
  private static final String STORE_NOT_FOUND = "store.not_found";
  private static final String GOODS_NOT_FOUND = "goods.not_found";
  private static final String OPTION_NOT_FOUND = "option.not_found";
  private static final String CART_ITEM_NOT_FOUND = "cart.item_not_found";
  private static final String ORDER_NOT_FOUND = "order.not_found";
  private static final String ORDER_INQUIRY_NOT_FOUND = "order.inquiry_not_found";
  private static final String ACCOUNT_NOT_FOUND = "account.not_found";
  private static final String ADDRESS_NOT_FOUND = "address.not_found";

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

  public String getVideoNotFoundMessage(String lang) {
    return messageSource.getMessage(VIDEO_NOT_FOUND, null, getLocale(lang));
  }

  public String getPostNotFoundMessage(String lang) {
    return messageSource.getMessage(POST_NOT_FOUND, null, getLocale(lang));
  }

  public String getBannerNotFoundMessage(String lang) {
    return messageSource.getMessage(BANNER_NOT_FOUND, null, getLocale(lang));
  }

  public String getStoreNotFoundMessage(String lang) {
    return messageSource.getMessage(STORE_NOT_FOUND, null, getLocale(lang));
  }

  public String getGoodsNotFoundMessage(String lang) {
    return messageSource.getMessage(GOODS_NOT_FOUND, null, getLocale(lang));
  }

  public String getOptionNotFoundMessage(String lang) {
    return messageSource.getMessage(OPTION_NOT_FOUND, null, getLocale(lang));
  }

  public String getCartItemNotFoundMessage(String lang) {
    return messageSource.getMessage(CART_ITEM_NOT_FOUND, null, getLocale(lang));
  }

  public String getOrderNotFoundMessage(String lang) {
    return messageSource.getMessage(ORDER_NOT_FOUND, null, getLocale(lang));
  }

  public String getOrderInquiryNotFoundMessage(String lang) {
    return messageSource.getMessage(ORDER_INQUIRY_NOT_FOUND, null, getLocale(lang));
  }

  public String getAccountNotFoundMessage(String lang) {
    return messageSource.getMessage(ACCOUNT_NOT_FOUND, null, getLocale(lang));
  }

  public String getAddressNotFoundMessage(String lang) {
    return messageSource.getMessage(ADDRESS_NOT_FOUND, null, getLocale(lang));
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
}
