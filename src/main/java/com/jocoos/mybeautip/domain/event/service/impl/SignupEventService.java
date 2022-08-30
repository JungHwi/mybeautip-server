package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.service.PresentFactory;
import com.jocoos.mybeautip.domain.event.service.PresentService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupEventService extends EventTypeAbstractService {

    private final EventRepository eventRepository;
    private final EventJoinRepository eventJoinRepository;
    private final PresentFactory presentFactory;

    @Override
    @Transactional
    public EventJoin join(Event event, Member member) {
        try {
            valid(event);
        } catch (BadRequestException ex) {
            return null;
        }

        EventProduct eventProduct = super.winPrize(event.getEventProductList());

        EventJoin eventJoin = EventJoin.builder()
                .memberId(member.getId())
                .eventId(event.getId())
                .status(EventJoinStatus.JOIN)
                .eventProduct(eventProduct)
                .eventProductId(eventProduct.getId())
                .build();

        eventJoin = eventJoinRepository.save(eventJoin);

        PresentService presentService = presentFactory.getPresentService(eventProduct.getType());
        presentService.present(member, eventJoin);

        return eventJoin;
    }

    @Transactional
    public EventJoin join(Member member) {
        Event event = eventRepository.findTopByTypeAndStatus(EventType.SIGNUP, EventStatus.PROGRESS);
        if (event == null || duplicateSignup(event, member)) {
            return null;
        }

        return this.join(event, member);
    }

    private void valid(Event event) {
        super.validEvent(event);
    }

    private boolean duplicateSignup(Event event, Member member) {
        return eventJoinRepository.existsByMemberIdAndEventId(member.getId(), event.getId());
    }
}
