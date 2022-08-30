package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.service.PresentService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PresentPointService implements PresentService {

    private final MemberPointService pointService;

    @Override
    public void present(Member member, EventJoin eventJoin) {
        valid(member, eventJoin);

        pointService.earnPoint(eventJoin);
    }

    private void valid(Member member, EventJoin eventJoin) {
        validMember(member);
        validJoin(eventJoin);
    }

    private void validMember(Member member) {
        if (member == null) {
            throw new MemberNotFoundException("Can't present event product. Member is null.");
        }
    }

    private void validJoin(EventJoin eventJoin) {
        if (eventJoin == null) {
            throw new BadRequestException("Can't present event product. Event Join info is null.");
        }

        validProduct(eventJoin.getEventProduct());
    }

    private void validProduct(EventProduct product) {
        if (product == null) {
            throw new BadRequestException("Can't present event product. Event Product info is null.");
        }
    }
}
