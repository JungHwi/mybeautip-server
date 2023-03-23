package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BroadcastStatisticsService {

    private final BroadcastDao broadcastDao;
    private final BroadcastViewerDao viewerDao;
}
