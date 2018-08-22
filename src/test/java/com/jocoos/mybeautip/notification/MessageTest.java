package com.jocoos.mybeautip.notification;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;


public class MessageTest {
  private static final String NOTIFICATION_NAME_FORMAT = "notification.%s";

  public static void main(String[] args) {
    ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
    source.setBasenames("i18n/messages");
    source.setDefaultEncoding("UTF8");
    source.setUseCodeAsDefaultMessage(true);

    String message = source.getMessage(String.format(NOTIFICATION_NAME_FORMAT, "video_started"), new String[]{"레이지"}, Locale.KOREAN);
    System.out.println(message);
  }
}
