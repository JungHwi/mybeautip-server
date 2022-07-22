package com.jocoos.mybeautip.domain.term.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermUsedType {
    SIGNUP("회원가입");

    private final String description;
}
