package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastViewerConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.GrantManagerRequest;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.ViewerSuspendRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
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

        return converter.converts(viewer);
    }

    @Transactional
    public ViewerResponse suspend(ViewerSuspendRequest request) {
        BroadcastViewer viewer = dao.suspend(request);

        return converter.converts(viewer);
    }

    @Transactional
    public ViewerResponse exile(long broadcastId, long memberId) {
        BroadcastViewer viewer = dao.exile(broadcastId, memberId);

        return converter.converts(viewer);
    }
}
