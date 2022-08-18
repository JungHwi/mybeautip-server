package com.jocoos.mybeautip.domain.community.dto;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EditCommunityCommentRequest {

    private Long communityId;

    private Long commentId;

    private String contents;

    private Member member;
}
