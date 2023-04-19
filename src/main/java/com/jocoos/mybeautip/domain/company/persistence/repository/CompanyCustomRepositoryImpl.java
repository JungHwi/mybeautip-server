package com.jocoos.mybeautip.domain.company.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.dto.CompanySearchRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.global.util.QuerydslUtil;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.domain.company.persistence.domain.QCompany.company;

@Repository
public class CompanyCustomRepositoryImpl implements CompanyCustomRepository {
    private final ExtendedQuerydslJpaRepository<Company, Long> repository;

    public CompanyCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Company, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<Company> search(CompanySearchRequest request) {
        List<Company> companies = withBaseConditionAndSort(request)
                .select(company)
                .fetch();

        JPAQuery<Long> countQuery = withBaseCondition(request)
                .select(company.count());

        return PageableExecutionUtils.getPage(companies, request.pageable(), countQuery::fetchOne);
    }

    private long count(CompanySearchRequest request) {
        Long count = withBaseCondition(request)
                .select(company.count())
                .fetchOne();

        return count == null ? 0 : count;
    }

    private JPAQuery<?> withBaseConditionAndSort(CompanySearchRequest condition) {
        return withBaseCondition(condition)
                .orderBy(getOrders(condition.pageable().getSort()))
                .offset(condition.pageable().getOffset())
                .limit(condition.pageable().getPageSize());
    }

    private JPAQuery<?> withBaseCondition(CompanySearchRequest condition) {
        return repository.query(query -> query
                .from(company)
                .where(
                        searchName(condition.name()),
                        inStatus(condition.status())
                ));
    }

    private BooleanExpression searchName(String keyword) {
        return keyword == null ? null : company.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression inStatus(Set<CompanyStatus> status) {
        return status == null ? null : company.status.in(status);
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        OrderSpecifier<?>[] orderSpecifiers = QuerydslUtil.getOrders(sort, Company.class, company);
        return orderSpecifiers.length == 0
                ? new OrderSpecifier[]{
                company.id.desc()}
                : orderSpecifiers;
    }
}
