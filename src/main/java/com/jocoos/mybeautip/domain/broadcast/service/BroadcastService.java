package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.converter.VodConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastCreateRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastStartedAtResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastCategory;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastCategoryDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastReportDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
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
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;
import static com.jocoos.mybeautip.global.code.UrlDirectory.VOD;
import static com.jocoos.mybeautip.global.validator.StringValidator.validateMaxLengthWithoutWhiteSpace;

@RequiredArgsConstructor
@Service
public class BroadcastService {

    private final BroadcastDao broadcastDao;
    private final BroadcastCategoryDao categoryDao;
    private final BroadcastReportDao reportDao;
    private final BroadcastConverter converter;
    private final VodConverter vodConverter;
    private final AwsS3Handler awsS3Handler;
    private final FlipFlopLiteService flipFlopLiteService;
    private final VodDao vodDao;

    @Transactional
    public BroadcastResponse createBroadcastAndVod(BroadcastCreateRequest request, long memberId) {
        validCreateRequest(request);
        BroadcastCategory category = categoryDao.getCategory(request.getCategoryId());
        ExternalBroadcastInfo externalInfo = flipFlopLiteService.createVideoRoom(request, memberId);
        Broadcast broadcast = converter.toEntity(request, externalInfo, category, memberId);
        broadcastDao.save(broadcast);

        Vod vod = vodConverter.toVod(broadcast);
        vodDao.save(vod);

        String thumbnailUrl = request.getThumbnailUrl();
        awsS3Handler.copy(thumbnailUrl, BROADCAST.getDirectory(broadcast.getId()));
        awsS3Handler.copy(thumbnailUrl, VOD.getDirectory(vod.getId()));
        return null;
    }

    @Transactional(readOnly = true)
    public BroadcastResponse get(long broadcastId) {
        BroadcastSearchResult searchResult = broadcastDao.getWithMemberAndCategory(broadcastId);
        return converter.toResponse(searchResult);
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

    // 외부 API 호출 비용을 고려해 생성자 호출 전에 서비스 레이어에서 검증
    private void validCreateRequest(BroadcastCreateRequest request) {
        validateMaxLengthWithoutWhiteSpace(request.getTitle(), 25, "title");
        validateMaxLengthWithoutWhiteSpace(request.getNotice(), 100, "notice");
    }

    private Day getDay(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? DEFAULT_SEARCH_STATUSES : getSearchStatuses(status);
    }
}
