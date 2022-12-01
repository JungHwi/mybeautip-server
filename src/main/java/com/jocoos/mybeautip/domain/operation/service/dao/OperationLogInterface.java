package com.jocoos.mybeautip.domain.operation.service.dao;

import com.jocoos.mybeautip.domain.operation.code.OperationType;

public interface OperationLogInterface {

    OperationType getOperationType();

    String getTargetId();

    String getDescription();

    long getCreatedBy();
}
