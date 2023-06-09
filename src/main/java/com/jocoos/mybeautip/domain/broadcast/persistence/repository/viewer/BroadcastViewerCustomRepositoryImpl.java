package com.jocoos.mybeautip.domain.broadcast.persistence.repository.viewer;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.vo.*;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastViewer.broadcastViewer;
import static com.jocoos.mybeautip.global.constant.SignConstant.ZERO;
import static com.jocoos.mybeautip.member.QMember.member;

@Repository
public class BroadcastViewerCustomRepositoryImpl implements BroadcastViewerCustomRepository {

    private final ExtendedQuerydslJpaRepository<BroadcastViewer, Long> repository;

    public BroadcastViewerCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<BroadcastViewer, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<ViewerSearchResult> search(ViewerSearchCondition condition) {
        return baseSearchQuery(condition).fetch();
    }

    @Override
    public ViewerSearchResult get(long broadcastId, long memberId) {
        List<ViewerSearchResult> result = baseGetQuery(broadcastId, memberId).fetch();

        if (CollectionUtils.isEmpty(result)) {
            throw new MemberNotFoundException(memberId);
        } else {
            return result.get(ZERO);
        }
    }

    @Override
    public List<ViewerCountResult> getViewerCount(long broadcastId) {
        return repository.query(query -> query
                .select(new QViewerCountResult(
                        broadcastViewer.type,
                        broadcastViewer.status,
                        broadcastViewer.id.count().castToNum(Integer.class)
                ))
                .from(broadcastViewer)
                .where(broadcastViewer.broadcast.id.eq(broadcastId)
                        .and(broadcastViewer.type.in(BroadcastViewerType.defaultSearchType))
                )
                .groupBy(broadcastViewer.type, broadcastViewer.status)
                .fetch()
        );
    }

    private JPAQuery<ViewerSearchResult> baseGetQuery(long broadcastId, long memberId) {
        return repository.query(query -> query
                .select(new QViewerSearchResult(member, broadcastViewer))
                .from(broadcastViewer).leftJoin(member).on(broadcastViewer.memberId.eq(member.id))
                .where(
                        broadcastViewer.broadcast.id.eq(broadcastId),
                        broadcastViewer.memberId.eq(memberId))
                );
    }

    private JPAQuery<ViewerSearchResult> baseSearchQuery(ViewerSearchCondition condition) {
        return repository.query(query -> query
                .select(new QViewerSearchResult(member, broadcastViewer))
                .from(broadcastViewer).leftJoin(member).on(broadcastViewer.memberId.eq(member.id))
                .where(
                        broadcastViewer.broadcast.id.eq(condition.getBroadcastId()),
                        eqViewerType(condition.getType()),
                        eqViewerStatus(condition.getStatus()),
                        isSuspended(condition.getIsSuspended()),
                        cursor(condition.getCursorCondition())
                )
                .orderBy(fieldType().asc(), broadcastViewer.sortedUsername.asc())
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize()));
    }

    private StringTemplate fieldType() {
        return fieldType(null);
    }

    // USE MYSQL FUNCTION
    private StringTemplate fieldType(BroadcastViewerType type) {
        if (type == null) {
            return Expressions.stringTemplate("FIELD(type, 'MANAGER', 'MEMBER', 'GUEST') ");
        }

        return Expressions.stringTemplate("FIELD({0}, 'MANAGER', 'MEMBER', 'GUEST') ", type.name());
    }

    private BooleanExpression isSuspended(Boolean isSuspended) {
        return isSuspended == null ? null : broadcastViewer.isSuspended.eq(isSuspended);
    }

    private BooleanExpression eqViewerStatus(BroadcastViewerStatus status) {
        return status == null ? null : broadcastViewer.status.eq(status);
    }

    private BooleanExpression eqViewerType(BroadcastViewerType type) {
        return type == null ? broadcastViewer.type.in(BroadcastViewerType.defaultSearchType) : broadcastViewer.type.eq(type);
    }

    private BooleanExpression cursor(ViewerCursorCondition cursor) {
        if (cursor == null) {
            return null;
        }

        return fieldType().gt(fieldType(cursor.type())).or(
                fieldType().eq(fieldType(cursor.type())).and(broadcastViewer.sortedUsername.gt(cursor.username())));
    }
}
