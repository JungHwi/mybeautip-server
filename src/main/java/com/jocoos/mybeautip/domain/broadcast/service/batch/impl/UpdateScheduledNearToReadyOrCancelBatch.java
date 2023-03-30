package com.jocoos.mybeautip.domain.broadcast.service.batch.impl;

import com.jocoos.mybeautip.domain.broadcast.service.batch.BroadcastBatchUseCase;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastStatusService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidate;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import com.jocoos.mybeautip.global.util.EntityUtil;
import com.jocoos.mybeautip.global.vo.Between;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.READY;

@RequiredArgsConstructor
@Component
public class UpdateScheduledNearToReadyOrCancelBatch implements BroadcastBatchUseCase {

    private final BroadcastStatusService statusService;
    private final BroadcastDao broadcastDao;

    @Override
    public List<BroadcastUpdateResult> batchUpdate() {
        List<BroadcastUpdateCandidate> candidates = statusService.getUpdateCandidates(updateScheduledNearToReady());
        Set<Long> liveCreators = getLiveCreatorIds(candidates);
        BroadcastUpdateResult toReady = toReady(candidates, liveCreators);
        BroadcastUpdateResult toCancel = toCancel(candidates, liveCreators);
        return List.of(toReady, toCancel);
    }

    private BroadcastUpdateResult toReady(List<BroadcastUpdateCandidate> candidates, Set<Long> liveCreators) {
        List<BroadcastUpdateCandidate> needToReady = candidates.stream()
                .filter(candidate -> !liveCreators.contains(candidate.memberId()))
                .toList();
        return statusService.bulkChangeStatus(READY, needToReady);
    }

    private BroadcastUpdateResult toCancel(List<BroadcastUpdateCandidate> candidates, Set<Long> liveCreators) {
        List<BroadcastUpdateCandidate> needToCancel = candidates.stream()
                .filter(candidate -> liveCreators.contains(candidate.memberId()))
                .toList();
        return statusService.bulkChangeStatus(CANCEL, needToCancel);
    }

    private BroadcastUpdateCandidateCondition updateScheduledNearToReady() {
        Between between5MinutesFromNow = new Between(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(5));
        return BroadcastUpdateCandidateCondition.updateScheduledNearToReady(between5MinutesFromNow);
    }

    private Set<Long> getLiveCreatorIds(List<BroadcastUpdateCandidate> candidates) {
        Set<Long> memberIds = EntityUtil.extractLongSet(candidates, BroadcastUpdateCandidate::memberId);
        return broadcastDao.getCreatorIdsLiveNowIn(memberIds);
    }
}
