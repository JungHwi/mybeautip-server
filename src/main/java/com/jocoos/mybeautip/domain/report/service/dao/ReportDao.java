package com.jocoos.mybeautip.domain.report.service.dao;

import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportDao {

    private final ReportRepository repository;

    @Transactional(readOnly = true)
    public Page<Report> getAllAccusedBy(Long accusedId, Pageable pageable) {
        return repository.findByYouId(accusedId, pageable);
    }
}
