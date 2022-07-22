package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.converter.TermConverter;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import com.jocoos.mybeautip.domain.term.persistence.repository.MemberTermRepository;
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.term.code.TermStatus.DELETE;

@RequiredArgsConstructor
@Service
public class TermService {

    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;
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

    private Term getTermWithValid(long termId) {
        return termRepository.findById(termId).orElseThrow(
                () -> new NotFoundException("term not found, termId : " + termId)
        );
    }
}
