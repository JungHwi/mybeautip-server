package com.jocoos.mybeautip.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonNullableUtils {

    public static <T> void changeIfPresent(JsonNullable<T> nullable, Consumer<T> consumer) {
        if (nullable.isPresent()) {
            consumer.accept(nullable.get());
        }
    }

    public static <T> T getIfPresent(JsonNullable<T> nullable, T originalValue) {
        if (nullable.isPresent()) {
            return nullable.get();
        }
        return originalValue;
    }
}
