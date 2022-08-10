package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityReportDao {

    private final CommunityReportRepository repository;

    @Transactional(readOnly = true)
    public boolean isReport(long memberId, long communityId) {
        return repository.existsByMemberIdAndCommunityIdAndIsReportIsTrue(memberId, communityId);
    }

    @Transactional(readOnly = true)
    public List<CommunityReport> reportCommunities(long memberId, List<Long> communityIds) {
        return repository.findByMemberIdAndCommunityIdInAndIsReportIsTrue(memberId, communityIds);
    }
}