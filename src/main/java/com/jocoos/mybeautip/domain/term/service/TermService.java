package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.converter.MemberTermConverter;
import com.jocoos.mybeautip.domain.term.converter.TermConverter;
import com.jocoos.mybeautip.domain.term.dto.MemberTermRequest;
import com.jocoos.mybeautip.domain.term.dto.MemberTermResponse;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import com.jocoos.mybeautip.domain.term.persistence.repository.MemberTermRepository;
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.term.code.TermStatus.DELETE;

@RequiredArgsConstructor
@Service
public class TermService {

    private final TermRepository termRepository;
    private final LegacyMemberService legacyMemberService;
    private final MemberTermRepository memberTermRepository;
    private final MemberTermConverter memberTermConverter;
    private final TermConverter termConverter;

    @Transactional(readOnly = true)
    public List<TermResponse> getTermsUsedIn(TermUsedInType usedIn) {
        List<Term> terms = termRepository.findAllByUsedInTypeAndCurrentTermStatusNot(usedIn, DELETE);
        return termConverter.convertToListResponse(terms);
    }

    @Transactional(readOnly = true)
    public TermDetailResponse getTerm(long termId) {
        return termConverter.convertToResponse(getTermWithValid(termId));
    }

    // TODO chooseTerms 로직이 스레드 세이프한지 체크필요
    @Transactional
    public List<MemberTermResponse> chooseTerms(List<MemberTermRequest> requests) {

        List<Term> terms = termRepository.findAllByIdIn(getTermIds(requests));
        valid(requests, terms);

        List<MemberTerm> memberTerms = createMemberTerms(requests, terms);
        // TODO JPA 벌크 쿼리 설정
        return memberTermConverter.convertToListResponse(memberTermRepository.saveAll(memberTerms));

    }

    @Transactional
    public MemberTermResponse changeOptionalTermStatus(long termId) {
        MemberTerm memberTerm = getMemberTermWithValid(termId);
        memberTerm.changeAcceptStatus();
        return memberTermConverter.convertToResponse(memberTerm);
    }


    private void valid(List<MemberTermRequest> requests, List<Term> terms) {
        if (terms.size() != requests.size())
            throw new NotFoundException("terms not found. check the ids");
    }

    private List<MemberTerm> createMemberTerms(List<MemberTermRequest> requests, List<Term> terms) {
        List<MemberTerm> memberTerms = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            Term term = terms.get(i);
            memberTerms.add(MemberTerm.builder()
                    .term(term)
                    .isAccept(requests.get(i).getIsAccept())
                    .version(term.getVersion())
                    .build());
        }
        return memberTerms;
    }

    private List<Long> getTermIds(List<MemberTermRequest> memberTerms) {
        return memberTerms.stream().map(MemberTermRequest::getTermId).collect(Collectors.toList());
    }

    private Term getTermWithValid(long termId) {
        return termRepository.findById(termId).orElseThrow(
                () -> new NotFoundException("term not found, termId : " + termId)
        );
    }

    private MemberTerm getMemberTermWithValid(long termId) {
        Long memberId = legacyMemberService.currentMemberId();
        return memberTermRepository.findByTermIdAndMemberId(termId, memberId).orElseThrow(
                () -> new NotFoundException("member term not found, termId : " + termId + " memberId : " + memberId)
        );
    }
}
