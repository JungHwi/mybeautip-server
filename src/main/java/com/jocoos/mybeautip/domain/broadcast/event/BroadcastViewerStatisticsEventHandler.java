package com.jocoos.mybeautip.domain.broadcast.event;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerCountResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BroadcastViewerStatisticsEventHandler {
    private final BroadcastDao broadcastDao;
    private final BroadcastViewerDao viewerDao;

    @Async
    @Transactional
    @TransactionalEventListener
    public void publish(BroadcastViewerStatisticsEvent event) {
        List<ViewerCountResult> result = viewerDao.getViewerCount(event.broadcastId());
        Broadcast broadcast = broadcastDao.get(event.broadcastId());

        broadcast.getStatistics().refresh(result);
    }
}
