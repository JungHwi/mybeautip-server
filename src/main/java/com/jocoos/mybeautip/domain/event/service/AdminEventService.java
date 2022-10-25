package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.converter.AdminEventConverter;
import com.jocoos.mybeautip.domain.event.dto.AdminEventResponse;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.dto.PageResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.event.dto.EventStatusResponse.addFirstAllEvent;

@RequiredArgsConstructor
@Service
public class AdminEventService {

    private final AdminEventConverter adminEventConverter;
    private final EventDao eventDao;

    @Transactional(readOnly = true)
    public List<EventStatusResponse> getEventStatesWithNum() {
        return addFirstAllEvent(eventDao.getEventStatesWithNum());
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminEventResponse> getEvents(EventSearchCondition condition) {
        List<EventSearchResult> events = eventDao.getEventsWithJoinCount(condition);
        Long totalCount = eventDao.getTotalCount(condition);
        return new PageResponse<>(totalCount, adminEventConverter.convertAllImages(events));
    }

    @Transactional(readOnly = true)
    public AdminEventResponse getEventAdmin(long eventId) {
        Event event = eventDao.getEvent(eventId);
        Long joinCount = eventDao.getJoinCount(event);
        return adminEventConverter.convertWithAllImages(event, joinCount);
    }
}
