package com.jocoos.mybeautip.domain.operation.dto;

import com.jocoos.mybeautip.domain.operation.code.OperationTargetType;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogRequest {

    private OperationTargetType targetType;

    private OperationType operationType;

    private String targetId;

    private String description;

    private long createdBy;
}
