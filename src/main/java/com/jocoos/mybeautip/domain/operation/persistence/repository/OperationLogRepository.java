package com.jocoos.mybeautip.domain.operation.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface OperationLogRepository extends ExtendedQuerydslJpaRepository<OperationLog, Long>, OperationLogCustomRepository {

    List<OperationLog> findBy (Set<OperationType> type, String targetId, Pageable pageable);
}
