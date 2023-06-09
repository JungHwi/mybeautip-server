package com.jocoos.mybeautip.domain.vod.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.vod.code.VodStatus;
import com.jocoos.mybeautip.domain.vod.dto.QVodListResponse;
import com.jocoos.mybeautip.domain.vod.dto.VodListResponse;
import com.jocoos.mybeautip.domain.vod.persistence.domain.Vod;
import com.jocoos.mybeautip.domain.vod.vo.VodSearchCondition;
import com.jocoos.mybeautip.global.code.SortField;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Collection;
import java.util.List;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastCategory.broadcastCategory;
import static com.jocoos.mybeautip.domain.vod.persistence.domain.QVod.vod;
import static com.jocoos.mybeautip.global.code.SortField.CREATED_AT;
import static com.jocoos.mybeautip.member.QMember.member;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
public class VodCustomRepositoryImpl implements VodCustomRepository {

    private final ExtendedQuerydslJpaRepository<Vod, Long> repository;

    public VodCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Vod, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<Vod> getVodPage(VodSearchCondition condition) {
        List<Vod> contents = withBaseConditionAndSort(condition)
                .select(vod)
                .fetch();

        JPAQuery<Long> countQuery = withBaseCondition(condition)
                .select(vod.count());

        return PageableExecutionUtils.getPage(contents, condition.pageable(), countQuery::fetchOne);
    }

    @Override
    public Page<VodListResponse> getPageList(VodSearchCondition condition) {
        List<VodListResponse> contents = getVodResponses(condition);
        return new PageImpl<>(contents, condition.pageable(), count(condition));
    }

    @Override
    public List<VodListResponse> getVodResponses(VodSearchCondition condition) {
        if (condition.needPagingSort()) {
            return getVodListWithPaging(condition);
        }
        JPAQuery<?> nonPagingSortQuery = withBaseCondition(condition);
        return selectVodResponse(nonPagingSortQuery, condition);

    }

    @Override
    public long count(VodSearchCondition condition) {
        Long count = withBaseCondition(condition)
                .select(vod.count())
                .fetchOne();
        return count == null ? 0 : count;
    }

    private List<VodListResponse> getVodListWithPaging(VodSearchCondition condition) {
        JPAQuery<?> query = withBaseConditionAndSort(condition);
        JPAQuery<?> pagingQuery = withCursorPaging(query, condition);
        return selectVodResponse(pagingQuery, condition);
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
                        inIds(condition.ids()),
                        eqStatus(condition.status())
                ));
    }

    private List<VodListResponse> selectVodResponse(JPAQuery<?> pagingQuery, VodSearchCondition condition) {
        return pagingQuery
                .select(new QVodListResponse(vod, broadcastCategory, member))
                .join(broadcastCategory).on(vod.category.eq(broadcastCategory))
                .join(member).on(vod.memberId.eq(member.id).and(eqMemberId(condition.memberId())))
                .fetch();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private JPAQuery<?> withCursorPaging(JPAQuery<?> query,
                                         VodSearchCondition condition) {
        PathBuilder<Vod> path = new PathBuilder<>(Vod.class, "vod");
        SortField nonUniqueSortField = condition.nonUniqueCursor();
        switch (nonUniqueSortField) {
            case TOTAL_HEART_COUNT, VIEW_COUNT -> {
                ComparablePath<Integer> comparablePath =
                        path.getComparable(nonUniqueSortField.getFieldName(), Integer.class);
                return getCursorPagingQuery(query, condition, comparablePath);
            }
            default -> {
                ComparablePath<ChronoZonedDateTime> comparablePath =
                        path.getComparable(CREATED_AT.getFieldName(), ChronoZonedDateTime.class);
                return getCursorPagingQuery(query, condition, comparablePath);
            }
        }
    }

    private <T extends Comparable<T>> JPAQuery<?> getCursorPagingQuery(JPAQuery<?> query,
                                                                       VodSearchCondition condition,
                                                                       ComparablePath<T> comparablePath) {
        T cursorValue = getCursorValue(comparablePath, condition.cursor());
        return query.where(cursor(comparablePath, cursorValue, condition.cursor()));
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

    private BooleanExpression inIds(Collection<Long> ids) {
        return isEmpty(ids) ? null : vod.id.in(ids);
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
