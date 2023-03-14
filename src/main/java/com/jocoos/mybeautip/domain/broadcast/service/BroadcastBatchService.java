package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.FFLDirectMessageRequest;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastViewerDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastViewerVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class BroadcastBatchService {

    private final BroadcastViewerDao broadcastViewerDao;
    private final FlipFlopLiteService flipFlopLiteService;


    @Transactional(propagation = REQUIRES_NEW)
    public void syncViewer(Broadcast broadcast) {
        List<BroadcastViewerVo> newViewers = flipFlopLiteService.getAllChatMembers(broadcast.getVideoKey());
        List<Long> outMangerIds = broadcast.syncViewer(newViewers);

        if (!outMangerIds.isEmpty()) {
            FFLDirectMessageRequest request = new FFLDirectMessageRequest();
            flipFlopLiteService.directMessage(broadcast.getVideoKey(), )
        }
    }
}
