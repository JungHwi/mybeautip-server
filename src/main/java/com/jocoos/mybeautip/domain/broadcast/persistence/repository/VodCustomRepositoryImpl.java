package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.dto.QVodResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.VodResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.broadcast.vo.VodSearchCondition;
import com.jocoos.mybeautip.domain.event.code.SortField;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.chrono.ChronoZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QVod.vod;
import static com.jocoos.mybeautip.member.QMember.member;

@Repository
public class VodCustomRepositoryImpl implements VodCustomRepository {

    private final ExtendedQuerydslJpaRepository<Vod, Long> repository;

    public VodCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Vod, Long> repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<VodResponse> getVodList(VodSearchCondition condition) {
        PathBuilder<Vod> path = new PathBuilder<>(Vod.class, "vod");
        SortField sortField = condition.sortField();
        switch (sortField) {
            case TOTAL_HEART_COUNT, VIEW_COUNT -> {
                ComparablePath<Integer> comparablePath =
                        path.getComparable(sortField.getFieldName(), Integer.class);
                return getVodList(condition, comparablePath);
            }
            default -> {
                ComparablePath<ChronoZonedDateTime> comparablePath =
                        path.getComparable(sortField.getFieldName(), ChronoZonedDateTime.class);
                return getVodList(condition, comparablePath);
            }
        }


    }

    private <T extends Comparable<T>> List<VodResponse> getVodList(VodSearchCondition condition,
                                                                   ComparablePath<T> comparablePath) {
        T cursorValue = getCursorValue(comparablePath, condition.cursor());
        return repository.query(query -> query
                        .select(new QVodResponse(vod, member))
                        .from(vod)
                        .join(member).on(vod.memberId.eq(member.id))
                        .where(cursor(comparablePath, cursorValue, condition.cursor()))
                        .orderBy(getOrders(condition.getSort()))
                        .limit(condition.getPageSize())
                        .fetch());
    }

    private <T extends Comparable<T>> T getCursorValue(ComparablePath<T> comparablePath, Long cursor) {
        if (cursor == null) {
            return null;
        }
        return repository.query(query -> query
                .select(comparablePath)
                .from(vod)
                .where(eqId(cursor))
                .fetchFirst());
    }

    private <T extends Comparable<T>> BooleanExpression cursor(ComparablePath<T> comparablePath,
                                                               T comparablePathValue,
                                                               Long cursor) {
        return comparablePathValue == null || cursor == null
                ? null
                : comparablePath.lt(comparablePathValue)
                .or(comparablePath.eq(comparablePathValue).and(vod.id.lt(cursor)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private OrderSpecifier<?>[] getOrders(Sort sort) {
        OrderSpecifier[] orderSpecifiers = sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    SimplePath<Vod> path = Expressions.path(Vod.class, vod, order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .toArray(OrderSpecifier[]::new);
        return orderSpecifiers.length == 0
                ? new OrderSpecifier[]{vod.createdAt.desc(), vod.id.desc()}
                : orderSpecifiers;
    }

    private static BooleanExpression eqId(Long cursor) {
        return cursor == null ? null : vod.id.eq(cursor);
    }
}
