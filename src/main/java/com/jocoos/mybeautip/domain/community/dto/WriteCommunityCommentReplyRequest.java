package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriteCommunityCommentReplyRequest {

    private Long communityId;

    private Long commentId;

    private String contents;
}
