package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityReportDao {

    private final CommunityDao communityDao;
    private final CommunityReportRepository repository;

    @Transactional
    public CommunityReport report(long memberId, long communityId, ReportRequest reportRequest) {
        CommunityReport communityReport = getReport(memberId, communityId);

        if (communityReport.isReport() == reportRequest.getIsReport()) {
            return communityReport;
        }

        communityReport.setReport(reportRequest.getIsReport());
        communityReport.setDescription(reportRequest.getDescription());

        communityDao.reportCount(communityId, reportRequest.getIsReport() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_MINUS_ONE);

        return repository.save(communityReport);
    }

    @Transactional(readOnly = true)
    public CommunityReport getReport(long memberId, long communityId) {
        return repository.findByMemberIdAndCommunityId(memberId, communityId)
                .orElse(new CommunityReport(memberId, communityId));
    }

    @Transactional(readOnly = true)
    public boolean isReport(long memberId, long communityId) {
        return repository.existsByMemberIdAndCommunityIdAndIsReportIsTrue(memberId, communityId);
    }

    @Transactional(readOnly = true)
    public List<CommunityReport> reportCommunities(long memberId, List<Long> communityIds) {
        return repository.findByMemberIdAndCommunityIdInAndIsReportIsTrue(memberId, communityIds);
    }
}