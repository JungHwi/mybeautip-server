package com.jocoos.mybeautip.domain.operation.persistence.repository.log;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;

import java.util.List;
import java.util.Set;

public interface OperationLogCustomRepository {

    List<OperationLog> getOperationLogList(Set<OperationType>)
}
