package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import lombok.Builder;

@Builder
public record CommentSearchCondition(long videoId,
                                     CommentState state,
                                     Long cursor,
                                     Long parentId,
                                     Long memberId,
                                     String lang) {
}
