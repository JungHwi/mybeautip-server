package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityLikeRepository extends DefaultJpaRepository<CommunityLike, Long> {

    Optional<CommunityLike> findByMemberIdAndCommunityId(long memberId, long communityId);

    boolean existsByMemberIdAndCommunityIdAndIsLikeIsTrue(long memberId, long communityId);

    List<CommunityLike> findByMemberIdAndCommunityIdInAndIsLikeIsTrue(long memberId, List<Long> communityId);

}
