package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;
import java.util.Set;

public interface InfluencerRepository extends DefaultJpaRepository<Influencer, Long> {

    boolean existsByIdAndStatus(long memberId, InfluencerStatus status);

    List<Influencer> findAllByIdIn(Set<Long> ids);
}
