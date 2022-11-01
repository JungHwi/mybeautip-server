package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;

import java.util.Map;

public interface MemberCustomRepository {
    Map<MemberStatus, Long> getStatusesWithCount();

    MemberSearchResult getMemberWithDetails(Long memberId);
}
