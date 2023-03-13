package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateStatusCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class BroadcastStatusService {

    private final BroadcastDao broadcastDao;
    private final FlipFlopLiteService flipFlopLiteService;
    private final VodDao vodDao;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public void changeStatus(Broadcast broadcast, BroadcastStatus changeStatus) {
        switch (changeStatus) {
            case LIVE -> toLive(broadcast);
            case END, CANCEL -> toFinish(broadcast, changeStatus);
            default -> throw new BadRequestException("");
        }
    }

    @Transactional
    public long bulkChangeStatus(BroadcastUpdateStatusCondition condition) {
        return switch (condition.updateStatus()) {
            case READY -> broadcastDao.bulkUpdateToReady(condition);
            case CANCEL, END -> bulkUpdateToFinish(condition);
            default -> throw new BadRequestException("");
        };
    }

    private Broadcast toLive(Broadcast broadcast) {
        ExternalBroadcastInfo info = flipFlopLiteService.startVideoRoom(broadcast.getVideoKey());
        broadcast.start(info.liveUrl(), info.lastModifiedAt());
        return broadcast;
    }

    private long bulkUpdateToFinish(BroadcastUpdateStatusCondition condition) {
        BroadcastUpdateResult result = broadcastDao.bulkUpdateToFinish(condition);
        for (Long videoKey : result.videoKeys()) {
            flipFlopLiteService.endVideoRoom(videoKey);
        }
        return result.count();
    }

    private Broadcast toFinish(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = flipFlopLiteService.endVideoRoom(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }
}
