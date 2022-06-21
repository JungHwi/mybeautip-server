package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventJoinStatus;
import com.jocoos.mybeautip.domain.event.code.EventProductType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouletteEventServiceType extends EventTypeAbstractService {

    private final MemberPointService memberPointService;
    private final EventJoinRepository eventJoinRepository;

    @Override
    public EventJoin join(Event event, Member member) {
        valid(event);
        memberPointService.usePoints(event, member);
        EventProduct eventProduct = winPrize(event.getEventProductList());

        EventJoin eventJoin = EventJoin.builder()
                .memberId(member.getId())
                .eventId(event.getId())
                .status(EventJoinStatus.WIN)
                .eventProductId(eventProduct.getId())
                .eventProduct(eventProduct)
                .build();

        eventJoin = eventJoinRepository.save(eventJoin);

        if (eventProduct.getType() == EventProductType.POINT) {
            memberPointService.earnPoint(eventJoin);
        }

        return eventJoin;
    }

    private EventProduct winPrize(List<EventProduct> productList) {
        int total = productList.stream()
                .mapToInt(EventProduct::getQuantity)
                .sum();

        int winningIndex = RandomUtils.getRandom(total);

        for (EventProduct product : productList) {
            winningIndex -= product.getQuantity();
            if (winningIndex <= 0) {
                return product.winPrize();
            }
        }
        throw new BadRequestException("sold_out", "All product is sold out.");
    }

    private void valid(Event event) {
        super.validEvent(event);
        validEventProduct(event.getEventProductList());
    }

    private void validEventProduct(List<EventProduct> productList) {
        long availableProduct = productList.stream()
                .filter(product -> product.getQuantity() > 0)
                .count();

        if (availableProduct <= 0) {
            throw new BadRequestException("sold_out", "All product is sold out.");
        }
    }
}
