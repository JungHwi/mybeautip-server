package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.converter.EventConverter;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.EVENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventConverter eventConverter;

    @Transactional
    public List<EventListResponse> getEventList(long cursor, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Order.asc("statusSorting"), Sort.Order.asc("sorting"), Sort.Order.desc("id"), Sort.Order.desc("endAt")));
        Slice<Event> eventSlice = eventRepository.findByIdLessThanAndStatusIn(cursor, EventStatus.visibleEventStatus, pageable);

        return eventConverter.convertToListResponse(eventSlice.getContent());
    }

    @Transactional
    public EventResponse getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(EVENT_NOT_FOUND, "Not found event. id - " + eventId));

        return eventConverter.convertToResponse(event);
    }
}
