package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeSearchField;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicySearchRequest;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.vo.DeliveryFeePolicySearchResult;
import com.jocoos.mybeautip.domain.delivery.vo.QDeliveryFeePolicyDetailSearchResult;
import com.jocoos.mybeautip.domain.delivery.vo.QDeliveryFeePolicySearchResult;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.tika.utils.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.company.persistence.domain.QCompany.company;
import static com.jocoos.mybeautip.domain.delivery.persistence.domain.QDeliveryFeePolicy.deliveryFeePolicy;
import static com.jocoos.mybeautip.domain.delivery.persistence.domain.QDeliveryFeePolicyDetail.deliveryFeePolicyDetail;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static java.util.stream.Collectors.toList;

public class DeliveryFeePolicyCustomRepositoryImpl implements DeliveryFeePolicyCustomRepository {

    private final ExtendedQuerydslJpaRepository<DeliveryFeePolicy, Long> repository;

    public DeliveryFeePolicyCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<DeliveryFeePolicy, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<DeliveryFeePolicySearchResult> search(DeliveryFeePolicySearchRequest request) {

        // FIXME .limit((long) request.pageable().getPageSize() * CountryCode.values().length)
        // FIXME polish 와 detail 테이블을 각각 조회하여 조합하도록 수정해야 할수도 있음. 현재는 각 국가 정보를 모두 추가해야 하므로 임시로 PageSize 를 연산하여 사용.
        Map<Long, DeliveryFeePolicySearchResult> resultMap = whereCondition(request)
                .from(deliveryFeePolicy)
                .innerJoin(deliveryFeePolicyDetail).on(deliveryFeePolicy.id.eq(deliveryFeePolicyDetail.deliveryFeePolicy.id))
                .innerJoin(company).on(deliveryFeePolicy.company.id.eq(company.id))
                .offset(request.pageable().getOffset())
                .limit((long) request.pageable().getPageSize() * CountryCode.values().length)
                .orderBy(deliveryFeePolicy.id.desc())
                .transform(groupBy(deliveryFeePolicy.id).as(new QDeliveryFeePolicySearchResult(
                        deliveryFeePolicy,
                        company,
                        set(new QDeliveryFeePolicyDetailSearchResult(deliveryFeePolicyDetail))
                )));

        List<DeliveryFeePolicySearchResult> result = resultMap.keySet().stream()
                .map(resultMap::get)
                .collect(toList());

        JPAQuery<Long> countQuery = whereCondition(request)
                .select(deliveryFeePolicy.count())
                .from(deliveryFeePolicy);

        return PageableExecutionUtils.getPage(result, request.pageable(), countQuery::fetchOne);
    }

    private JPAQuery<?> whereCondition(DeliveryFeePolicySearchRequest request) {
        return repository.query(query -> query
                .where(
                        searchText(request.searchField(), request.searchText()),
                        eqType(request.type())
                )
        );
    }

    private BooleanExpression eqType(DeliveryFeeType type) {
        return type == null ? null : deliveryFeePolicy.type.eq(type);
    }

    private BooleanExpression searchText(DeliveryFeeSearchField searchField, String searchText) {
        if (StringUtils.isEmpty(searchText)) {
            return null;
        }

        return switch (searchField) {
            case DELIVERY_FEE_NAME -> deliveryFeePolicy.name.contains(searchText);
            case DELIVERY_FEE_CODE -> deliveryFeePolicy.code.contains(searchText);
            case COMPANY_NAME -> deliveryFeePolicy.company.name.contains(searchText);
            case COMPANY_CODE -> deliveryFeePolicy.company.code.contains(searchText);
        };
    }
}
