package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVoteMember;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import com.jocoos.mybeautip.member.Member;

import java.util.Optional;

public interface CommunityVoteMemberRepository extends DefaultJpaRepository<CommunityVoteMember, Long> {

    Optional<CommunityVoteMember> findByCommunityAndMember(Community community, Member member);

    Optional<CommunityVoteMember> findByCommunityIdAndMember(Long communityId, Member member);

    boolean existsByCommunityAndMember(Community community, Member member);
}
