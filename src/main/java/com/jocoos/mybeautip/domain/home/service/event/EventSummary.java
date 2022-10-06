package com.jocoos.mybeautip.domain.home.service.event;

import com.jocoos.mybeautip.domain.event.converter.EventConverter;
import com.jocoos.mybeautip.domain.event.dto.EventListResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventSummary {

    private final EventDao dao;
    private final EventConverter converter;

    @Transactional(readOnly = true)
    public List<EventListResponse> summary(int summaryEventNum) {
        List<Event> events = dao.summary(summaryEventNum);
        return converter.convertToListResponse(events);
    }
}
