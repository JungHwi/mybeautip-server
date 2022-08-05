package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class WriteCommunityCommentRequest {

    private Long communityId;

    private Long parentId;

    private String contents;

}
