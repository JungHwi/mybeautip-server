package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityVoteMemberResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVoteMember;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityVoteDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityVoteMemberDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.jocoos.mybeautip.global.exception.ErrorCode.COMMUNITY_VOTE_NOT_MATCH;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommunityVoteService {

    private final CommunityVoteMemberDao communityVoteMemberDao;
    private final CommunityVoteDao communityVoteDao;
    private final CommunityDao communityDao;

    @Transactional
    public CommunityVoteMemberResponse vote(Member member, Long communityId, Long voteId) {
        CommunityVote communityVote = communityVoteDao.get(voteId);
        Community community = communityDao.get(communityId);
        valid(communityVote, community, member);

        CommunityVoteMember voteMember = new CommunityVoteMember(communityVote, community, member);
        communityVoteMemberDao.save(voteMember);

        communityVoteDao.increaseVoteCount(voteId);

        Community updatedCommunity = communityDao.getUpdated(communityId);
        return CommunityVoteMemberResponse.from(updatedCommunity, voteMember.getCommunityVoteId());
    }

    private void valid(CommunityVote vote, Community community, Member member) {
        validMatchVoteAndCommunity(vote, community);
        validDuplicateVote(community, member);
    }

    private void validDuplicateVote(Community community, Member member) {
        if (communityVoteMemberDao.isExist(community, member)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VOTE.getDescription());
        }
    }

    private void validMatchVoteAndCommunity(CommunityVote vote, Community community) {
        if (!Objects.equals(community, vote.getCommunity())) {
            throw new BadRequestException(COMMUNITY_VOTE_NOT_MATCH.getDescription());
        }
    }
}
