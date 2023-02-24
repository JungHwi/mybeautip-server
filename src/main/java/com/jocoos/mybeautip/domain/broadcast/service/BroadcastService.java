package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStartedAtResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.Day;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastCategoryType.GROUP;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.DEFAULT_SEARCH_STATUSES;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;

@RequiredArgsConstructor
@Service
public class BroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastCategoryDao categoryDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final MemberDao memberDao;
    private final VodDao vodDao;

    @Transactional
    public BroadcastListResponse createBroadcastAndVod(BroadcastRequest request) {
        BroadcastCategory category = categoryDao.getCategory(request.getCategoryId());

        if (category.isType(GROUP)) {
            throw new BadRequestException("");
        }

        // TODO FFL LOGIC SHOULD BE CONFIRMED
        // call flip flop lite

        // save broadcast

        // save vod

        // return
        return null;
    }

    @Transactional(readOnly = true)
    public BroadcastResponse get(long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        Member member = memberDao.getMember(broadcast.getMemberId());
        return converter.toResponse(broadcast, member);
    }

    @Transactional(readOnly = true)
    public List<BroadcastListResponse> getList(BroadcastStatus status, LocalDate localDate, Long cursor, int size) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .day(getDay(localDate))
                .cursor(cursor)
                .pageable(Pageable.ofSize(size))
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        return converter.toListResponse(searchResults);
    }

    @Transactional(readOnly = true)
    public BroadcastStartedAtResponse getDateList() {
        List<ZonedDateTime> results = broadcastDao.getStartedAtList(Pageable.ofSize(10)).getContent();
        return new BroadcastStartedAtResponse(results);
    }

    // TODO 응답값 논의 필요
    @Transactional
    public void report(long broadcastId, long reporterId, String description) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastReport report = new BroadcastReport(broadcast, reporterId, description);
        reportDao.save(report);
    }

    private Day getDay(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? DEFAULT_SEARCH_STATUSES : getSearchStatuses(status);
    }
}
