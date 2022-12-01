package com.jocoos.mybeautip.domain.operation.converter;

import com.jocoos.mybeautip.domain.operation.dto.OperationLogRequest;
import com.jocoos.mybeautip.domain.operation.persistence.domain.OperationLog;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogInterface;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OperationLogConverter {

    public abstract OperationLog convertsToEntity(OperationLogRequest request);

    public OperationLogRequest converts(OperationLogInterface request) {
        return OperationLogRequest.builder()
                .operationType(request.getOperationType())
                .targetType(request.getOperationType().getTargetType())
                .targetId(request.getTargetId())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .build();
    }
}
