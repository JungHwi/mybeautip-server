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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcast.broadcast;
import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastCategory.broadcastCategory;
import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastPinMessage.broadcastPinMessage;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
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
                getSearchResult(baseConditionQuery(query, condition))
                        .where(cursor(condition.cursor()))
                        .orderBy(getOrders(condition.sort()))
                        .offset(condition.offset())
                        .limit(condition.size())
                        .fetch());
    }

    @Override
    public long count(BroadcastSearchCondition condition) {
        Long count = repository.query(query ->
                withMemberAndCategoryAndPinMessage(
                        baseConditionQuery(query, condition))
                        .select(broadcast.count())
                        .fetchOne());
        // count can be null ignore ide
        return count == null ? 0 : count;
    }

    @Override
    public Page<BroadcastSearchResult> getPage(BroadcastSearchCondition condition) {
        List<BroadcastSearchResult> contents = getList(condition);
        long count = count(condition);
        return new PageImpl<>(contents, condition.pageable(), count);
    }

    @Override
    public Optional<BroadcastSearchResult> get(long broadcastId) {
        BroadcastSearchResult result = repository.query(query -> getSearchResult(query)
                .where(eqId(broadcastId))
                .fetchOne());
        // result is nullable ignore ide
        return Optional.ofNullable(result);
    }

    @Override
    public void bulkUpdateStatusAndEndedAt(Collection<Long> ids, BroadcastStatus status, ZonedDateTime endedAt) {
        if (!isEmpty(ids)) {
            repository.update(query -> query
                    .set(broadcast.status, status)
                    .set(broadcast.endedAt, endedAt)
                    .where(inIds(ids))
                    .execute());
        }
    }

    @Override
    public void bulkUpdateStatus(Collection<Long> ids, BroadcastStatus status) {
        if (!isEmpty(ids)) {
            repository.update(query -> query
                    .set(broadcast.status, status)
                    .where(inIds(ids))
                    .execute());
        }
    }

    @Override
    public List<BroadcastUpdateCandidate> getUpdateCandidates(BroadcastUpdateCandidateCondition condition) {
        return repository.query(query -> query
                .select(new QBroadcastUpdateCandidate(broadcast.id, broadcast.videoKey, broadcast.memberId))
                .from(broadcast)
                .where(
                        inStatus(condition.statuses()),
                        startedAtBetween(condition.betweenStart(), condition.betweenEnd()),
                        startedAtLt(condition.startedAt()),
                        pausedAtLt(condition.pausedAt())
                )
                .fetch());
    }

    private JPAQuery<BroadcastSearchResult> getSearchResult(JPAQuery<?> query) {
        return withMemberAndCategoryAndPinMessage(query)
                .select(new QBroadcastSearchResult(broadcast, broadcast.statistics, broadcastCategory, member, broadcastPinMessage));
    }

    private JPAQuery<?> withMemberAndCategoryAndPinMessage(JPAQuery<?> query) {
        return query
                .from(broadcast)
                .innerJoin(broadcast.statistics).on(broadcast.id.eq(broadcast.statistics.id))
                .join(broadcastCategory).on(broadcast.category.eq(broadcastCategory))
                .join(member).on(broadcast.memberId.eq(member.id))
                .leftJoin(broadcastPinMessage).on(broadcast.id.eq(broadcastPinMessage.broadcastId));
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
                        eqMemberId(condition.memberId()),
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
        return isReported ? broadcast.statistics.reportCount.gt(0) : broadcast.statistics.reportCount.eq(0);
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

    private BooleanExpression inIds(Collection<Long> ids) {
        return isEmpty(ids) ? null : broadcast.id.in(ids);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId == null ? null : broadcast.memberId.eq(memberId);
    }

    private BooleanExpression eqId(Long id) {
        return id == null ? null : broadcast.id.eq(id);
    }
}
