package com.jocoos.mybeautip.domain.operation.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationType implements CodeValue {
    MEMBER_SUSPENDED("회원 정지", OperationTargetType.MEMBER),
    MEMBER_SUSPENDED_OFF("회원 정지 해제", OperationTargetType.MEMBER),
    MEMBER_EXILE("회원 추방", OperationTargetType.MEMBER);

    private final String description;
    private final OperationTargetType targetType;

    @Override
    public String getName() {
        return this.name();
    }
}
