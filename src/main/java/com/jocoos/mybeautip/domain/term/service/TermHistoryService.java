package com.jocoos.mybeautip.domain.term.service;

import com.jocoos.mybeautip.domain.term.persistence.repository.TermHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jocoos.mybeautip.domain.term.code.TermStatus.REQUIRED;

@Service
@RequiredArgsConstructor
public class TermHistoryService {

    private final TermHistoryRepository termHistoryRepository;

    public List<Long> getRequiredChangeTermIds(List<Long> termIds) {
        return termHistoryRepository.findVersionChangeHistoryIn(REQUIRED, termIds);
    }
}
