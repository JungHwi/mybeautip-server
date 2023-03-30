package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidate;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateCandidateCondition;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BroadcastCustomRepository {

    long count(BroadcastSearchCondition condition);

    Page<BroadcastSearchResult> getPageList(BroadcastSearchCondition condition);
    List<BroadcastSearchResult> getList(BroadcastSearchCondition condition);
    Optional<BroadcastSearchResult> get(long broadcastId);

    void bulkUpdateStatusAndEndedAt(Collection<Long> ids, BroadcastStatus status, ZonedDateTime endedAt);

    void bulkUpdateStatus(Collection<Long> ids, BroadcastStatus status);

    List<BroadcastUpdateCandidate> getUpdateCandidates(BroadcastUpdateCandidateCondition condition);
}
