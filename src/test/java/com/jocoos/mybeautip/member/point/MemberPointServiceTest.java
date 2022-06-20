package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberPointServiceTest {

    @Autowired
    private EventJoinRepository eventJoinRepository;

    @Autowired
    private MemberPointService memberPointService;

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void earnPoint() {
        EventJoin eventJoin = eventJoinRepository.getById(1L);
        memberPointService.earnPoint(eventJoin);
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void usePoints() {
        EventJoin eventJoin = eventJoinRepository.getById(1L);
        memberPointService.usePoints(eventJoin);
    }
}