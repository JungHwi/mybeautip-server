package com.jocoos.mybeautip.domain.event.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.event.code.EventStatus;
import com.jocoos.mybeautip.domain.event.code.EventType;
import com.jocoos.mybeautip.domain.event.persistence.domain.Event;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {

    private final ExtendedQuerydslJpaRepository<Event, Long> repository;

    public EventCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Event, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Event> getEvents(EventType type, Set<EventStatus> statuses) {
        return repository.query(query -> query
                .select(event)
                .from(event)
                .where(
                        inStatuses(statuses),
                        eqType(type)
                )
                .orderBy(event.statusSorting.asc(), event.sorting.asc(), event.id.desc())
                .fetch());
    }

    private BooleanExpression inStatuses(Set<EventStatus> statuses) {
        return isEmpty(statuses) ? null : event.status.in(statuses);
    }

    private BooleanExpression eqType(EventType type) {
        return type == null ? null : event.type.eq(type);
    }

}
