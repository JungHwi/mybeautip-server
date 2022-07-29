package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.term.code.TermUsedInType;
import com.jocoos.mybeautip.domain.term.converter.TermConverter;
import com.jocoos.mybeautip.domain.term.dto.TermDetailResponse;
import com.jocoos.mybeautip.domain.term.dto.TermResponse;
import com.jocoos.mybeautip.domain.term.persistence.domain.Term;
import com.jocoos.mybeautip.domain.term.persistence.repository.TermRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TermService {

    private final TermRepository termRepository;
    private final TermConverter termConverter;


    @Transactional(readOnly = true)
    public List<TermResponse> getTermsUsedIn(TermUsedInType usedIn) {
        List<Term> terms = termRepository.findAllByUsedIn(usedIn.name());
        return termConverter.convertToListResponse(terms);
    }

    @Transactional(readOnly = true)
    public TermDetailResponse getTermDetail(long termId) {
        return termConverter.convertToResponse(getTerm(termId));
    }


    public List<Term> getAllTerms() {
        return termRepository.findAll();
    }

    public List<Term> getTerms(List<Long> termIds) {
        List<Term> terms = termRepository.findAllByIdIn(termIds);
        validTermsExist(termIds.size(), terms.size());
        return terms;
    }

    public Term getTerm(long termId) {
        return termRepository.findById(termId).orElseThrow(
                () -> new NotFoundException("term not found, termId : " + termId)
        );
    }

    public Term getLastUpdateTerm() {
        return termRepository.findTopByOrderByModifiedAtDesc()
                .orElseThrow(() -> new NotFoundException("no term found"));
    }

    public void validTermExist(long termId) {
        if (!termRepository.existsById(termId))
            throw new NotFoundException("term not found, termId : " + termId);
    }

    public void validTermsExist(int requestSize, int dataSize) {
        if (requestSize != dataSize)
            throw new NotFoundException("terms not found. check the ids");
    }


    // 2021-07-28 현재 서버에서 약관 관리하지 않기 때문에 임시로 만듦, 추후 서버에서 약관 관리한다면 삭제
    public List<Term> getTermsByType(Set<TermType> termTypes) {
        List<Term> terms = termRepository.findAllByTypeIn(termTypes);
        validTermsExist(termTypes.size(), terms.size());
        return terms;
    }

    public Term getTermByType(TermType type) {
        return termRepository.findByType(type).orElseThrow(
                () -> new NotFoundException("term not found, term type : " + type)
        );
    }
}
