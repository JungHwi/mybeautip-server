package com.jocoos.mybeautip.domain.community.vo;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityRelationInfo {
    private Boolean isLike;
    private Boolean isBlock;
    private Boolean isReport;
}
