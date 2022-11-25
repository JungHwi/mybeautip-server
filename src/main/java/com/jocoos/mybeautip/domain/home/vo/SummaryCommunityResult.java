package com.jocoos.mybeautip.domain.home.vo;

import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;
import static java.util.Collections.emptyList;

@Getter
public class SummaryCommunityResult {

    private final Community community;
    private final String eventTitle;
    private final MemberResponse memberResponse;
    private List<String> thumbnailUrl;
    private List<VoteResponse> voteResponses;

    @QueryProjection
    public SummaryCommunityResult(Community community, MemberResponse memberResponse) {
        this.community = community;
        this.memberResponse = memberResponse;
        this.thumbnailUrl = new ArrayList<>();
        this.eventTitle = null;
    }

    @QueryProjection
    public SummaryCommunityResult(Community community, MemberResponse memberResponse, String eventTitle) {
        this.community = community;
        this.memberResponse = memberResponse;
        this.eventTitle = eventTitle;
        this.thumbnailUrl = new ArrayList<>();
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
