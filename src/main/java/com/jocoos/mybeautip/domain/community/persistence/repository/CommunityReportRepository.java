package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;

public interface CommunityReportRepository extends DefaultJpaRepository<CommunityReport, Long> {

    boolean existsByMemberIdAndCommunityIdAndIsReportIsTrue(long memberId, long communityId);

    List<CommunityReport> findByMemberIdAndCommunityIdInAndIsReportIsTrue(long memberId, List<Long> communityId);
}
