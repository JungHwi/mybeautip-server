package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberActivityCountRepository extends ExtendedQuerydslJpaRepository<MemberActivityCount, Long>, MemberActivityCountCustomRepository {


    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.communityCount = activityCount.communityCount + :count WHERE activityCount.id = :memberId")
    void updateNormalCommunityCount(@Param("memberId") long memberId, @Param("count") int count);

    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.communityCommentCount = activityCount.communityCommentCount + :count WHERE activityCount.id in :memberIds")
    void updateNormalCommunityCommentCount(@Param("memberIds") List<Long> memberIds, @Param("count") int count);

    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.videoCommentCount = activityCount.videoCommentCount + :count WHERE activityCount.id in :memberIds")
    void updateNormalVideoCommentCount(@Param("memberIds") List<Long> memberIds, @Param("count") int count);
}
