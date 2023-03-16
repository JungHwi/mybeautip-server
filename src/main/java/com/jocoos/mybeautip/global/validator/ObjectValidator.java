package com.jocoos.mybeautip.global.validator;

import lombok.NoArgsConstructor;

import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ObjectValidator {

    public static void requireNonNull(Object object, String variableName) {
        Objects.requireNonNull(object, variableName + " must not be null");
    }
}
