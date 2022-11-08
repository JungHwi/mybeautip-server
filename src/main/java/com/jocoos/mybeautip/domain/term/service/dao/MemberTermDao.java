package com.jocoos.mybeautip.domain.term.service.dao;

import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import com.jocoos.mybeautip.domain.term.persistence.repository.MemberTermRepository;
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.term.code.TermType.MARKETING_INFO;

@RequiredArgsConstructor
@Service
public class MemberTermDao {

    private final MemberTermRepository memberTermRepository;
    private final TermRepository termRepository;

    @Transactional(readOnly = true)
    public boolean isAgreeOnMarketingTerm(Long memberId) {
        long marketingTermId = getMarketingTermId();

        return memberTermRepository.findByTermIdAndMemberId(marketingTermId, memberId)
                .map(MemberTerm::getIsAccept)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Map<Long, Boolean> isAgreeMarketingTerm(List<Long> memberIds) {
        long marketingTermId = getMarketingTermId();
        List<MemberTerm> membTerms = memberTermRepository.findByTermIdAndMemberIdIn(marketingTermId, memberIds);
        return membTerms.stream()
                .collect(Collectors.toMap(m -> m.getMember().getId(), MemberTerm::getIsAccept));
    }

    private long getMarketingTermId() {
        return termRepository.findByType(MARKETING_INFO)
                .orElseThrow(() -> new NotFoundException("marketing term not found"))
                .getId();
    }
}
