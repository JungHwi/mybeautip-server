package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.member.Member;
import org.springframework.stereotype.Service;

@Service
public interface EventTypeService {

    EventJoin join(Event event, Member member);
}
