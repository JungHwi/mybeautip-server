package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcastViewerRepository extends DefaultJpaRepository<BroadcastViewer, Long> {

    boolean existsByBroadcastIdAndMemberIdAndType(long broadcastId, long memberId, BroadcastViewerType type);
}
