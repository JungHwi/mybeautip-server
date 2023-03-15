package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.viewer.BroadcastViewerCustomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastViewerRepository extends ExtendedQuerydslJpaRepository<BroadcastViewer, Long>, BroadcastViewerCustomRepository {

    Optional<BroadcastViewer> findByBroadcastIdAndMemberId(long broadcastId, long memberId);

    Optional<BroadcastViewer> findByBroadcastIdAndMemberIdAndType(Long broadcastId, Long memberId, BroadcastViewerType type);

    boolean existsByBroadcastIdAndMemberIdAndType(long broadcastId, long memberId, BroadcastViewerType type);

    List<BroadcastViewer> findByBroadcast(Broadcast broadcast);

}
