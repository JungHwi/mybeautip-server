package com.jocoos.mybeautip.domain.company.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessPermission implements CodeValue {

    ADMINISTRATOR("관리자 승인"),
    AUTO("자동 승인");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
