package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface CommunityRepository extends DefaultJpaRepository<Community, Long> {

    List<Community> findByCategoryInAndSortedAtLessThan(List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable);
    List<Community> findByEventIdAndCategoryInAndSortedAtLessThan(Long EventId, List<CommunityCategory> categoryList, ZonedDateTime cursor, Pageable pageable);

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

}
