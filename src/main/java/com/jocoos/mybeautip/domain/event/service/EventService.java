package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.converter.EventConverter;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.dto.EventResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.event.code.EventStatus.PROGRESS;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventConverter eventConverter;
    private final EventDao dao;

    @Transactional(readOnly = true)
    public List<EventListResponse> getEventList(EventType eventType, EventStatus status) {
        List<Event> events = getEvents(eventType, status);
        return eventConverter.convertToListResponse(events);
    }

    private List<Event> getEvents(EventType eventType, EventStatus status) {
        if (PROGRESS.equals(status)) {
            return dao.getProgressEvents(eventType);
        }
        return dao.getVisibleEvents(eventType);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event. id - " + eventId));

        return eventConverter.convertToResponse(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getProgressEventByType(EventType type) {
        return eventRepository.findByTypeAndStatus(type, PROGRESS);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventTitle(Set<Long> eventIds) {
        return eventRepository.findByIdIn(eventIds);
    }

    public Map<Long, String> getEventTitleMap(Set<Long> eventIds) {
        return getEventTitle(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, Event::getTitle));
    }
}
