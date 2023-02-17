package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BroadcastRepository extends DefaultJpaRepository<Broadcast, Long> {

    boolean existsByIdAndMemberId(long broadcastId, long memberId);

}
