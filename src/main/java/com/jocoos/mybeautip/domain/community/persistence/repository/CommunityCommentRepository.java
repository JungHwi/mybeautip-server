package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.repository.comment.CommunityCommentCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityCommentRepository extends ExtendedQuerydslJpaRepository<CommunityComment, Long>, CommunityCommentCustomRepository {

    Long countByMemberId(Long memberId);
    Optional<CommunityComment> findByCommunityIdAndId(long communityId, long commentId);
    Slice<CommunityComment> findByMemberIdAndStatusAndIdLessThan(long memberId, CommunityStatus status, long cursor, Pageable pageable);
    Slice<CommunityComment> findByCommunityIdAndParentIdAndIdGreaterThan(long communityId, Long parentId, long cursor, Pageable pageable);
    Slice<CommunityComment> findByCommunityIdAndParentIdAndIdLessThan(long communityId, Long parentId, long cursor, Pageable pageable);
    List<CommunityComment> findByParentIdInOrderByCreatedAtAsc(List<Long> parentIds);

    Page<CommunityComment> findAllByParentIdIsNullAndCommunityId(Long communityId, Pageable pageable);

    @Query("select cc from CommunityComment cc where cc.communityId = :communityId and cc.parentId = :parentId and cc.id > :cursor and (cc.memberId not in :members or cc.categoryId = 2) order by cc.id asc")
    Slice<CommunityComment> getAllByAscParentIdNotNull(@Param("communityId") Long communityId, @Param("parentId") Long parentId, @Param("cursor") Long cursor, @Param("members") List<Long> members, Pageable pageable);

    @Query("select cc from CommunityComment cc where cc.communityId = :communityId and cc.parentId is null and cc.id > :cursor and (cc.memberId not in :members or cc.categoryId = 2) order by cc.id asc")
    Slice<CommunityComment> getAllByAscParentIdNull(@Param("communityId") Long communityId, @Param("cursor") Long cursor, @Param("members") List<Long> members, Pageable pageable);

    @Query("select cc from CommunityComment cc where cc.communityId = :communityId and cc.parentId = :parentId and cc.id < :cursor and (cc.memberId not in :members or cc.categoryId = 2) order by cc.id desc")
    Slice<CommunityComment> getAllByDescParentIdNotNull(@Param("communityId") Long communityId, @Param("parentId") Long parentId, @Param("cursor") Long cursor, @Param("members") List<Long> members, Pageable pageable);

    @Query("select cc from CommunityComment cc where cc.communityId = :communityId and cc.parentId is null and cc.id < :cursor and (cc.memberId not in :members or cc.categoryId = 2) order by cc.id desc")
    Slice<CommunityComment> getAllByDescParentIdNull(@Param("communityId") Long communityId, @Param("cursor") Long cursor, @Param("members") List<Long> members, Pageable pageable);

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
