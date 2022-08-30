package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DripEventService extends EventTypeAbstractService {

    private final MemberPointService memberPointService;
    private final EventJoinRepository eventJoinRepository;

    @Override
    public EventJoin join(Event event, Member member) {

        if(!valid(event)) {
            return null;
        }

        memberPointService.usePoints(event, member);

        EventJoin eventJoin = EventJoin.builder()
                .memberId(member.getId())
                .eventId(event.getId())
                .status(EventJoinStatus.JOIN)
                .build();

        eventJoin = eventJoinRepository.save(eventJoin);

        return eventJoin;
    }

    private boolean valid(Event event) {
        try {
            super.validEvent(event);
            return true;
        } catch (BadRequestException ex) {
            return false;
        }
    }
}
