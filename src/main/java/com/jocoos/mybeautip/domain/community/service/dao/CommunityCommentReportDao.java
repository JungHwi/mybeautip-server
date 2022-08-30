package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentReport;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityCommentReportRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommunityCommentReportDao {

    private final CommunityCommentDao communityDao;
    private final CommunityCommentReportRepository repository;

    @Transactional
    public CommunityCommentReport report(long memberId, long commentId, ReportRequest reportRequest) {
        CommunityCommentReport report = getReport(memberId, commentId);

        if (report.isReport()) {
            throw new BadRequestException("already_report", "Already report. Id is " + commentId);
        }

        if (report.isReport() == reportRequest.getIsReport()) {
            return report;
        }

        report.setReport(reportRequest.getIsReport());
        report.setDescription(reportRequest.getDescription());

        communityDao.reportCount(commentId, reportRequest.getIsReport() ? NumberUtils.INTEGER_ONE : NumberUtils.INTEGER_MINUS_ONE);

        return repository.save(report);
    }

    @Transactional(readOnly = true)
    public CommunityCommentReport getReport(long memberId, long commentId) {
        return repository.findByMemberIdAndCommentId(memberId, commentId)
                .orElse(new CommunityCommentReport(memberId, commentId));
    }

    @Transactional(readOnly = true)
    public boolean isReport(long memberId, long commentId) {
        return repository.existsByMemberIdAndCommentIdAndIsReportIsTrue(memberId, commentId);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentReport> reportComments(long memberId, List<Long> commentIds) {
        return repository.findByMemberIdAndCommentIdInAndIsReportIsTrue(memberId, commentIds);
    }
}