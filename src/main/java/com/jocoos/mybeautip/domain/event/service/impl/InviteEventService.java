package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.service.PresentFactory;
import com.jocoos.mybeautip.domain.event.service.PresentService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteEventService extends EventTypeAbstractService {
    private final EventJoinRepository eventJoinRepository;
    private final PresentFactory presentFactory;

    @Override
    public EventJoin join(Event event, Member member) {
        valid(event);

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

    private void valid(Event event) {
        super.validEvent(event);
    }
}
