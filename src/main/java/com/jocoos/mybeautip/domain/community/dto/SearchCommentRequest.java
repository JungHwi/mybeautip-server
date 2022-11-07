package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Builder
@Getter
public class SearchCommentRequest {
    long communityId;

    Long parentId;

    @Setter
    Long memberId;

    long cursor;

    Pageable pageable;
}
