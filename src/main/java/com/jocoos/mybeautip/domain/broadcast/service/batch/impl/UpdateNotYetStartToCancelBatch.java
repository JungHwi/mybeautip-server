package com.jocoos.mybeautip.domain.broadcast.service.batch.impl;

import com.jocoos.mybeautip.domain.broadcast.service.batch.BroadcastBatchUseCase;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastStatusService;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidate;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.CANCEL;
import static com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition.updateNotYetStartToCancel;

@RequiredArgsConstructor
@Component
public class UpdateNotYetStartToCancelBatch implements BroadcastBatchUseCase {

    private final BroadcastStatusService statusService;

    @Transactional
    @Override
    public List<BroadcastUpdateResult> batchUpdate() {
        BroadcastUpdateCandidateCondition condition = updateNotYetStartToCancel(ZonedDateTime.now().minusMinutes(5));
        List<BroadcastUpdateCandidate> candidates = statusService.getUpdateCandidates(condition);
        return List.of(statusService.bulkChangeStatus(CANCEL, candidates));
    }
}
