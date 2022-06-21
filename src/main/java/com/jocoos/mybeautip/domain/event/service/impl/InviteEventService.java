package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.member.Member;
import org.springframework.stereotype.Service;

@Service
public class InviteEventService implements EventTypeService {

    @Override
    public EventJoin join(Event event, Member member) {
        return null;
    }
}
