package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.dto.EventJoinResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
class EventJoinServiceTest {

    @Autowired
    private EventJoinService eventJoinService;

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    public void testRoulette() {
        EventJoinResponse response = eventJoinService.join(3, 4);
        assertNotEquals(0, response.getResult());
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    @Rollback(value = false)
    public void testJoin() {
        EventJoinResponse response = eventJoinService.join(4, 4);
        assertEquals(0, response.getResult());
    }
}