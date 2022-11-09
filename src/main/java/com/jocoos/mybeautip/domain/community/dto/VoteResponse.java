package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;


@Builder
@AllArgsConstructor
@Getter
public class VoteResponse {
    private Long id;
    private String fileUrl;
    private Integer count;
    private Boolean isVoted;

    public void setCountZero() {
        this.count = 0;
    }

    public void userVoted(Boolean isVoted) {
        this.isVoted = isVoted;
    }

    @QueryProjection
    public VoteResponse(Long id, Long communityId, String filename) {
        this.id = id;
        this.fileUrl = toUrl(filename, COMMUNITY, communityId);
        this.count = 0;
        this.isVoted = false;
    }

    public static VoteResponse from(CommunityVote vote) {
        return new VoteResponse(vote.getId(), vote.getCommunity().getId(), vote.getCommunityFile().getFile());
    }
}
