package com.jocoos.mybeautip.domain.term.code;

import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TermUsedInType {
    SIGNUP("signup");

    private final String description;


    public static TermUsedInType getBy(String s) {
        try {
            return TermUsedInType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("url param " + s + " is not valid");
        }
    }
}
