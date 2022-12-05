package com.jocoos.mybeautip.domain.operation.dto;

import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.global.wrapper.PageableCondition;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
public class OperationLogSearchCondition extends PageableCondition {

    private Set<OperationType> types;
    private String targetId;

    public OperationLogSearchCondition(Set<OperationType> types, Long targetId, int page, int size) {
        super(page, size);
        this.types = types;
        this.targetId = String.valueOf(targetId);
    }
}
