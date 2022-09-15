package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VoteResponse {
    private Long id;
    private String fileUrl;
    private Integer count;

    public void setCountNull() {
        this.count = null;
    }
}
