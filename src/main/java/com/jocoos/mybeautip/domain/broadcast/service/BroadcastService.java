package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.converter.VodConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.system.service.dao.SystemOptionDao;
import com.jocoos.mybeautip.global.dto.IdAndCountResponse.ReportCountResponse;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
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

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.DEFAULT_SEARCH_STATUSES;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.getSearchStatuses;
import static com.jocoos.mybeautip.domain.system.code.SystemOptionType.FREE_LIVE_PERMISSION;

@RequiredArgsConstructor
@Service
public class BroadcastService {

    private final BroadcastDao broadcastDao;
    private final MemberDao memberDao;
    private final VodDao vodDao;
    private final InfluencerDao influencerDao;
    private final SystemOptionDao systemOptionDao;
    private final BroadcastConverter converter;
    private final VodConverter vodConverter;
    private final AwsS3Handler awsS3Handler;
    private final BroadcastDomainService domainService;
    private final BroadcastParticipantInfoService participantInfoService;

    @Transactional
    public BroadcastResponse createBroadcastAndVod(BroadcastCreateRequest request, long creatorId) {
        validCanAccess(creatorId);
        Member member = memberDao.getMember(creatorId);
        Broadcast broadcast = domainService.create(request, creatorId);
        createVod(broadcast);
        awsS3Handler.copy(request.getThumbnail(), broadcast.getThumbnailUrlPath());
        return converter.toResponse(broadcast, member);
    }

    @Transactional(readOnly = true)
    public BroadcastResponse get(long broadcastId, long requestMemberId) {
        BroadcastSearchResult searchResult = broadcastDao.getWithMemberAndCategory(broadcastId);
        Member owner = memberDao.getMember(searchResult.getCreatedBy().getId());
        BroadcastParticipantInfo participantInfo = participantInfoService.getParticipantInfo(requestMemberId, searchResult);
        return converter.toResponse(searchResult, owner, participantInfo);
    }

    @Transactional(readOnly = true)
    public List<BroadcastListResponse> getList(BroadcastStatus status, LocalDate localDate, Long cursor, int size) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .statuses(getStatuses(status))
                .day(getDay(localDate))
                .cursor(cursor)
                .pageable(Pageable.ofSize(size))
                .build();
        List<BroadcastSearchResult> searchResults = broadcastDao.getList(condition).getContent();
        return converter.toListResponse(searchResults);
    }

    @Transactional

    public BroadcastResponse edit(long broadcastId, BroadcastEditRequest request, Long requestMemberId) {
        Broadcast editedBroadcast = domainService.edit(broadcastId, request, requestMemberId);
        awsS3Handler.editFiles(request.getThumbnails(), editedBroadcast.getThumbnailUrlPath());
        return converter.toResponse(editedBroadcast);
    }

    @Transactional(readOnly = true)
    public BroadcastDateListResponse getBroadcastDateList(Pageable pageable) {
        List<ZonedDateTime> results = broadcastDao.getStartedAtList(pageable).getContent();
        return new BroadcastDateListResponse(results);
    }

    @Transactional
    public BroadcastResponse changeStatus(long broadcastId, BroadcastStatus changeStatus, long memberId) {
        if (!changeStatus.isCanManuallyChange()) {
            throw new BadRequestException(changeStatus + " can not change manually");
        }
        Broadcast broadcast = domainService.changeStatus(broadcastId, changeStatus);
        return converter.toResponse(broadcast);
    }

    @Transactional
    public ReportCountResponse report(long broadcastId, long reporterId, String description) {
        Broadcast broadcast = domainService.report(broadcastId, reporterId, description);
        return new ReportCountResponse(broadcast.getId(), broadcast.getReportCount());
    }

    private void createVod(Broadcast broadcast) {
        Vod vod = vodConverter.init(broadcast);
        vodDao.save(vod);
    }

    private void validCanAccess(long creatorId) {
        if (!systemOptionDao.getSystemOption(FREE_LIVE_PERMISSION)
                && !influencerDao.isInfluencer(creatorId)) {
            throw new AccessDeniedException("Only influencer can request");
        }
    }

    private Day getDay(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? DEFAULT_SEARCH_STATUSES : getSearchStatuses(status);
    }
}
