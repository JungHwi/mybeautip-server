package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastEvent.BroadcastEditNotificationEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastNotification;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastDomainService;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastRelationService;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastVodService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.IsVisibleResponse;
import com.jocoos.mybeautip.global.dto.IdAndBooleanResponse.NotificationResponse;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.Between;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.ACTIVE_STATUSES;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;

@Slf4j
@RequiredArgsConstructor
@Service
public class BroadcastService {

    private final BroadcastDao broadcastDao;
    private final MemberDao memberDao;
    private final BroadcastPermissionDao permissionDao;
    private final BroadcastDomainService domainService;
    private final BroadcastVodService broadcastVodService;
    private final BroadcastRelationService relationService;
    private final BroadcastConverter converter;
    private final AwsS3Handler awsS3Handler;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public BroadcastResponse createBroadcastAndVod(BroadcastCreateRequest request, long creatorId) {
        validCanAccess(creatorId);
        Member member = memberDao.getMember(creatorId);
        Broadcast broadcast = domainService.create(request, creatorId);
        broadcastVodService.createVod(broadcast);
        awsS3Handler.copy(request.getThumbnail(), broadcast.getThumbnailUrlPath());
        return converter.toResponse(broadcast, member);
    }

    @Transactional(readOnly = true)
    public BroadcastResponse get(long broadcastId) {
        BroadcastSearchResult searchResult = broadcastDao.getSearchResult(broadcastId);
        return converter.toResponse(searchResult);
    }

    @Transactional(readOnly = true)
    public List<BroadcastListResponse> getList(BroadcastStatus status,
                                               LocalDate localDate,
                                               Long cursor,
                                               int size,
                                               String tokenUsername) {
        Sort sort = BroadcastSortField.getSortBy(status);
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .day(getDay(localDate))
                .cursor(cursor)
                .pageable(PageRequest.of(0, size, sort))
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition);
        Map<Long, BroadcastRelationInfo> relationInfoMap = relationService.getRelationInfoMap(tokenUsername, searchResults);
        return converter.toListResponse(searchResults, relationInfoMap);
    }

    @Transactional(readOnly = true)
    public BroadcastStatisticsResponse getStatistics(long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);

        ZonedDateTime endedAt = broadcast.getEndedAt() == null? ZonedDateTime.now() : broadcast.getEndedAt();
        Duration duration = Duration.between(broadcast.getStartedAt(), endedAt);

        return converter.converts(broadcast);
    }

    @Transactional
    public BroadcastResponse edit(Long broadcastId, BroadcastEditRequest request, Long requestMemberId) {
        BroadcastEditResult editResult = domainService.overwriteEdit(broadcastId, request, requestMemberId);
        Broadcast editedBroadcast = editResult.broadcast();
        if (editResult.isThumbnailChanged()) {
            awsS3Handler.editFiles(request.getThumbnails(), editedBroadcast.getThumbnailUrlPath());
        }
        eventPublisher.publishEvent(new BroadcastEditNotificationEvent(editResult));
        return converter.toResponse(editedBroadcast);
    }

    @Transactional(readOnly = true)
    public BroadcastDateListResponse getBroadcastDateList(ZoneId requestZoneId, Pageable pageable) {
        List<ZonedDateTime> startedAtList = broadcastDao.getStartedAtList(ACTIVE_STATUSES, pageable).getContent();
        List<LocalDate> requestZonedLocalDates = startedAtList.stream()
                .map(time -> time.withZoneSameInstant(requestZoneId).toLocalDate())
                .distinct()
                .toList();
        return new BroadcastDateListResponse(requestZonedLocalDates);
    }

    @Transactional
    public BroadcastResponse changeStatus(long broadcastId, BroadcastStatus changeStatus, long memberId) {
        if (!changeStatus.isCanManuallyChange()) {
            throw new BadRequestException(changeStatus + " can not change manually");
        }
        BroadcastEditResult editResult = domainService.changeStatus(broadcastId, changeStatus);
        eventPublisher.publishEvent(new BroadcastEditNotificationEvent(editResult));
        return converter.toResponse(editResult.broadcast());
    }

    @Transactional
    public NotificationResponse setNotify(Long broadcastId, boolean isNotifyNeeded, Long memberId) {
        BroadcastNotification notification = domainService.setNotify(broadcastId, isNotifyNeeded, memberId);
        return new NotificationResponse(notification.getBroadcast().getId(), notification.getIsNotifyNeeded());
    }

    @Transactional
    public int report(long broadcastId, long reporterId, BroadcastReportRequest request) {
        return switch (request.type()) {
            case BROADCAST -> domainService.broadcastReport(broadcastId, reporterId, request.reason());
            case MESSAGE ->
                    domainService.messageReport(broadcastId, reporterId, request.reportedId(), request.reason(), request.description());
        };
    }

    @Transactional
    public IsVisibleResponse chooseVodVisibilityByEndOfBroadcast(Long broadcastId, boolean isVisible) {
        return broadcastVodService.chooseVodVisibilityByEndOfBroadcast(broadcastId, isVisible);
    }

    public HeartCountResponse addHeartCount(Long broadcastId, int heartCount) {
        Broadcast broadcast = domainService.addHeartCount(broadcastId, heartCount);
        return new HeartCountResponse(broadcast.getId(), broadcast.getHeartCount());
    }

    @Transactional(readOnly = true)
    public HeartCountResponse getHeartCount(Long broadcastId) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        return new HeartCountResponse(broadcast.getId(), broadcast.getStatistics().getHeartCount());
    }

    private void validCanAccess(long creatorId) {
        if (!permissionDao.canBroadcast(creatorId)) {
            throw new AccessDeniedException("Only influencer can request");
        }
    }

    private Between getDay(LocalDate localDate) {
        return localDate == null ? null : Between.day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? ACTIVE_STATUSES : getSearchStatuses(status);
    }
}
