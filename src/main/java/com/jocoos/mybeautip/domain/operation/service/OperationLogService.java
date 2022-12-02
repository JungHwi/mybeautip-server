package com.jocoos.mybeautip.domain.operation.service;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.converter.OperationLogConverter;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogResponse;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogDao dao;
    private final OperationLogConverter converter;

    public List<OperationLogResponse> getOperationLogs(Set<OperationType> typeList, long memberId, Pageable pageable) {
        List<OperationLog> operationLogList = dao.getOperationLogList(typeList, memberId, pageable);

        return converter.converts(operationLogList);
    }
}
