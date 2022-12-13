package com.jocoos.mybeautip.domain.operation.service.dao;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.member.Member;

public interface OperationLogInterface {

    OperationType getOperationType();

    String getTargetId();

    String getDescription();

    Member getCreatedBy();
}
