package com.jocoos.mybeautip.domain.community.dao;

import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CommunityReportDao {

    private final CommunityReportRepository repository;

    @Transactional(readOnly = true)
    public boolean isReport(long memberId, long communityId) {
        return repository.existsByMemberIdAndCommunityIdAndIsReportIsTrue(memberId, communityId);
    }
}
