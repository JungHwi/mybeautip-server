package com.jocoos.mybeautip.domain.home.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SummaryResult {
    private final Community community;
    private final String eventTitle;

    @QueryProjection
    public SummaryResult(Community community, String eventTitle) {
        this.community = community;
        this.eventTitle = eventTitle;
    }

    @QueryProjection
    public SummaryResult(Community community) {
        this.community = community;
        this.eventTitle = null;
    }
}
