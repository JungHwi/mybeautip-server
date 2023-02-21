package com.jocoos.mybeautip.domain.broadcast.persistence.repository.viewer;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerStatus;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastViewerType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastViewer;
import com.jocoos.mybeautip.domain.broadcast.vo.QViewerSearchResult;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerCursorCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.ViewerSearchResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastViewer.broadcastViewer;
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

    private JPAQuery<ViewerSearchResult> baseSearchQuery(ViewerSearchCondition condition) {
        return repository.query(query -> query
                .select(new QViewerSearchResult(member, broadcastViewer))
                .from(broadcastViewer).leftJoin(member).on(broadcastViewer.memberId.eq(member.id))
                .where(
                        broadcastViewer.broadcast.id.eq(condition.getBroadcastId()),
                        eqViewerType(condition.getType()),
                        eqViewerStatus(condition.getStatus()),
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

    private BooleanExpression eqViewerStatus(BroadcastViewerStatus status) {
        return status == null ? null : broadcastViewer.status.eq(status);
    }

    private BooleanExpression eqViewerType(BroadcastViewerType type) {
        return type == null ? null : broadcastViewer.type.eq(type);
    }

    private BooleanExpression cursor(ViewerCursorCondition cursor) {
        if (cursor == null) {
            return null;
        }

        return fieldType().gt(fieldType(cursor.type())).or(
                fieldType().eq(fieldType(cursor.type())).and(broadcastViewer.sortedUsername.gt(cursor.username())));
    }
}
