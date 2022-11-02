package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberActivityCountRepository extends DefaultJpaRepository<MemberActivityCount, Long> {


    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.communityCount = activityCount.communityCount + :count WHERE activityCount.id = :memberId")
    void updateCommunityCount(@Param("memberId") long memberId, @Param("count") int count);

    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.communityCommentCount = activityCount.communityCommentCount + :count WHERE activityCount.id = :memberId")
    void updateCommunityCommentCount(@Param("memberId") long memberId, @Param("count") int count);

    @Modifying
    @Query("UPDATE MemberActivityCount activityCount SET activityCount.videoCommentCount = activityCount.videoCommentCount + :count WHERE activityCount.id = :memberId")
    void updateVideoCommentCount(@Param("memberId") long memberId, @Param("count") int count);
}
