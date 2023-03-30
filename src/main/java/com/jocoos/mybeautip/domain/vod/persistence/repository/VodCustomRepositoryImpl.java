package com.jocoos.mybeautip.domain.vod.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.global.code.SortField;
import com.jocoos.mybeautip.domain.vod.code.VodStatus;
import com.jocoos.mybeautip.domain.vod.dto.QVodResponse;
import com.jocoos.mybeautip.domain.vod.dto.VodResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastCategory.broadcastCategory;
import static com.jocoos.mybeautip.domain.vod.persistence.domain.QVod.vod;
import static com.jocoos.mybeautip.member.QMember.member;

@Repository
public class VodCustomRepositoryImpl implements VodCustomRepository {

    private final ExtendedQuerydslJpaRepository<Vod, Long> repository;

    public VodCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Vod, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Vod> getVodList(VodSearchCondition condition) {
        return withBaseConditionAndSort(condition)
                .select(vod)
                .fetch();
    }

    @Override
    public Page<VodResponse> getPageList(VodSearchCondition condition) {
        List<VodResponse> contents = getVodResponses(condition);
        return new PageImpl<>(contents, condition.pageable(), count(condition));
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<VodResponse> getVodResponses(VodSearchCondition condition) {
        PathBuilder<Vod> path = new PathBuilder<>(Vod.class, "vod");
        SortField nonUniqueSortField = condition.nonUniqueCursor();
        switch (nonUniqueSortField) {
            case TOTAL_HEART_COUNT, VIEW_COUNT -> {
                ComparablePath<Integer> comparablePath =
                        path.getComparable(nonUniqueSortField.getFieldName(), Integer.class);
                return getVodList(condition, comparablePath);
            }
            default -> {
                ComparablePath<ChronoZonedDateTime> comparablePath =
                        path.getComparable(nonUniqueSortField.getFieldName(), ChronoZonedDateTime.class);
                return getVodList(condition, comparablePath);
            }
        }
    }

    @Override
    public long count(VodSearchCondition condition) {
        Long count = withBaseCondition(condition)
                .select(vod.count())
                .fetchOne();
        return count == null ? 0 : count;
    }

    private <T extends Comparable<T>> List<VodResponse> getVodList(VodSearchCondition condition,
                                                                   ComparablePath<T> comparablePath) {
        T cursorValue = getCursorValue(comparablePath, condition.cursor());
        return withBaseConditionAndSort(condition)
                .select(new QVodResponse(vod, broadcastCategory, member))
                .join(broadcastCategory).on(vod.category.eq(broadcastCategory))
                .join(member).on(vod.memberId.eq(member.id).and(eqMemberId(condition.memberId())))
                .where(
                        cursor(comparablePath, cursorValue, condition.cursor())
                )
                .fetch();
    }

    private JPAQuery<?> withBaseConditionAndSort(VodSearchCondition condition) {
        return withBaseCondition(condition)
                .orderBy(getOrders(condition.getSort()))
                .offset(condition.offset())
                .limit(condition.pageSize());
    }

    private JPAQuery<?> withBaseCondition(VodSearchCondition condition) {
        return repository.query(query -> query
                .from(vod)
                .where(
                        searchTitle(condition.keyword()),
                        createdAtAfter(condition.startAt()),
                        createdAtBefore(condition.endAt()),
                        isReported(condition.isReported()),
                        isVisible(condition.isVisible()),
                        eqStatus(condition.status())
                ));
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

    private BooleanExpression isReported(Boolean isReported) {
        if (isReported == null) {
            return null;
        }
        return isReported ? vod.reportCount.goe(1) : vod.reportCount.eq(0);
    }

    private BooleanExpression isVisible(Boolean isVisible) {
        if (isVisible == null) {
            return null;
        }
        return isVisible ? vod.isVisible.isTrue() : vod.isVisible.isFalse();
    }

    private BooleanExpression searchTitle(String keyword) {
        return keyword == null ? null : vod.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression createdAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : vod.createdAt.goe(dateTime);
    }

    private BooleanExpression createdAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : vod.createdAt.loe(dateTime);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId == null ? null : vod.memberId.eq(memberId);
    }

    private BooleanExpression eqStatus(VodStatus status) {
        return status == null ? null : vod.status.eq(status);
    }

    private static BooleanExpression eqId(Long vodId) {
        return vodId == null ? null : vod.id.eq(vodId);
    }
}
