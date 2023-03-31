package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastMessageRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLStreamKeyResponse;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastViewerConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.*;
import com.jocoos.mybeautip.domain.broadcast.event.BroadcastViewerStatisticsEvent;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastFFLService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerCursorCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastViewerService {

    private final BatchBroadcastService batchBroadcastService;
    private final BroadcastViewerDao dao;
    private final BroadcastDao broadcastDao;
    private final MemberDao memberDao;
    private final FlipFlopLiteService flipFlopLiteService;
    private final BroadcastFFLService fflService;
    private final BroadcastViewerConverter converter;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<ViewerResponse> search(ViewerSearchCondition condition) {
        if (condition.getCursor() != null) {
            BroadcastViewer viewer = dao.getBroadcastViewer(condition.getBroadcastId(), condition.getCursor());
            ViewerCursorCondition cursorCondition = new ViewerCursorCondition(viewer.getType(), viewer.getSortedUsername());
            condition.setCursorCondition(cursorCondition);
        }

        List<ViewerSearchResult> searchList = dao.search(condition);

        return converter.converts(searchList);
    }

    @Transactional(readOnly = true)
    public ViewerResponse get(long broadcastId, long memberId) {
        ViewerSearchResult viewer = dao.get(broadcastId, memberId);

        return converter.converts(viewer);
    }

    @Transactional
    public ViewerResponse grantManager(GrantManagerRequest request) {
        BroadcastViewer viewer = dao.grantManager(request);
        Broadcast broadcast = broadcastDao.get(request.broadcastId());

        FFLDirectMessageRequest messageRequest = FFLDirectMessageRequest.of(request, List.of(request.memberId()));
        flipFlopLiteService.directMessage(broadcast.getVideoKey(), messageRequest);

        return converter.converts(viewer);
    }

    @Transactional
    public ViewerResponse suspend(ViewerSuspendRequest request) {
        BroadcastViewer viewer = dao.suspend(request);

        Broadcast broadcast = broadcastDao.get(request.broadcastId());
        FFLDirectMessageRequest messageRequest = FFLDirectMessageRequest.of(request);
        flipFlopLiteService.directMessage(broadcast.getVideoKey(), messageRequest);

        return converter.converts(viewer);
    }

    @Transactional
    public ViewerResponse exile(long broadcastId, long memberId) {
        BroadcastViewer viewer = dao.exile(broadcastId, memberId);

        Broadcast broadcast = broadcastDao.get(broadcastId);
        FFLDirectMessageRequest messageRequest = FFLDirectMessageRequest.ofExile(memberId, viewer.getUsername());
        flipFlopLiteService.directMessage(broadcast.getVideoKey(), messageRequest);

        return converter.converts(viewer);
    }

    public StreamKeyResponse getStreamKey(long memberId) {
        FFLStreamKeyResponse response = flipFlopLiteService.getStreamKey(memberId);

        Long broadcastId = null;
        if (response.videoRoom() != null && response.videoRoom().id() != null) {
            Broadcast broadcast = broadcastDao.getByVideoKey(response.videoRoom().id());
            broadcastId = broadcast.getId();
        }

        return new StreamKeyResponse(response.streamKey(),
                response.streamKeyState(),
                broadcastId);
    }

    public void visibleMessage(VisibleMessageRequest request) {
        Broadcast broadcast = broadcastDao.get(request.broadcastId());
        flipFlopLiteService.visibleMessage(broadcast.getVideoKey(), request);
        flipFlopLiteService.broadcastMessage(broadcast.getVideoKey(), FFLBroadcastMessageRequest.ofVisibleMessage(request));
    }

    @Transactional
    public void syncViewer() {
        List<Broadcast> broadcastList = broadcastDao.findByStatusIn(BroadcastStatus.NEED_SYNC_MEMBER_STATUS);

        for (Broadcast broadcast : broadcastList) {
            batchBroadcastService.syncViewer(broadcast);
        }
    }

    @Transactional
    public ViewerResponse join(long broadcastId, BroadcastViewerVo viewer) {
        Broadcast broadcast = broadcastDao.get(broadcastId);

        BroadcastViewer broadcastViewer = null;
        if (dao.exist(broadcastId, viewer.memberId())) {
            broadcastViewer = dao.findBroadcastViewer(broadcastId, viewer.memberId())
                    .orElseThrow(() -> new MemberNotFoundException(viewer.memberId()));
            broadcastViewer.reJoin(viewer.type(), viewer.username());
        } else {
            broadcastViewer = dao.save(new BroadcastViewer(broadcast, viewer));
        }

        BroadcastKey broadcastKey = getBroadcastKey(viewer, broadcast.getMemberId(), broadcast.getChatChannelKey());

        eventPublisher.publishEvent(new BroadcastViewerStatisticsEvent(broadcastId));

        return converter.converts(broadcastViewer, broadcastKey);
    }

    @Transactional
    public ViewerResponse out(long broadcastId, BroadcastViewerVo viewer) {
        BroadcastViewer broadcastViewer = dao.findBroadcastViewer(broadcastId, viewer.memberId())
                .orElseThrow(() -> new MemberNotFoundException(viewer.memberId()));

        broadcastViewer.inactive();

        if (viewer.type() == BroadcastViewerType.MANAGER) {
            Broadcast broadcast = broadcastDao.get(broadcastId);
            FFLDirectMessageRequest request = FFLDirectMessageRequest.ofManagerOut(broadcast.getMemberId());
            flipFlopLiteService.directMessage(broadcast.getVideoKey(), request);
        }

        eventPublisher.publishEvent(new BroadcastViewerStatisticsEvent(broadcastId));

        return converter.converts(broadcastViewer);
    }

    private BroadcastKey getBroadcastKey(BroadcastViewerVo viewer, Long ownerId, String channelKey) {
        if (viewer.type() == BroadcastViewerType.GUEST) {
            return fflService.getGuestBroadcastKey(viewer.username(), channelKey);
        }

        if (isStreamKeyNeeded(viewer.memberId(), ownerId)) {
            return fflService.getBroadcastKeyWithStreamKey(viewer.memberId(), channelKey, ownerId);
        }
        return fflService.getBroadcastKey(viewer.memberId(), channelKey);
    }

    private boolean isStreamKeyNeeded(Long requestMemberId, Long broadcastOwnerId) {
        return broadcastOwnerId.equals(requestMemberId) || memberDao.isAdmin(requestMemberId);
    }
}
