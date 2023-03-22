package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastReportResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.QBroadcastReportResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.member.dto.QMemberIdAndUsernameResponse;
import com.jocoos.mybeautip.member.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastReport.broadcastReport;

@Repository
public class BroadcastReportCustomRepositoryImpl implements BroadcastReportCustomRepository {

    private final ExtendedQuerydslJpaRepository<BroadcastReport, Long> repository;

    public BroadcastReportCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<BroadcastReport, Long> repository) {
        this.repository = repository;
    }


    @Override
    public Page<BroadcastReportResponse> getList(Long broadcastId, BroadcastReportType type, Pageable pageable) {
        QMember reporter = new QMember("reporter");
        QMember reported = new QMember("reported");

        List<BroadcastReportResponse> contents = defaultWhereCondition(broadcastId, type)
                .select(new QBroadcastReportResponse(
                        broadcastReport,
                        new QMemberIdAndUsernameResponse(reporter.id, reporter.username),
                        new QMemberIdAndUsernameResponse(reported.id, reported.username)))
                .from(broadcastReport)
                .join(reporter).on(reporter.id.eq(broadcastReport.reporterId))
                .join(reported).on(reported.id.eq(broadcastReport.reportedId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(broadcastReport.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = defaultWhereCondition(broadcastId, type)
                .select(broadcastReport.count())
                .from(broadcastReport);

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private JPAQuery<?> defaultWhereCondition(Long broadcastId, BroadcastReportType type) {
        return repository.query(query -> query
                .where(eqBroadcastId(broadcastId),
                eqType(type)));
    }

    private BooleanExpression eqBroadcastId(Long broadcastId) {
        return broadcastId == null ? null : broadcastReport.broadcastId.eq(broadcastId);
    }

    private BooleanExpression eqType(BroadcastReportType type) {
        return type == null ? null : broadcastReport.type.eq(type);
    }
}
