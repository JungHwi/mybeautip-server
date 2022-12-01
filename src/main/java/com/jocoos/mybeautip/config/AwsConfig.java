package com.jocoos.mybeautip.config;

import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonSNS amazonSNS() {
        return AmazonSNSClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    @Bean
    public AWSSecurityTokenService awsSTS() {
        return AWSSecurityTokenServiceClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
        return new NotificationMessagingTemplate(amazonSNS);
    }
}
