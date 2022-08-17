package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Builder
@Getter
public class SearchCommentRequest {
    long communityId;

    Long parentId;

    long cursor;

    Pageable pageable;
}
