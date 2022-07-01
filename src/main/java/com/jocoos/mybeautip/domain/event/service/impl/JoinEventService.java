package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.address.AddressRepository;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinEventService extends EventTypeAbstractService {

    private final AddressRepository addressRepository;
    private final MemberPointService memberPointService;
    private final EventJoinRepository eventJoinRepository;

    @Override
    public EventJoin join(Event event, Member member) {
        Address address = addressRepository.findByCreatedByIdAndDeletedAtIsNullAndBaseIsTrue(member.getId())
                .orElse(null);

        valid(event, address);

        memberPointService.usePoints(event, member);

        EventJoin eventJoin = EventJoin.builder()
                .memberId(member.getId())
                .eventId(event.getId())
                .status(EventJoinStatus.JOIN)
                .recipientInfo(super.getRecipientInfo(address))
                .build();

        eventJoin = eventJoinRepository.save(eventJoin);

        return eventJoin;
    }

    private void valid(Event event, Address address) {
        super.validEvent(event, address);
    }
}
