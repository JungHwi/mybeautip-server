package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityCommentLikeRepository extends DefaultJpaRepository<CommunityCommentLike, Long> {

    Optional<CommunityCommentLike> findByMemberIdAndCommentId(long memberId, long commentId);

    boolean existsByMemberIdAndCommentIdAndIsLikeIsTrue(long memberId, long commentId);

    List<CommunityCommentLike> findByMemberIdAndCommentIdInAndIsLikeIsTrue(long memberId, List<Long> commentIds);

}
