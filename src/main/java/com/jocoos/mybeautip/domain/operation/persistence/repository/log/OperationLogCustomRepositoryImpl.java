package com.jocoos.mybeautip.domain.operation.persistence.repository.log;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogSearchCondition;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.domain.operation.persistence.domain.QOperationLog.operationLog;
import static com.querydsl.sql.SQLExpressions.count;

@Repository
public class OperationLogCustomRepositoryImpl implements OperationLogCustomRepository {

    private final ExtendedQuerydslJpaRepository<OperationLog, Long> repository;

    public OperationLogCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<OperationLog, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<OperationLog> findByLogs(OperationLogSearchCondition condition) {
        boolean direction = isIdAscending(condition.getPageable());

        List<OperationLog> operationLogList = baseQueryForLogs(condition.getTypes(), condition.getTargetId())
                .orderBy(orderById(direction))
                .offset(condition.getPageable().getOffset())
                .limit(condition.getPageable().getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = baseQueryForLogs(condition.getTypes(), condition.getTargetId())
                .select(count(operationLog));

        return PageableExecutionUtils.getPage(operationLogList, condition.getPageable(), countQuery::fetchOne);

    }

    private JPAQuery<OperationLog> baseQueryForLogs(Set<OperationType> types, String targetId) {
        return repository.query(query -> query
                .select(operationLog)
                .from(operationLog)
                .where(
                        inType(types),
                        eqTargetId(targetId)
                ));
    }

    private BooleanExpression inType(Set<OperationType> types) {
        return operationLog.operationType.in(types);
    }

    private BooleanExpression eqTargetId(String targetId) {
        return StringUtils.isEmpty(targetId) ? null : operationLog.targetId.eq(targetId);
    }

    private OrderSpecifier<?> orderById(boolean isAscending) {
        return isAscending ? operationLog.id.asc() : operationLog.id.desc();
    }

    private boolean isIdAscending(Pageable pageable) {
        final String property = "id";
        return pageable.getSort()
                .stream()
                .filter(order -> property.equals(order.getProperty()))
                .anyMatch(Sort.Order::isAscending);
    }
}
