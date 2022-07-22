package com.jocoos.mybeautip.domain.term.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermStatus implements CodeValue {
    REQUIRED("필수"),
    OPTIONAL("선택"),
    GUIDANCE("읽기"),
    DELETE("삭제");

    private final String description;
}
