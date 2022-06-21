package com.jocoos.mybeautip.member.point;

import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberPointServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventJoinRepository eventJoinRepository;

    @Autowired
    private MemberPointService memberPointService;

    @Autowired
    private LegacyMemberService legacyMemberService;

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void earnPoint() {
        for (int a = 0; a < 100;  a++) {
            EventJoin eventJoin = eventJoinRepository.getById(1L);
            memberPointService.earnPoint(eventJoin);
        }
    }

    @Test
    @WithUserDetails(value = "4", userDetailsServiceBeanName = "mybeautipUserDetailsService")
    @Transactional
    void usePoints() {
        Event event = eventRepository.getById(1L);
        Member member = legacyMemberService.currentMember();
        memberPointService.usePoints(event, member);
    }
}