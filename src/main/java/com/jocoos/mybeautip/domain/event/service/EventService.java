package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.converter.EventConverter;
import com.jocoos.mybeautip.domain.event.dto.*;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.event.dto.EventStatusResponse.addFirstAllEvent;
import static com.jocoos.mybeautip.domain.event.persistence.domain.Event.getIds;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventConverter eventConverter;
    private final EventDao eventDao;

    @Transactional(readOnly = true)
    public List<EventListResponse> getEventList(EventType eventType) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("statusSorting"), Sort.Order.asc("sorting"), Sort.Order.desc("id")));
        // FIXME Dynamic Query to QueryDSL
        List<Event> eventList;
        if (eventType == null) {
            eventList = eventRepository.findByStatusIn(EventStatus.visibleEventStatus, pageable);
        } else {
            eventList = eventRepository.findByTypeAndStatusIn(eventType, EventStatus.visibleEventStatus, pageable);
        }

        return eventConverter.convertToListResponse(eventList);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event_not_found", "Not found event. id - " + eventId));

        return eventConverter.convertToResponse(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getProgressEventByType(EventType type) {
        return eventRepository.findByTypeAndStatus(type, EventStatus.PROGRESS);
    }

    @Transactional(readOnly = true)
    public List<Event> getEventTitle(Set<Long> eventIds) {
        return eventRepository.findByIdIn(eventIds);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getEventTitleMap(Set<Long> eventIds) {
        return getEventTitle(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, Event::getTitle));
    }

    @Transactional(readOnly = true)
    public List<EventStatusResponse> getEventStatesWithNum() {
        return addFirstAllEvent(eventDao.getEventStatesWithNum());
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminEventListResponse> getEvents(EventSearchCondition condition) {
        List<Event> events = eventDao.getEvents(condition);
        Map<Long, Long> countMap = eventDao.getJoinCountMap(getIds(events));
        Long totalCount = eventDao.getTotalCount(condition);
        return new PageResponse<>(totalCount, AdminEventListResponse.from(events, countMap));
    }
}
