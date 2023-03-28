package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.client.flipfloplite.exception.FFLException;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastBulkUpdateStatusCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult.OriginalInfo;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;

@Log4j2
@RequiredArgsConstructor
@Service
public class BroadcastStatusService {

    private final BroadcastDao broadcastDao;
    private final BroadcastFFLService fflService;


    @Transactional
    public BroadcastEditResult changeStatus(Broadcast broadcast, BroadcastStatus changeStatus) {
        OriginalInfo originalInfo = new OriginalInfo(broadcast);
        Broadcast editedBroadcast = switch (changeStatus) {
            case LIVE -> toLive(broadcast);
            case END -> toEnd(broadcast, changeStatus);
            case CANCEL -> toCancel(broadcast, changeStatus);
            default -> throw new BadRequestException("");
        };
        return new BroadcastEditResult(editedBroadcast, originalInfo);
    }

    @Transactional
    public BroadcastUpdateResult bulkChangeStatus(BroadcastBulkUpdateStatusCommand command) {
        return switch (command.updateStatus()) {
            case READY -> bulkUpdateToReady(command);
            case END -> bulkUpdateToEnd(command);
            case CANCEL -> bulkUpdateToCancel(command);
            default -> throw new BadRequestException("");
        };
    }

    private BroadcastUpdateResult bulkUpdateToReady(BroadcastBulkUpdateStatusCommand command) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToReady(command);
        for (Long videoKey : result.videoKeys()) {
            fflService.sendChangeBroadcastStatusMessage(videoKey, READY);
        }
        return result;
    }

    private Broadcast toLive(Broadcast broadcast) {
        ExternalBroadcastInfo info = fflService.startFFLVideoRoomAndSendChatMessage(broadcast);
        broadcast.start(info.liveUrl(), info.lastModifiedAt());
        return broadcast;
    }

    private BroadcastUpdateResult bulkUpdateToEnd(BroadcastBulkUpdateStatusCommand command) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToFinish(command);
        for (Long videoKey : result.videoKeys()) {
            try {
                fflService.endVideoRoomAndSendChatMessage(videoKey);
            } catch (FFLException e) {
                log.error("Fail To Change Status To End. Video Key : {}", videoKey, e);
            }
        }
        return result;
    }

    private Broadcast toEnd(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.endVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }

    private BroadcastUpdateResult bulkUpdateToCancel(BroadcastBulkUpdateStatusCommand condition) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToFinish(condition);
        for (Long videoKey : result.videoKeys()) {
            try {
                fflService.cancelVideoRoomAndSendChatMessage(videoKey);
            } catch (FFLException e) {
                log.error("Fail To Change Status To Cancel. Video Key : {}", videoKey, e);
            }
        }
        return result;
    }

    private Broadcast toCancel(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.cancelVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }
}
