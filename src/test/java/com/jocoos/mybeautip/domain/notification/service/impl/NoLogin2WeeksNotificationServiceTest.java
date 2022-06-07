package com.jocoos.mybeautip.domain.notification.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class NoLogin2WeeksNotificationServiceTest {
    @Autowired
    private NoLogin2WeeksNotificationService service;

    @Test
    @Transactional
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    void occurs() {
        service.occurs();
    }
}