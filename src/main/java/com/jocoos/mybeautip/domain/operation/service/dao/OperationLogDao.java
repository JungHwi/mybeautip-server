package com.jocoos.mybeautip.domain.operation.service.dao;

import com.jocoos.mybeautip.domain.operation.converter.OperationLogConverter;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogRequest;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.persistence.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationLogDao {

    private final OperationLogRepository repository;
    private final OperationLogConverter converter;

    @Transactional
    public OperationLog logging(OperationLogRequest request) {
        OperationLog operationLog = converter.convertsToEntity(request);
        return repository.save(operationLog);
    }
}
