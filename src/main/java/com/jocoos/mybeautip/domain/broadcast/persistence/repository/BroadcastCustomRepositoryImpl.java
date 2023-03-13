package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.vo.*;
import com.jocoos.mybeautip.global.util.QuerydslUtil;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcast.broadcast;
import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastCategory.broadcastCategory;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static com.querydsl.sql.SQLExpressions.count;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
public class BroadcastCustomRepositoryImpl implements BroadcastCustomRepository {

    private static final String TUPLE_WITH_THREE_PARAMS = "({0}, {1}, {2})";
    private static final StringTemplate STARTED_AT_CREATED_AT_ID_TUPLE_EXPRESSION = stringTemplate(
            TUPLE_WITH_THREE_PARAMS,
            broadcast.startedAt,
            broadcast.createdAt,
            broadcast.id);
    private final ExtendedQuerydslJpaRepository<Broadcast, Long> repository;

    public BroadcastCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Broadcast, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<BroadcastSearchResult> getList(BroadcastSearchCondition condition) {
        return repository.query(query ->
                searchResultWithMemberAndCategory(
                        baseConditionQuery(query, condition)
                )
                        .select(new QBroadcastSearchResult(broadcast, broadcastCategory, member))
                        .orderBy(
                                getOrders(condition.sort())
                        )
                        .offset(condition.offset())
                        .limit(condition.size())
                        .fetch());
    }

    @Override
    public Page<BroadcastSearchResult> getPageList(BroadcastSearchCondition condition) {
        List<BroadcastSearchResult> contents = getList(condition);
        JPAQuery<Long> countQuery = repository.query(query ->
                fromBroadcastWithMemberAndCategory(
                        baseConditionQuery(query, condition))
                        .select(count(broadcast)));
        return PageableExecutionUtils.getPage(contents, condition.pageable(), countQuery::fetchOne);
    }

    @Override
    public Optional<BroadcastSearchResult> get(long broadcastId) {
        BroadcastSearchResult result = repository.query(query -> searchResultWithMemberAndCategory(query)
                .where(eqId(broadcastId))
                .fetchOne());
        // result is nullable ignore ide
        return Optional.ofNullable(result);
    }

    @Override
    public BroadcastUpdateResult bulkUpdateStatusAndEndedAt(BroadcastUpdateStatusCondition condition) {

        List<Long> videoKeys = repository.query(query -> query
                .select(broadcast.videoKey)
                .from(broadcast)
                .where(
                        eqStatus(condition.currentStatus()),
                        startedAtBetween(condition.betweenStart(), condition.betweenEnd()),
                        startedAtLt(condition.startedAtLt()),
                        pausedAtLt(condition.pausedAtLt())
                )
                .fetch());

        if (!isEmpty(videoKeys)) {
            repository.update(query -> query
                    .set(broadcast.status, condition.updateStatus())
                    .set(broadcast.endedAt, condition.updateEndedAt())
                    .where(
                            inVideoKey(videoKeys)
                    )
                    .execute());
        }

        return new BroadcastUpdateResult(videoKeys.size(), videoKeys);
    }

    @Override
    public long bulkUpdateStatus(BroadcastUpdateStatusCondition condition) {
        return repository.update(query -> query
                .set(broadcast.status, condition.updateStatus())
                .where(
                        eqStatus(condition.currentStatus()),
                        startedAtBetween(condition.betweenStart(), condition.betweenEnd())
                )
                .execute());
    }

    private JPAQuery<BroadcastSearchResult> searchResultWithMemberAndCategory(JPAQuery<?> query) {
        return fromBroadcastWithMemberAndCategory(query)
                .select(new QBroadcastSearchResult(broadcast, broadcastCategory, member));
    }

    private JPAQuery<?> fromBroadcastWithMemberAndCategory(JPAQuery<?> query) {
        return query
                .from(broadcast)
                .join(member).on(broadcast.memberId.eq(member.id))
                .join(broadcastCategory).on(broadcast.category.eq(broadcastCategory));
    }

    private JPAQuery<?> baseConditionQuery(JPAQuery<?> query, BroadcastSearchCondition condition) {
        return query
                .where(
                        startedAtBetween(condition.startOfDay(), condition.endOfDay()),
                        createdAtAfter(condition.startAt()),
                        createdAtBefore(condition.endAt()),
                        searchByKeyword(condition.searchOption()),
                        inStatus(condition.statuses()),
                        isReported(condition.isReported()),
                        cursor(condition.cursor())
                );
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrders(sort, Broadcast.class, broadcast);
        return orderSpecifiers.length == 0
                ? new OrderSpecifier[]{
                broadcast.sortedStatus.asc(),
                broadcast.startedAt.desc(),
                broadcast.id.desc()}
                : orderSpecifiers;
    }

    // 커서 기반 페이지네이션과 유니크하지 않은 컬럼 정렬을 동시에 하기 위해 튜플 비교를 한다
    private BooleanExpression cursor(Long cursor) {
        if (cursor == null) {
            return null;
        }

        Tuple cursorValues = repository.query(query -> query
                .select(broadcast.sortedStatus, broadcast.startedAt, broadcast.createdAt)
                .from(broadcast)
                .where(eqId(cursor))
                .fetchOne());

        StringTemplate cursorValuesTuple = stringTemplate(
                TUPLE_WITH_THREE_PARAMS,
                cursorValues.get(broadcast.startedAt),
                cursorValues.get(broadcast.createdAt),
                cursor);

        BooleanExpression isBroadcastTupleLessThanCursorValuesTuple =
                STARTED_AT_CREATED_AT_ID_TUPLE_EXPRESSION.lt(cursorValuesTuple);

        Integer sortedStatus = cursorValues.get(broadcast.sortedStatus);
        return broadcast.sortedStatus.gt(sortedStatus)
                .or(broadcast.sortedStatus.eq(sortedStatus).and(isBroadcastTupleLessThanCursorValuesTuple));
    }

    private BooleanExpression searchByKeyword(SearchOption searchOption) {
        if (searchOption == null || searchOption.isNoKeywordSearch()) {
            return null;
        }
        String keyword = searchOption.getKeyword();
        if (Objects.equals(searchOption.getSearchField(), "username")) {
            return member.username.containsIgnoreCase(keyword);
        }
        return broadcast.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression isReported(Boolean isReported) {
        if (isReported == null) {
            return null;
        }
        return isReported ? broadcast.reportCount.gt(0) : broadcast.reportCount.eq(0);
    }

    private BooleanExpression pausedAtLt(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : broadcast.pausedAt.lt(zonedDateTime);
    }

    private BooleanExpression startedAtBetween(ZonedDateTime start, ZonedDateTime end) {
        return start == null || end == null ? null : broadcast.startedAt.between(start, end);
    }

    private BooleanExpression startedAtLt(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : broadcast.startedAt.lt(zonedDateTime);
    }

    private BooleanExpression createdAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : broadcast.createdAt.goe(dateTime);
    }

    private BooleanExpression createdAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : broadcast.createdAt.loe(dateTime);
    }

    private BooleanExpression inStatus(List<BroadcastStatus> statuses) {
        return isEmpty(statuses) ? null : broadcast.status.in(statuses);
    }

    private BooleanExpression eqStatus(BroadcastStatus status) {
        return status == null ? null : broadcast.status.eq(status);
    }

    private BooleanExpression inVideoKey(List<Long> videoKeys) {
        return isEmpty(videoKeys) ? null : broadcast.videoKey.in(videoKeys);
    }

    private BooleanExpression eqId(Long id) {
        return id == null ? null : broadcast.id.eq(id);
    }
}
