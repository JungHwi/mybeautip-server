package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@AllArgsConstructor
@Getter
public class CommunityVoteMemberResponse {
    private List<VoteResponse> votes;

    public static CommunityVoteMemberResponse from(Community updatedCommunity, Long userVoted) {
        List<VoteResponse> votes = updatedCommunity.getCommunityVoteList().stream()
                .map(vote -> VoteResponse.builder()
                        .id(vote.getId())
                        .fileUrl(toUrl(vote.getCommunityFile().getFile(), COMMUNITY, vote.getCommunity().getId()))
                        .count(vote.getVoteCount())
                        .isVoted(Objects.equals(vote.getId(), userVoted))
                        .build())
                .collect(Collectors.toList());
        return new CommunityVoteMemberResponse(votes);
    }
}
