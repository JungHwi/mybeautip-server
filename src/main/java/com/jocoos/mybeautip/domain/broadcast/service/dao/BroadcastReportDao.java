package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BroadcastReportDao {

    private final BroadcastReportRepository repository;

    @Transactional
    public BroadcastReport save(BroadcastReport report) {
        return repository.save(report);
    }
}
