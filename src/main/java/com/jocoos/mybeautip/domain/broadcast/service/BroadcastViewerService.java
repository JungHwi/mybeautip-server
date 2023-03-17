package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLBroadcastMessageRequest;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastViewerConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.GrantManagerRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerSuspendRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.VisibleMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerCursorCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.GUEST_TOKEN_PREFIX;
import static com.jocoos.mybeautip.global.constant.SignConstant.EMPTY_STRING;

@Service
@RequiredArgsConstructor
public class BroadcastViewerService {

    private final BatchBroadcastService batchBroadcastService;
    private final BroadcastViewerDao dao;
    private final BroadcastDao broadcastDao;
    private final MemberDao memberDao;
    private final FlipFlopLiteService flipFlopLiteService;
    private final BroadcastViewerConverter converter;

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

        FFLDirectMessageRequest messageRequest = FFLDirectMessageRequest.of(request, List.of(broadcast.getMemberId(), request.memberId()));
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
    public ViewerResponse join(long broadcastId, String userInfo) {
        Broadcast broadcast = broadcastDao.get(broadcastId);
        BroadcastViewerType type = BroadcastViewerType.MEMBER;
        Long memberId = Long.parseLong(userInfo.replace(GUEST_TOKEN_PREFIX, EMPTY_STRING));
        String username = userInfo;

        if (userInfo.startsWith(GUEST_TOKEN_PREFIX)) {
            type = BroadcastViewerType.GUEST;
        } else if (Objects.equals(broadcast.getMemberId(), memberId)){
            type = BroadcastViewerType.OWNER;
        } else if (memberDao.isAdmin(memberId)) {
            type = BroadcastViewerType.ADMIN;
        }

        if (type != BroadcastViewerType.GUEST) {
            Member member = memberDao.getMember(memberId);
            username = member.getUsername();
        }

        BroadcastViewer broadcastViewer = null;
        if (dao.exist(broadcastId, memberId)) {
            broadcastViewer = dao.findBroadcastViewer(broadcastId, memberId)
                    .orElseThrow(() -> new MemberNotFoundException(memberId));
            broadcastViewer.reJoin(type, username);
        } else {
            broadcastViewer = dao.save(new BroadcastViewer(broadcast, type, memberId, username));
        }

        return converter.converts(broadcastViewer);
    }
}
