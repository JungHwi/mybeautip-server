package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import org.springframework.data.domain.Page;

public interface BroadcastCustomRepository {

    Page<BroadcastSearchResult> getList(BroadcastSearchCondition condition);

    BroadcastSearchResult get(long broadcastId);
}
