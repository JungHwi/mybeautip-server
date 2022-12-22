package com.jocoos.mybeautip.domain.event.service.dao;

import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.converter.AdminEventConverter;
import com.jocoos.mybeautip.domain.event.dto.EditEventRequest;
import com.jocoos.mybeautip.domain.event.dto.EventRequest;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventJoinRepository;
import com.jocoos.mybeautip.domain.event.persistence.repository.EventRepository;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.event.code.EventStatus.PROGRESS;
import static com.jocoos.mybeautip.domain.event.code.EventStatus.WAIT;

@RequiredArgsConstructor
@Service
public class EventDao {

    private final EventRepository repository;
    private final EventJoinRepository joinRepository;
    private final AdminEventConverter converter;

    @Transactional
    public Event create(EventRequest request) {
        Event event = converter.convert(request);
        return repository.save(event);
    }

    @Transactional
    public Event edit(Event originalEvent, EditEventRequest request) {
        Event event = converter.convertForEdit(originalEvent, request);
        event.setEventProductList(originalEvent.getEventProductList());
        return repository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Not found event. id - " + eventId));
    }

    @Transactional(readOnly = true)
    public List<Event> getVisibleEvents(EventType type) {
        EventSearchCondition condition = EventSearchCondition.builder()
                .type(type)
                .statuses(EventStatus.visibleEventStatus)
                .isVisible(true)
                .build();
        return repository.getEvents(condition);
    }

    @Transactional(readOnly = true)
    public Map<EventStatus, Long> getJoinCountMapGroupByEventStatus() {
        return repository.getEventStatesWithNum();
    }

    @Transactional(readOnly = true)
    public List<EventSearchResult> getEventsWithJoinCount(EventSearchCondition condition) {
        return repository.getEventsWithJoinCount(condition);
    }

    @Transactional(readOnly = true)
    public Long getTotalCount(EventSearchCondition condition) {
        return repository.getTotalCount(condition);
    }

    @Transactional(readOnly = true)
    public Long getJoinCount(Event event) {
        return joinRepository.countByEvent(event);
    }

    @Transactional(readOnly = true)
    public List<Event> findStartEvent() {
        return repository.findByReservationAtLessThanEqualAndStatus(ZonedDateTime.now(), WAIT);
    }

    @Transactional(readOnly = true)
    public List<Event> findEndEvent() {
        return repository.findByEndAtLessThanEqualAndStatus(ZonedDateTime.now(), PROGRESS);
    }

    @Transactional
    public int updateStatus(List<Long> ids, EventStatus status) {
        return repository.updateStatus(ids, status);
    }

    @Transactional
    public int updateStatusAndIsTopFixAndSorting(List<Long> ids, EventStatus status, Boolean isTopFix, Integer sorting) {
        return repository.updateStatusAndIsTopFixAndSorting(ids, status, isTopFix, sorting);
    }

    @Transactional(readOnly = true)
    public int getLastSortOrder() {
        Integer lastSortOrder = repository.getLastSortOrder();
        return lastSortOrder == null ? 1 : lastSortOrder + 1;
    }

    @Transactional
    public List<Long> arrangeByIndex(List<Long> sortedIds) {
        return repository.arrangeByIndex(sortedIds);
    }

    @Transactional
    public void hardDelete(Event event) {
        repository.delete(event);
    }
}
