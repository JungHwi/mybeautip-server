package com.jocoos.mybeautip.domain.broadcast.service.child;

import com.jocoos.mybeautip.client.flipfloplite.dto.ExternalBroadcastInfo;
import com.jocoos.mybeautip.client.flipfloplite.exception.FFLException;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastEditResult.OriginalInfo;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidate;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.*;

// FIXME: All Status Switch Case To Class
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
            case READY, SCHEDULED -> throw new BadRequestException("");
        };
        return new BroadcastEditResult(editedBroadcast, originalInfo);
    }

    @Transactional
    public BroadcastUpdateResult bulkChangeStatus(BroadcastStatus status, List<BroadcastUpdateCandidate> candidates) {
        return switch (status) {
            case READY -> bulkUpdateToReady(candidates);
            case END -> bulkUpdateToEnd(candidates);
            case CANCEL -> bulkUpdateToCancel(candidates);
            case LIVE, SCHEDULED -> throw new BadRequestException("");
        };
    }

    @Transactional(readOnly = true)
    public List<BroadcastUpdateCandidate> getUpdateCandidates(BroadcastUpdateCandidateCondition condition) {
        return broadcastDao.getCandidates(condition);
    }

    public void forceFinishAll(List<Broadcast> broadcasts) {
        for (Broadcast broadcast : broadcasts) {
            switch (broadcast.getStatus()) {
                case SCHEDULED, READY -> forceFinish(broadcast, CANCEL);
                case LIVE -> forceFinish(broadcast, END);
                case END,CANCEL -> {}
            }
        }
    }

    private void forceFinish(Broadcast broadcast, BroadcastStatus status) {
        fflService.sendForceFinishDirectMessage(broadcast.getVideoKey(), broadcast.getMemberId(), status);
    }

    private BroadcastUpdateResult bulkUpdateToReady(List<BroadcastUpdateCandidate> candidates) {
        return bulkUpdate(candidates,
                READY,
                broadcastDao::bulkUpdateToReady,
                videoKey -> fflService.sendChangeBroadcastStatusMessage(videoKey, READY));
    }

    private Broadcast toLive(Broadcast broadcast) {
        if (broadcast.isStatusEq(LIVE)) {
            throw new BadRequestException("Already Live. Broadcast Id " + broadcast.getId());
        }
        ExternalBroadcastInfo info = fflService.startFFLVideoRoomAndSendChatMessage(broadcast);
        broadcast.start(info.liveUrl(), info.lastModifiedAt());
        return broadcast;
    }

    private BroadcastUpdateResult bulkUpdateToEnd(List<BroadcastUpdateCandidate> candidates) {
        return bulkUpdate(candidates,
                END,
                broadcastDao::bulkUpdateToEnd,
                fflService::endVideoRoomAndSendChatMessage);
    }

    private Broadcast toEnd(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.endVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }

    private BroadcastUpdateResult bulkUpdateToCancel(List<BroadcastUpdateCandidate> candidates) {
        return bulkUpdate(candidates,
                CANCEL,
                broadcastDao::bulkUpdateToCancel,
                fflService::cancelVideoRoomAndSendChatMessage);
    }

    private Broadcast toCancel(Broadcast broadcast, BroadcastStatus changeStatus) {
        ZonedDateTime endedAt = fflService.cancelVideoRoomAndSendChatMessage(broadcast.getVideoKey());
        broadcast.finish(changeStatus, endedAt);
        return broadcast;
    }

    private BroadcastUpdateResult bulkUpdate(List<BroadcastUpdateCandidate> candidates,
                                                   BroadcastStatus toStatus,
                                                   Consumer<List<Long>> bulkStatusChangeFunc,
                                                   LongConsumer fflStatusChangeFunc) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failIds = new ArrayList<>();

        for (BroadcastUpdateCandidate candidate : candidates) {
            Long id = candidate.id();
            try {
                fflStatusChangeFunc.accept(candidate.videoKey());
                successIds.add(id);
            } catch (FFLException e) {
                log.error("Fail To Change Status To {} ID : {}", toStatus, id, e);
                failIds.add(id);
            }
        }

        bulkStatusChangeFunc.accept(successIds);
        return new BroadcastUpdateResult(toStatus, successIds, failIds);
    }
}
