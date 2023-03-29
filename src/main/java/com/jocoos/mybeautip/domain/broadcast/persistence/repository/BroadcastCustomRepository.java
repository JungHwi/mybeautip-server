package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastBulkUpdateStatusCommand;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastUpdateResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BroadcastCustomRepository {

    long count(BroadcastSearchCondition condition);

    Page<BroadcastSearchResult> getPage(BroadcastSearchCondition condition);
    List<BroadcastSearchResult> getList(BroadcastSearchCondition condition);
    Optional<BroadcastSearchResult> get(long broadcastId);

    BroadcastUpdateResult bulkUpdateStatusAndEndedAt(BroadcastBulkUpdateStatusCommand condition);

    BroadcastUpdateResult bulkUpdateStatus(BroadcastBulkUpdateStatusCommand condition);
}
