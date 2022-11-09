package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.repository.community.CommunityCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends ExtendedQuerydslJpaRepository<Community, Long>, CommunityCustomRepository {
    List<Community> findByIdIn(List<Long> ids);
    Slice<Community> findByMemberIdAndStatusAndIdLessThan(long memberId, CommunityStatus status, long id, Pageable pageable);
    Slice<Community> findByCategoryInAndSortedAtLessThan(List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable);
    Slice<Community> findByEventIdAndCategoryInAndIsWinAndSortedAtLessThan(Long EventId, List<CommunityCategory> categoryList, Boolean isWin, ZonedDateTime cursor, Pageable pageable);

    Optional<Community> findByIsTopFixIsTrue();
    Long countByMemberId(Long memberId);

    @Modifying
    @Query("UPDATE Community community SET community.viewCount = community.viewCount + 1 WHERE community.id = :communityId")
    void readCount(@Param("communityId") long communityId);

    @Modifying
    @Query("UPDATE Community community SET community.likeCount = community.likeCount + :count WHERE community.id = :communityId")
    void likeCount(@Param("communityId") long communityId, @Param("count") int count);

    @Modifying
    @Query("UPDATE Community community SET community.reportCount = community.reportCount + :count WHERE community.id = :communityId")
    void reportCount(@Param("communityId") long communityId, @Param("count") int count);

    @Modifying
    @Query("UPDATE Community community SET community.commentCount = community.commentCount + :count WHERE community.id = :communityId")
    void commentCount(@Param("communityId") long communityId, @Param("count") int count);

    @Modifying
    @Query("UPDATE Community community SET community.sortedAt = :now WHERE community.id = :communityId")
    void updateSortedAt(@Param("communityId") long communityId, @Param("now") ZonedDateTime now);

}
