package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastBulkUpdateStatusCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;

@RequiredArgsConstructor
@Service
public class BroadcastStatusService {

    private final BroadcastDao broadcastDao;
    private final BroadcastFFLService fflService;

    @Transactional
    public void changeStatus(Broadcast broadcast, BroadcastStatus changeStatus) {
        switch (changeStatus) {
            case LIVE -> toLive(broadcast);
            case END -> toEnd(broadcast, changeStatus);
            case CANCEL -> toCancel(broadcast, changeStatus);
            default -> throw new BadRequestException("");
        }
    }

    @Transactional
    public long bulkChangeStatus(BroadcastBulkUpdateStatusCommand command) {
        return switch (command.updateStatus()) {
            case READY -> bulkUpdateToReady(command);
            case END -> bulkUpdateToEnd(command);
            case CANCEL -> bulkUpdateToCancel(command);
            default -> throw new BadRequestException("");
        };
    }

    private long bulkUpdateToReady(BroadcastBulkUpdateStatusCommand command) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToReady(command);
        for (Long videoKey : result.videoKeys()) {
            fflService.sendChangeBroadcastStatusMessage(videoKey, READY);
        }
        return result.count();
    }

    private Broadcast toLive(Broadcast broadcast) {
        ExternalBroadcastInfo info = fflService.startFFLVideoRoomAndSendChatMessage(broadcast);
        broadcast.start(info.liveUrl(), info.lastModifiedAt());
        return broadcast;
    }

    private long bulkUpdateToEnd(BroadcastBulkUpdateStatusCommand command) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToFinish(command);
        for (Long videoKey : result.videoKeys()) {
            fflService.endVideoRoomAndSendChatMessage(videoKey);
        }
        return result.count();
    }

    private Broadcast toEnd(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.endVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }

    private long bulkUpdateToCancel(BroadcastBulkUpdateStatusCommand condition) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToFinish(condition);
        for (Long videoKey : result.videoKeys()) {
            fflService.cancelVideoRoomAndSendChatMessage(videoKey);
        }
        return result.count();
    }

    private Broadcast toCancel(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.cancelVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }
}