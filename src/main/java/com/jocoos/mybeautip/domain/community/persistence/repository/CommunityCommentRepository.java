package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends DefaultJpaRepository<CommunityComment, Long> {

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
