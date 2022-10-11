package com.jocoos.mybeautip.domain.community.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommunityRelationInfo {
    private Boolean isLike;
    private Boolean isReport;
    private Boolean isBlock;
    private Boolean isScrap;

    public CommunityRelationInfo() {
        this.isLike = false;
        this.isReport = false;
        this.isBlock = false;
        this.isScrap = false;
    }
}
