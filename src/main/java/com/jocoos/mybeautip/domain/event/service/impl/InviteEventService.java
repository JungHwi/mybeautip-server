package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.service.PresentFactory;
import com.jocoos.mybeautip.domain.event.service.PresentService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.address.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteEventService extends EventTypeAbstractService {
    private final AddressRepository addressRepository;
    private final EventJoinRepository eventJoinRepository;
    private final PresentFactory presentFactory;

    @Override
    public EventJoin join(Event event, Member member) {
        Address address = addressRepository.findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(member.getId())
                .orElse(null);

        if(!valid(event, address)) {
            return null;
        }

        EventProduct eventProduct = super.winPrize(event.getEventProductList());

        EventJoin eventJoin = EventJoin.builder()
                .memberId(member.getId())
                .eventId(event.getId())
                .status(EventJoinStatus.JOIN)
                .eventProduct(eventProduct)
                .build();

        eventJoin = eventJoinRepository.save(eventJoin);

        PresentService presentService = presentFactory.getPresentService(eventProduct.getType());
        presentService.present(member, eventJoin);

        return eventJoin;
    }

    private boolean valid(Event event, Address address) {
        try {
            super.validEvent(event, address);
            return true;
        } catch (BadRequestException ex) {
            return false;
        }
    }
}
