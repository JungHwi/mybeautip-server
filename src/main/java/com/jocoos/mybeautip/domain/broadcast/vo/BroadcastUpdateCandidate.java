package com.jocoos.mybeautip.domain.broadcast.vo;

import com.querydsl.core.annotations.QueryProjection;

public record BroadcastUpdateCandidate(Long id,
                                       Long videoKey,
                                       Long memberId) {

    @QueryProjection
    public BroadcastUpdateCandidate(Long id,
                                    Long videoKey,
                                    Long memberId) {
        this.id = id;
        this.videoKey = videoKey;
        this.memberId = memberId;
    }
}
