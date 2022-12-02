package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.member.Member;

public interface MemberActivityCountCustomRepository {
    void updateAllAndNormalCommunityCount(Member member, int count);
    void updateAllAndNormalCommunityCommentCount(Member member, int count);
    void updateAllAndNormalVideoCommentCount(Member member, int count);
}
