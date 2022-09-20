package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.repository.comment.CommunityCommentCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends ExtendedQuerydslJpaRepository<CommunityComment, Long>, CommunityCommentCustomRepository {

    Optional<CommunityComment> findByCommunityIdAndId(long communityId, long commentId);

    Slice<CommunityComment> findByMemberIdAndStatusAndIdLessThan(long memberId, CommunityStatus status, long cursor, Pageable pageable);
    Slice<CommunityComment> findByCommunityIdAndParentIdAndIdGreaterThan(long communityId, Long parentId, long cursor, Pageable pageable);
    Slice<CommunityComment> findByCommunityIdAndParentIdAndIdLessThan(long communityId, Long parentId, long cursor, Pageable pageable);

    @Modifying
    @Query("UPDATE CommunityComment comment SET comment.likeCount = comment.likeCount + :count WHERE comment.id = :commentId")
    void likeCount(@Param("commentId") long commentId, @Param("count") int count);

    @Modifying
    @Query("UPDATE CommunityComment comment SET comment.reportCount = comment.reportCount + :count WHERE comment.id = :commentId")
    void reportCount(@Param("commentId") long commentId, @Param("count") int count);

    @Modifying
    @Query("UPDATE CommunityComment comment SET comment.commentCount = comment.commentCount + :count WHERE comment.id = :commentId")
    void commentCount(@Param("commentId") long commentId, @Param("count") int count);
}
