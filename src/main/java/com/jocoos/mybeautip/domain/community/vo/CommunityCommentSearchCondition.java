package com.jocoos.mybeautip.domain.community.vo;

public record CommunityCommentSearchCondition(long communityId, Long parentId, Long memberId, long cursor) {
}
