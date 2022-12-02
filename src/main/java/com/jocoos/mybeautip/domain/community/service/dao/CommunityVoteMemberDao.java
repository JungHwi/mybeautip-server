package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVoteMember;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityVoteMemberRepository;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommunityVoteMemberDao {

    private final CommunityVoteMemberRepository repository;

    @Transactional
    public CommunityVoteMember save(CommunityVoteMember communityVoteMember) {
        return repository.save(communityVoteMember);
    }

    @Transactional(readOnly = true)
    public Optional<CommunityVoteMember> findByCommunityIdAndMember(Long communityId, Member member) {
        return repository.findByCommunityIdAndMember(communityId, member);
    }

    @Transactional(readOnly = true)
    public boolean isExist(Community community, Member member) {
        return repository.existsByCommunityAndMember(community, member);
    }

    @Transactional(readOnly = true)
    public Long getUserVotedId(Long communityId, Member member) {
        return repository
                .findByCommunityIdAndMember(communityId, member)
                .map(CommunityVoteMember::getCommunityVoteId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getUserVotedIds(List<Long> communityIds, Member member) {
        List<CommunityVoteMember> communityVoteMemberList = repository.findByCommunityIdInAndMember(communityIds, member);

        return communityVoteMemberList.stream()
                .collect(Collectors.toMap(vote -> vote.getCommunity().getId(), CommunityVoteMember::getId));
    }
}
