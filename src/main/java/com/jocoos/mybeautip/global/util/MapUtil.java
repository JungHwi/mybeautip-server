package com.jocoos.mybeautip.global.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {

    public static <T, R> Map<T, R> toIdentityMap(Collection<T> identities, Function<T, R> valueMapper) {
        return identities.stream().collect(Collectors.toMap(x -> x, valueMapper));
    }

    public static <T, R> Map<R, T> toMap(Collection<T> identities, Function<T, R> keyMapper) {
        return identities.stream().collect(Collectors.toMap(keyMapper, x -> x));
    }
}
