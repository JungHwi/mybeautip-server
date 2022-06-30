package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventProduct;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public abstract class EventTypeAbstractService implements EventTypeService {

    public EventProduct winPrize(List<EventProduct> productList) {
        if (productList.size() == 1) {
            return productList.get(0);
        }

        int winningIndex = RandomUtils.getRandomIndex(productList.size());
        EventProduct winningProduct = productList.get(winningIndex);
        return winningProduct.winPrize();
    }

    public void validEvent(Event event) {
        if (event == null) {
            throw new BadRequestException("unable_event", "Event is not available");
        }
        validEventStatus(event.getStatus());
        validEventPeriod(event.getStartAt(), event.getEndAt());
    }

    private void validEventStatus(EventStatus status) {
        if (!status.isCanJoin()) {
            throw new BadRequestException("can_not_join_status", "This event type is " + status);
        }
    }

    private void validEventPeriod(ZonedDateTime startAt, ZonedDateTime endAt) {
        validEventStartDate(startAt);
        validEventEndDate(endAt);
    }

    private void validEventStartDate(ZonedDateTime startAt) {
        if (!ZonedDateTime.now().isAfter(startAt)) {
            throw new BadRequestException("not_started_event", "Event start " + startAt);
        }
    }

    private void validEventEndDate(ZonedDateTime endAt) {
        if (endAt == null){
            return;
        }

        if (!ZonedDateTime.now().isBefore(endAt)) {
            throw new BadRequestException("already_ended_event", "Event ended " + endAt);
        }
    }
}
