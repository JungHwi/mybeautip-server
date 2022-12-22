package com.jocoos.mybeautip.domain.operation.service;

import com.jocoos.mybeautip.domain.operation.converter.OperationLogConverter;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogResponse;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogSearchCondition;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogDao dao;
    private final OperationLogConverter converter;

    public PageResponse<OperationLogResponse> getOperationLogs(OperationLogSearchCondition condition) {
        Page<OperationLog> operationLogList = dao.getOperationLogList(condition);
        return converter.converts(operationLogList);
    }
}
