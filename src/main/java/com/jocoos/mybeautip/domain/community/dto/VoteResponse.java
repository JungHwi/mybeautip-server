package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
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
}
