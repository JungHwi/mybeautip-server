package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.dto.EventStatusResponse;
import com.jocoos.mybeautip.domain.event.dto.QEventStatusResponse;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.jocoos.mybeautip.domain.event.vo.EventSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
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
        JPAQuery<Event> baseQuery = baseSelectQuery(condition);
        if (condition.getLimit() != null) {
            return baseQuery
                    .limit(condition.getLimit())
                    .fetch();
        }
        return baseQuery.fetch();
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
                        inStatuses(condition.getStatuses()),
                        eqType(condition.getType()),
                        startedAtBefore(condition.getBetween()),
                        endedAtAfter(condition.getBetween())
                )
                .orderBy(event.statusSorting.asc(), event.sorting.asc(), event.id.desc()));
    }

    private BooleanExpression endedAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.endAt.after(dateTime);
    }

    private BooleanExpression startedAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : event.startAt.before(dateTime);
    }

    private BooleanExpression inStatuses(Set<EventStatus> statuses) {
        return isEmpty(statuses) ? null : event.status.in(statuses);
    }

    private BooleanExpression eqType(EventType type) {
        return type == null ? null : event.type.eq(type);
    }

}
