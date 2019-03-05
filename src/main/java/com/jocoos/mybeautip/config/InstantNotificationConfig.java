package com.jocoos.mybeautip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("mybeautip.notification.instant-message")
public class InstantNotificationConfig {

  private int delay;

  private int platform;
}
