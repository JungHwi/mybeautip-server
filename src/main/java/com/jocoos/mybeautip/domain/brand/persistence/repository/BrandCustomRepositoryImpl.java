package com.jocoos.mybeautip.domain.brand.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.brand.code.BrandStatus;
import com.jocoos.mybeautip.domain.brand.dto.BrandSearchRequest;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.util.QuerydslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.brand.persistence.domain.QBrand.brand;

@Repository
public class BrandCustomRepositoryImpl implements BrandCustomRepository {

    private final ExtendedQuerydslJpaRepository<Brand, Long> repository;

    public BrandCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Brand, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<Brand> search(BrandSearchRequest request) {
        List<Brand> brands = withBaseConditionAndSort(request)
                .select(brand)
                .fetch();

        JPAQuery<Long> countQuery = withBaseCondition(request)
                .select(brand.count());

        return PageableExecutionUtils.getPage(brands, request.getPageable(), countQuery::fetchOne);
    }

    private long count(BrandSearchRequest request) {
        Long count = withBaseCondition(request)
                .select(brand.count())
                .fetchOne();

        return count == null ? 0 : count;
    }

    private JPAQuery<?> withBaseConditionAndSort(BrandSearchRequest condition) {
        return withBaseCondition(condition)
                .orderBy(getOrders(condition.getPageable().getSort()))
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize());
    }

    private JPAQuery<?> withBaseCondition(BrandSearchRequest condition) {
        return repository.query(query -> query
                .from(brand)
                .where(
                        searchField(condition.getSearchField(), condition.getKeyword()),
                        eqStatus(condition.getStatus())
                ));
    }

    private BooleanExpression searchField(String field, String keyword) {
        if (StringUtils.isBlank(field)) {
            return null;
        }

        return switch (field.toString()) {
            case "NAME" -> keyword == null ? null : brand.name.containsIgnoreCase(keyword);
            case "CODE" -> keyword == null ? null : brand.code.containsIgnoreCase(keyword);
            default -> throw new BadRequestException("searchField can only [NAME][CODE]");
        };
    }

    private BooleanExpression eqStatus(BrandStatus status) {
        return status == null ? null : brand.status.eq(status);
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrders(sort, Brand.class, brand);
        return orderSpecifiers.length == 0
                ? new OrderSpecifier[]{
                brand.id.desc()}
                : orderSpecifiers;
    }
}
