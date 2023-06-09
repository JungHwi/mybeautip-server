package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.jocoos.mybeautip.domain.event.vo.EventSearchResult;
import com.jocoos.mybeautip.domain.event.vo.QEventSearchResult;
import com.jocoos.mybeautip.global.vo.Paging;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
import static com.jocoos.mybeautip.domain.event.persistence.domain.QEventJoin.eventJoin;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.dsl.Expressions.nullExpression;
import static com.querydsl.sql.SQLExpressions.count;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {

    private final ExtendedQuerydslJpaRepository<Event, Long> repository;


    public EventCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Event, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Event> getEvents(EventSearchCondition condition) {
        JPAQuery<Event> baseQuery = paging(baseSelectQuery(condition), condition.getPaging());
        return orderByDefault(baseQuery).fetch();
    }

    @Override
    public List<EventSearchResult> getEventsWithJoinCount(EventSearchCondition condition) {
        JPAQuery<Event> baseQuery = paging(baseSelectQuery(condition), condition.getPaging());

        if (condition.isOrderByJoinCount()) {
            return orderByJoinCount(baseQuery);
        }

        return resultWithJoinCount(orderByDefault(baseQuery)).fetch();
    }

    @Override
    public Long getTotalCount(EventSearchCondition condition) {
        return baseSelectQuery(condition)
                .select(count(event))
                .fetchOne();
    }

    @Override
    public Integer getLastSortOrder() {
        return repository.query(query -> query
                .select(event.fixSorting.sorting)
                .from(event)
                .orderBy(event.fixSorting.sorting.desc())
                .fetchFirst());
    }

    @Override
    public List<Long> arrangeByIndex(List<Long> sortedIds) {
        allIsTopFixFalseAndSortingToNull();
        IntStream.range(0, sortedIds.size()).forEach(index -> isTopFixTrueAndSortingByIndex(sortedIds, index));
        return getSortedIds();
    }

    @Override
    public Map<EventStatus, Long> getEventStatesWithNum() {
        return repository.query(query -> query
                .select(event.status, count(event))
                .from(event)
                .groupBy(event.status))
                .transform(groupBy(event.status).as(count(event)));
    }

    private JPAQuery<Event> baseSelectQuery(EventSearchCondition condition) {
        return repository.query(query -> query
                .select(event)
                .from(event)
                .where(
                        searchCondition(condition.getKeyword()),
                        inStatuses(condition.getStatuses()),
                        eqVisible(condition.getIsVisible()),
                        eqType(condition.getType()),
                        goeCreatedAt(condition.getStartAt()),
                        loeCreatedAt(condition.getEndAt()),
                        startAtBefore(condition.getBetween()),
                        endAtAfter(condition.getBetween()),
                        isTopFix(condition.isTopFix()),
                        eqRelationId(condition.getCommunityCategoryId())
                ));
    }

    private JPAQuery<Event> paging(JPAQuery<Event> baseQuery, Paging paging) {

        if (paging == null) {
            return baseQuery;
        }

        return switch (paging.getType()) {
            case NO_PAGING -> baseQuery;
            case OFFSET -> baseQuery
                    .offset(paging.getOffset())
                    .limit(paging.getSize());
            case CURSOR -> baseQuery
                    .where()
                    .limit(paging.getSize());
        };
    }

    private JPAQuery<Event> orderByDefault(JPAQuery<Event> query) {
        return query
                .orderBy(event.statusSorting.asc().nullsLast(),
                        event.fixSorting.sorting.asc().nullsLast(),
                        event.createdAt.desc(),
                        event.id.desc());
    }

    private JPAQuery<EventSearchResult> resultWithJoinCount(JPAQuery<Event> query) {
        return query.
                select(new QEventSearchResult(event, count(eventJoin).nullif(0L)))
                .leftJoin(eventJoin).on(event.id.eq(eventJoin.eventId))
                .groupBy(event.id);
    }

    private List<EventSearchResult> orderByJoinCount(JPAQuery<Event> query) {
        NumberPath<Long> joinCount = Expressions.numberPath(Long.class, "joinCount");
        return query
                .select(new QEventSearchResult(event, count(eventJoin).nullif(0L).as(joinCount)))
                .leftJoin(eventJoin).on(event.id.eq(eventJoin.eventId))
                .groupBy(event.id)
                .orderBy(joinCount.desc(), event.createdAt.desc(), event.id.desc())
                .fetch();
    }

    private void allIsTopFixFalseAndSortingToNull() {
        repository.update(query -> query
                .set(event.fixSorting.sorting, nullExpression())
                .set(event.fixSorting.isTopFix, false)
                .execute());
    }

    private void isTopFixTrueAndSortingByIndex(List<Long> sortedIds, int index) {
        repository.update(query -> query
                .set(event.fixSorting.sorting, index + 1)
                .set(event.fixSorting.isTopFix, true)
                .where(eqId(sortedIds.get(index)))
                .execute());
    }

    private List<Long> getSortedIds() {
        return repository.query(query -> query
                .select(event.id)
                .from(event)
                .where(isTopFix(true))
                .orderBy(event.fixSorting.sorting.asc())
                .fetch());
    }

    private BooleanExpression eqId(Long eventId) {
        return eventId == null ? null : event.id.eq(eventId);
    }

    private BooleanExpression eqRelationId(Long relationId) {
        return relationId == null ? null : event.relationId.eq(relationId);
    }

    private BooleanExpression isTopFix(Boolean isTopFix) {
        if (isTopFix == null) {
            return null;
        }
        return isTopFix ? event.fixSorting.isTopFix.isTrue() : event.fixSorting.isTopFix.isFalse();
    }

    private BooleanExpression goeCreatedAt(Date dateTime) {
        return dateTime == null ? null : event.createdAt.goe(dateTime);
    }

    private BooleanExpression loeCreatedAt(Date dateTime) {
        return dateTime == null ? null : event.createdAt.loe(dateTime);
    }

    private BooleanExpression endAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.endAt.after(dateTime);
    }

    private BooleanExpression startAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.startAt.before(dateTime);
    }

    private BooleanExpression inStatuses(Set<EventStatus> statuses) {
        return isEmpty(statuses) ? null : event.status.in(statuses);
    }

    private BooleanExpression eqVisible(Boolean visible) {
        return visible == null ? null : event.isVisible.eq(visible);
    }

    private BooleanExpression eqType(EventType type) {
        return type == null ? null : event.type.eq(type);
    }

    private BooleanBuilder searchCondition(String keyword) {
        return StringUtils.hasText(keyword) ? containsTitle(keyword) : null;
    }

    private BooleanBuilder containsTitle(String keyword) {
        return nullSafeBuilder(() -> event.title.contains(keyword));
    }

    private BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (NullPointerException e) {
            return new BooleanBuilder();
        }
    }
}

