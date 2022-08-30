package com.jocoos.mybeautip.domain.community.persistence.repository;

import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentReport;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityCommentReportRepository extends DefaultJpaRepository<CommunityCommentReport, Long> {

    Optional<CommunityCommentReport> findByMemberIdAndCommentId(long memberId, long commentId);

    boolean existsByMemberIdAndCommentIdAndIsReportIsTrue(long memberId, long commentId);

    List<CommunityCommentReport> findByMemberIdAndCommentIdInAndIsReportIsTrue(long memberId, List<Long> commentIds);
}
