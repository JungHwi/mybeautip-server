package com.jocoos.mybeautip.domain.operation.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperationTargetType implements CodeValue {
    MEMBER("회원 정보");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
