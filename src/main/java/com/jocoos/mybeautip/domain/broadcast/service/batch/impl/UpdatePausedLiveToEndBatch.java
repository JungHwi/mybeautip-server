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

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.END;
import static com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition.updatePausedLiveToEnd;

@RequiredArgsConstructor
@Component
public class UpdatePausedLiveToEndBatch implements BroadcastBatchUseCase {

    private final BroadcastStatusService statusService;

    @Transactional
    @Override
    public List<BroadcastUpdateResult> batchUpdate() {
        BroadcastUpdateCandidateCondition condition = updatePausedLiveToEnd(ZonedDateTime.now().minusMinutes(1));
        List<BroadcastUpdateCandidate> candidates = statusService.getUpdateCandidates(condition);
        return List.of(statusService.bulkChangeStatus(END, candidates));
    }
}
