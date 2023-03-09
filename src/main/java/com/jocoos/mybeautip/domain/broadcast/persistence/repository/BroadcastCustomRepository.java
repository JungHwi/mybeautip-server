package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BroadcastCustomRepository {

    Page<BroadcastSearchResult> getPageList(BroadcastSearchCondition condition);
    List<BroadcastSearchResult> getList(BroadcastSearchCondition condition);
    Optional<BroadcastSearchResult> get(long broadcastId);
}
