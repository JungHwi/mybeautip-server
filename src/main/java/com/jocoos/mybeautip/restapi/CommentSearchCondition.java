package com.jocoos.mybeautip.restapi;

import lombok.Builder;

@Builder
public record CommentSearchCondition(long videoId,
                                     Long cursor,
                                     Long parentId,
                                     Long memberId,
                                     String lang) {
}
