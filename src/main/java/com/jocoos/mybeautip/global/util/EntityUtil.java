package com.jocoos.mybeautip.global.util;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class EntityUtil {

    public static <T>  Map<Long, T> getIdEntityMap(List<T> entities, ToLongFunction<T> getIdFunc) {
        return entities.stream()
                .collect(Collectors.toMap(getIdFunc::applyAsLong, x -> x));
    }
}
