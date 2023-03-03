package com.jocoos.mybeautip.domain.operation.persistence.repository.log;

import com.jocoos.mybeautip.domain.operation.dto.OperationLogSearchCondition;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import org.springframework.data.domain.Page;

public interface OperationLogCustomRepository {
    Page<OperationLog> findByLogs(OperationLogSearchCondition condition);
}
