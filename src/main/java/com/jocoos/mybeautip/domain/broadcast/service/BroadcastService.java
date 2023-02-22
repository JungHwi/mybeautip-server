package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStartedAtListResponse;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.global.vo.Day;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.DEFAULT_SEARCH_STATUSES;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;

@RequiredArgsConstructor
@Service
public class BroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastConverter converter;

    @Transactional(readOnly = true)
    public List<BroadcastResponse> getList(BroadcastStatus status, LocalDate localDate, Long cursor, int size) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .day(getDay(localDate))
                .cursor(cursor)
                .pageable(Pageable.ofSize(size))
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        return converter.toResponse(searchResults);
    }

    @Transactional(readOnly = true)
    public BroadcastStartedAtListResponse getDateList() {
        List<ZonedDateTime> results = broadcastDao.getStartedAtList(Pageable.ofSize(10)).getContent();
        return new BroadcastStartedAtListResponse(results);
    }

    private Day getDay(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? DEFAULT_SEARCH_STATUSES : getSearchStatuses(status);
    }
}
