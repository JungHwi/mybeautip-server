package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.global.vo.Between;
import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface MemberCustomRepository {
    Map<MemberStatus, Long> getStatusesWithCount();

    MemberSearchResult getMemberWithDetails(Long memberId);

    Page<MemberBasicSearchResult> getMembers(MemberSearchCondition condition);

    List<Member> getMemberLastLoggedAtSameDayIn(List<Between> lastLoggedAt);
}
