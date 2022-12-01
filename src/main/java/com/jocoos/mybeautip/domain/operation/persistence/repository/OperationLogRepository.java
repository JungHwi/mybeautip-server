package com.jocoos.mybeautip.domain.operation.persistence.repository;

import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

public interface OperationLogRepository extends DefaultJpaRepository<OperationLog, Long> {
}
