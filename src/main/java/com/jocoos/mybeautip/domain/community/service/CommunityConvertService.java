package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityVoteMemberDao;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;

@RequiredArgsConstructor
@Service
public class CommunityConvertService {

    private final CommunityConverter converter;
    private final CommunityRelationService relationService;
    private final CommunityVoteMemberDao communityVoteMemberDao;

    @Transactional(readOnly = true)
    public List<CommunityResponse> toResponse(Member member, List<Community> communities) {
        List<CommunityResponse> responses = converter.convert(communities);
        setUserVoted(member, responses);
        return relationService.setRelationInfo(responses);
    }

    @Transactional(readOnly = true)
    public CommunityResponse toResponse(Member member, Community community) {
        CommunityResponse response = converter.convert(community);
        setUserVoted(member, response);
        return relationService.setRelationInfo(response);
    }

    @Transactional(readOnly = true)
    public List<MyCommunityResponse> toMyCommunityResponse(List<Community> communityList) {
        return converter.convertToMyCommunity(communityList);
    }

    private void setUserVoted(Member member, List<CommunityResponse> responses) {
        List<Long> voteCommunityIds = responses.stream()
                .filter(response -> response.isCategoryType(VOTE))
                .map(CommunityResponse::getId)
                .toList();

        Map<Long, Long>  voteIdMap = communityVoteMemberDao.getUserVotedIds(voteCommunityIds, member);
        for (CommunityResponse communityResponse : responses) {
            Long voteId = voteIdMap.get(communityResponse.getId());
            communityResponse.userVote(voteId);
        }
    }

    private void setUserVoted(Member member, CommunityResponse response) {
        if (response.isCategoryType(VOTE)) {
            Long userVotedId = communityVoteMemberDao.getUserVotedId(response.getId(), member);
            response.userVote(userVotedId);
        }
    }
}
