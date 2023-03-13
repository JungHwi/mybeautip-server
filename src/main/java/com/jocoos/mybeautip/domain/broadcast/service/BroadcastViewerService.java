package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastViewerService {

    private final BroadcastViewerDao dao;
    private final BroadcastDao broadcastDao;
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
        FFLDirectMessageRequest messageRequest = FFLDirectMessageRequest.ofExile(memberId);
        flipFlopLiteService.directMessage(broadcast.getVideoKey(), messageRequest);

        return converter.converts(viewer);
    }

    public void visibleMessage(VisibleMessageRequest request) {
        Broadcast broadcast = broadcastDao.get(request.broadcastId());

        if (request.isVisible()) {
            flipFlopLiteService.unhideMessage(broadcast.getVideoKey(), request.messageId());
        } else {
            flipFlopLiteService.hideMessage(broadcast.getVideoKey(), request.messageId());
        }
    }
}
