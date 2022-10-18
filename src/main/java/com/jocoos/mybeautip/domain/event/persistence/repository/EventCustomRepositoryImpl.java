package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.dto.QEventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.persistence.domain.EventJoin;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.jsonwebtoken.lang.Collections;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
import static com.jocoos.mybeautip.domain.event.persistence.domain.QEventJoin.eventJoin;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.sql.SQLExpressions.count;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {

    private final ExtendedQuerydslJpaRepository<Event, Long> repository;
    private final ExtendedQuerydslJpaRepository<EventJoin, Long> eventJoinRepository;

    public EventCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Event, Long> repository,
                                     @Lazy ExtendedQuerydslJpaRepository<EventJoin, Long> eventJoinRepository) {
        this.repository = repository;
        this.eventJoinRepository = eventJoinRepository;
    }

    @Override
    public List<Event> getEvents(EventSearchCondition condition) {
        JPAQuery<Event> baseQuery = baseSelectQuery(condition);

        if (condition.isNoOffset()) {
            return baseQuery
                    .limit(condition.getLimit())
                    .fetch();
        }

        if (condition.isPaging()) {
            return baseQuery
                    .offset(condition.getOffset())
                    .limit(condition.getLimit())
                    .fetch();
        }

        return baseQuery.fetch();
    }

    @Override
    public Long getTotalCount(EventSearchCondition condition) {
        return baseSelectQuery(condition)
                .select(count(event))
                .fetchOne();
    }

    @Override
    public Map<Long, Long> getEventJoinCountMap(List<Long> eventIds) {
        return eventJoinRepository.query(query -> query
                .select(count(eventJoin))
                .from(eventJoin)
                .where(inEventJoinEventId(eventIds))
                .groupBy(eventJoin.eventId)
                .transform(groupBy(eventJoin.eventId).as(count(eventJoin))));
    }

    private static BooleanExpression inEventJoinEventId(List<Long> ids) {
        return Collections.isEmpty(ids) ? null : eventJoin.eventId.in(ids);
    }

    @Override
    public List<EventStatusResponse> getEventStatesWithNum() {
        return repository.query(query -> query
                .select(new QEventStatusResponse(event.status, count(event)))
                .from(event)
                .groupBy(event.status)).fetch();
    }

    private JPAQuery<Event> baseSelectQuery(EventSearchCondition condition) {
        return repository.query(query -> query
                .select(event)
                .from(event)
                .where(
                        searchCondition(condition.getKeyword()),
                        inStatuses(condition.getStatuses()),
                        eqType(condition.getType()),
                        startAtAfter(condition.getStartAt()),
                        endAtBefore(condition.getEndAt()),
                        startAtBefore(condition.getBetween()),
                        endAtAfter(condition.getBetween())
                )
                .orderBy(event.statusSorting.asc(), event.sorting.asc(), event.id.desc()));
    }

    private BooleanExpression startAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.startAt.goe(dateTime);
    }

    private BooleanExpression endAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.endAt.loe(dateTime);
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

    private BooleanExpression eqType(EventType type) {
        return type == null ? null : event.type.eq(type);
    }

    private BooleanBuilder searchCondition(String keyword) {
        return StringUtils.hasText(keyword) ? containsTitle(keyword) : null;
    }

    private BooleanBuilder containsTitle(String keyword) {
        return nullSafeBuilder(() -> event.title.contains(keyword));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (NullPointerException e) {
            return new BooleanBuilder();
        }
    }

}
