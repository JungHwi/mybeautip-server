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
        CommunityVoteMember voteMember = createVoteMember(member, voteId, communityId);

        communityVoteDao.increaseVoteCount(voteId);

        Community updatedCommunity = communityDao.getUpdated(communityId);
        return CommunityVoteMemberResponse.from(updatedCommunity, voteMember.getCommunityVoteId());
    }


    private CommunityVoteMember createVoteMember(Member member, Long voteId, Long communityId) {
        CommunityVote vote = communityVoteDao.get(voteId);
        Community community = communityDao.get(communityId);
        valid(vote, community);
        return createVoteMember(member, vote, community);
    }

    private CommunityVoteMember createVoteMember(Member member, CommunityVote vote, Community community) {
        if (communityVoteMemberDao.isExist(community, member)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VOTE.getDescription());
        } else {
            CommunityVoteMember voteMember = CommunityVoteMember.builder()
                    .community(community)
                    .member(member)
                    .vote(vote)
                    .build();
            return communityVoteMemberDao.save(voteMember);
        }
    }

    private void valid(CommunityVote vote, Community community) {
        if (!Objects.equals(community, vote.getCommunity())) {
            throw new BadRequestException(COMMUNITY_VOTE_NOT_MATCH.getDescription());
        }
    }
}
