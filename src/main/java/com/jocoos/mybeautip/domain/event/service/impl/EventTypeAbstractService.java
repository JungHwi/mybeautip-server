package com.jocoos.mybeautip.domain.event.service.impl;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.EventTypeService;
import com.jocoos.mybeautip.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public abstract class EventTypeAbstractService implements EventTypeService {

    public void validEvent(Event event) {
        validEventStatus(event.getStatus());
        validEventPeriod(event.getStartAt(), event.getEndAt());
    }

    private void validEventStatus(EventStatus status) {
        if (!status.isCanJoin()) {
            throw new BadRequestException("can_not_join_status", "This event type is " + status);
        }
    }

    private void validEventPeriod(LocalDateTime startAt,LocalDateTime endAt) {
        validEventStartDate(startAt);
        validEventEndDate(endAt);
    }

    private void validEventStartDate(LocalDateTime startAt) {
        if (!LocalDateTime.now().isAfter(startAt)) {
            throw new BadRequestException("not_started_event", "Event start " + startAt);
        }
    }

    private void validEventEndDate(LocalDateTime endAt) {
        if (endAt == null){
            return;
        }

        if (!LocalDateTime.now().isBefore(endAt)) {
            throw new BadRequestException("already_ended_event", "Event ended " + endAt);
        }
    }
}
