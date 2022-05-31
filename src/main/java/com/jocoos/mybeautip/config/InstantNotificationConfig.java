package com.jocoos.mybeautip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("mybeautip.notification.instant-message")
public class InstantNotificationConfig {

    private int delay;

    private int platform;

    private int interval;
}
