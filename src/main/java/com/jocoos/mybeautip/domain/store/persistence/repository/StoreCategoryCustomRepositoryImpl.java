package com.jocoos.mybeautip.domain.store.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.store.code.StoreCategoryStatus;
import com.jocoos.mybeautip.domain.store.dto.QStoreCategoryListResponse;
import com.jocoos.mybeautip.domain.store.dto.SearchStoreCategoryRequest;
import com.jocoos.mybeautip.domain.store.dto.StoreCategoryListResponse;
import com.jocoos.mybeautip.domain.store.persistence.domain.StoreCategory;
import com.jocoos.mybeautip.global.util.QuerydslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.jocoos.mybeautip.domain.store.persistence.domain.QStore.store;
import static com.jocoos.mybeautip.domain.store.persistence.domain.QStoreCategory.storeCategory;

@Repository
public class StoreCategoryCustomRepositoryImpl implements StoreCategoryCustomRepository {

    private final ExtendedQuerydslJpaRepository<StoreCategory, Long> repository;

    public StoreCategoryCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<StoreCategory, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<StoreCategoryListResponse> search(SearchStoreCategoryRequest request) {
        List<StoreCategoryListResponse> responses = withBaseConditionAndSort(request)
                .fetch();

        JPAQuery<Long> countQuery = withBaseCondition(request)
                .select(storeCategory.count());

        return PageableExecutionUtils.getPage(responses, request.pageable(), countQuery::fetchOne);
    }

    private JPAQuery<StoreCategoryListResponse> withBaseConditionAndSort(SearchStoreCategoryRequest condition) {
        return withBaseCondition(condition)
                .orderBy(getOrders(condition.pageable().getSort()))
                .offset(condition.pageable().getOffset())
                .limit(condition.pageable().getPageSize());
    }

    private JPAQuery<StoreCategoryListResponse> withBaseCondition(SearchStoreCategoryRequest condition) {
        return repository.query(query -> query
                .select(new QStoreCategoryListResponse(storeCategory, store.count()))
                .from(storeCategory).leftJoin(store).on(storeCategory.id.eq(store.category.id))
                .where(
                        inStatus(condition.statuses())
                )
                .groupBy(storeCategory));
    }

    private BooleanExpression inStatus(List<StoreCategoryStatus> statuses) {
        return CollectionUtils.isEmpty(statuses) ? null : storeCategory.status.in(statuses);
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrders(sort, StoreCategory.class, storeCategory);
        return orderSpecifiers.length == 0
                ? new OrderSpecifier[]{
                storeCategory.sort.asc()}
                : orderSpecifiers;
    }
}
