package com.jocoos.mybeautip.domain.home.vo;

import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static java.util.Collections.emptyList;

@Getter
public class SummaryResult {

    private final CommunityMemberResponse memberResponse;

    private final Community community;
    private final String eventTitle;
    private List<VoteResponse> voteResponses;
    private List<String> thumbnailUrl;

    @QueryProjection
    public SummaryResult(Community community) {
        this.community = community;
        this.eventTitle = null;
        this.memberResponse = null;
    }

    @QueryProjection
    public SummaryResult(Community community, CommunityMemberResponse memberResponse) {
        this.community = community;
        this.memberResponse = memberResponse;
        this.eventTitle = null;
    }

    @QueryProjection
    public SummaryResult(Community community, CommunityMemberResponse memberResponse, String eventTitle) {
        this.community = community;
        this.memberResponse = memberResponse;
        this.eventTitle = eventTitle;
    }

    public void setVoteResponses(Map<Long, List<VoteResponse>> votesMap) {
        this.voteResponses = votesMap.get(this.community.getId());
    }

    public void setThumbnailUrl(Map<Long, List<CommunityFile>> fileMap) {
        List<CommunityFile> files = fileMap.get(this.community.getId());
        thumbnailUrl = getThumbnailUrlFrom(files);
    }

    private List<String> getThumbnailUrlFrom(List<CommunityFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return emptyList();
        }
        CommunityFile thumbnail = files.get(0);
        return Collections.singletonList(toUrl(thumbnail.getFile(), COMMUNITY, thumbnail.getCommunity().getId()));
    }
}
