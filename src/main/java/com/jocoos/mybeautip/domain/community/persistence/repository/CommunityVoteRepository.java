package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityVoteRepository extends DefaultJpaRepository<CommunityVote, Long> {

    List<CommunityVote> findByCommunityIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE CommunityVote communityVote SET communityVote.voteCount = communityVote.voteCount + :count WHERE communityVote.id = :communityVoteId")
    void voteCount(@Param("communityVoteId") long communityVoteId, @Param("count") int count);

}
