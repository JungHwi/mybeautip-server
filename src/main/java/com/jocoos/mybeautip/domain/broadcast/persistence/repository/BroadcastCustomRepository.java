package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;

import java.util.List;

public interface BroadcastCustomRepository {

    List<BroadcastSearchResult> getList(BroadcastSearchCondition condition);
}
