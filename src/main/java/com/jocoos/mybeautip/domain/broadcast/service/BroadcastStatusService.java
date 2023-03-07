package com.jocoos.mybeautip.domain.broadcast.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.service.dao.VodDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.code.UrlDirectory.VOD;

@RequiredArgsConstructor
@Service
public class BroadcastStatusService {

    private final FlipFlopLiteService flipFlopLiteService;
    private final VodDao vodDao;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public void changeStatus(Broadcast broadcast, BroadcastStatus changeStatus) {
        switch (changeStatus) {
            case LIVE -> toLive(broadcast);
            case END, CANCEL -> toFinish(broadcast, changeStatus);
            case READY -> toReady(broadcast);
            default -> throw new BadRequestException("");
        }
    }

    private void toReady(Broadcast broadcast) {
        broadcast.ready();
    }

    private Broadcast toLive(Broadcast broadcast) {
        ExternalBroadcastInfo info = flipFlopLiteService.startVideoRoom(broadcast.getVideoKey());
        broadcast.start(info.liveUrl(), info.lastModifiedAt());
        return broadcast;
    }

    private Broadcast toFinish(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = flipFlopLiteService.endVideoRoom(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        copyThumbnailToVod(broadcast);
        return broadcast;
    }

    private void copyThumbnailToVod(Broadcast broadcast) {
        Vod vod = vodDao.getByVideoKey(broadcast.getVideoKey());
        awsS3Handler.copyWithKeepOriginal(broadcast.getThumbnailUrl(), VOD.getDirectory(vod.getId()));
    }
}
