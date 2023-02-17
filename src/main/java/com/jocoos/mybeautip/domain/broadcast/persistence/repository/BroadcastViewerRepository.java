package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.viewer.BroadcastViewerCustomRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BroadcastViewerRepository extends ExtendedQuerydslJpaRepository<BroadcastViewer, Long>, BroadcastViewerCustomRepository {

    Optional<BroadcastViewer> findByBroadcastIdAndMemberId(long broadcastId, long memberId);

    boolean existsByBroadcastIdAndMemberIdAndType(long broadcastId, long memberId, BroadcastViewerType type);



}
