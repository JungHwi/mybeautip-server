package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.converter.VodConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.system.service.dao.SystemOptionDao;
import com.jocoos.mybeautip.global.dto.FileDto;
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

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;
import static com.jocoos.mybeautip.domain.system.code.SystemOptionType.FREE_LIVE_PERMISSION;
import static com.jocoos.mybeautip.global.code.UrlDirectory.BROADCAST;

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
    private final FlipFlopLiteService flipFlopLiteService;

    @Transactional(readOnly = true)
    public BroadcastResponse get(long broadcastId, long memberId) {
        BroadcastSearchResult searchResult = broadcastDao.getWithMemberAndCategory(broadcastId);
        String streamKey = getStreamKey(memberId, searchResult.getMember().getId());
        // TODO 채팅 토큰 발급
        return converter.toResponse(searchResult, streamKey);
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

    @Transactional
    public BroadcastResponse createBroadcastAndVod(BroadcastCreateRequest request, long creatorId) {
        validCanAccess(creatorId);
        Member creator = memberDao.getMember(creatorId);
        Broadcast broadcast = domainService.create(request, creatorId);
        createVod(broadcast);
        flipFlopLiteService.createChatRoom(broadcast.getVideoKey());
        awsS3Handler.copy(request.getThumbnailUrl(), BROADCAST.getDirectory(broadcast.getId()));
        return converter.toResponse(broadcast, creator);
    }

    @Transactional
    public BroadcastResponse edit(long broadcastId, BroadcastEditRequest request) {
        // FIXME Request get member query should replace by join
        BroadcastEditResult editResult = domainService.edit(broadcastId, request);
        Broadcast editedBroadcast = editResult.broadcast();
        Member member = memberDao.getMember(editedBroadcast.getMemberId());
        changeThumbnail(editedBroadcast, editResult.originalThumbnailUrl());
        return converter.toResponse(editedBroadcast, member);
    }

    // Broadcast reservation date max is 2 weeks so result count wii be max 14 by this reason no paging
    @Transactional(readOnly = true)
    public BroadcastDateListResponse getBroadcastDateList() {
        List<ZonedDateTime> results = broadcastDao.getStartedAtList().getContent();
        return new BroadcastDateListResponse(results);
    }

    @Transactional
    public BroadcastResponse changeStatus(long broadcastId, BroadcastStatus changeStatus, long memberId) {
        if (changeStatus == SCHEDULED || changeStatus == READY) {
            throw new BadRequestException(changeStatus + " can not change manually");
        }
        Broadcast broadcast = domainService.changeStatus(broadcastId, changeStatus);
        Member member = memberDao.getMember(memberId);
        return converter.toResponse(broadcast, member);
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

    private String getStreamKey(long memberId, long creatorId) {
        if (memberId == creatorId) {
            return flipFlopLiteService.getStreamKey(memberId);
        }
        return null;
    }

    private void changeThumbnail(Broadcast broadcast, String originalThumbnailUrl) {
        if (!broadcast.isThumbnailEq(originalThumbnailUrl)) {
            List<FileDto> files = FileDto.getUploadAndDeleteFileDtoList(broadcast.getThumbnailUrl(), originalThumbnailUrl);
            awsS3Handler.editFiles(files, broadcast.getThumbnailUrlPath());
        }
    }

    private Day getDay(LocalDate localDate) {
        return localDate == null ? null : new Day(localDate, ZoneId.of("Asia/Seoul"));
    }

    private List<BroadcastStatus> getStatuses(BroadcastStatus status) {
        return status == null ? DEFAULT_SEARCH_STATUSES : getSearchStatuses(status);
    }
}
