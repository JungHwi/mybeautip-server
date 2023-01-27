package com.jocoos.mybeautip.domain.operation.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.persistence.repository.log.OperationLogCustomRepository;

public interface OperationLogRepository extends ExtendedQuerydslJpaRepository<OperationLog, Long>, OperationLogCustomRepository {
    void deleteByTargetId(String memberId);
}
