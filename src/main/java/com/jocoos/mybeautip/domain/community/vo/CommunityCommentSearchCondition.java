package com.jocoos.mybeautip.domain.community.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommunityCommentSearchCondition {
    private final long communityId;
    private final Long parentId;
    private final long cursor;
}
