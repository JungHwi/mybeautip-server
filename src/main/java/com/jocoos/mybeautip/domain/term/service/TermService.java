package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.converter.TermConverter;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.term.code.TermStatus.DELETE;

@RequiredArgsConstructor
@Service
public class TermService {

    private final TermRepository termRepository;
    private final TermConverter termConverter;

    @Transactional(readOnly = true)
    public List<TermResponse> getTermsUsedIn(TermUsedInType usedIn) {
        List<Term> terms = termRepository.findAllByUsedInTypeAndCurrentTermStatusNot(usedIn, DELETE);
        return termConverter.convertToListResponse(terms);
    }
}
