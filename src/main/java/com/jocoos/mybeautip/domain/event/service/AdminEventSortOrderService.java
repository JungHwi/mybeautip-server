package com.jocoos.mybeautip.domain.event.service;

import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.service.dao.EventDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminEventSortOrderService {

    private final EventDao eventDao;

    @Transactional
    public Long topFix(Long eventId, boolean isTopFix) {
        Event event = eventDao.getEvent(eventId);
        changeIsTopFixAndSorting(isTopFix, event);
        return event.getId();
    }

    @Transactional
    public List<Long> changeSortOrder(List<Long> sortedIds) {
        return eventDao.arrangeByIndex(sortedIds);
    }

    private void changeIsTopFixAndSorting(boolean isTopFix, Event event) {
        if (isTopFix) {
            int lastSortOrder = eventDao.getLastSortOrder();
            event.fix(lastSortOrder);
        } else {
            event.unFix();
        }
    }
}
