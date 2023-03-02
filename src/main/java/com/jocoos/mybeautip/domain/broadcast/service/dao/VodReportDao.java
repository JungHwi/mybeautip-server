package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.VodReport;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.VodReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class VodReportDao {

    private final VodReportRepository repository;

    @Transactional
    public VodReport save(VodReport report) {
        return repository.save(report);
    }

    @Transactional(readOnly = true)
    public boolean exist(long vodId, long reporterId) {
        return repository.existsByVodIdAndReporterId(vodId, reporterId);
    }

    @Transactional(readOnly = true)
    public List<VodReport> getByVodIdIn(Set<Long> ids) {
        return repository.findAllByVodIdIn(ids);
    }
}
