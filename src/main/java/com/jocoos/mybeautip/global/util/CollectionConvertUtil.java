package com.jocoos.mybeautip.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionConvertUtil {

    public static <T, K, V> Map<K, V> toMap(Collection<V> targets, Function<V, K> keyFunc) {
        return targets.stream().collect(Collectors.toMap(keyFunc, target -> target));
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> targets, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return targets.stream().collect(Collectors.toMap(keyFunc, valueFunc));
    }
}
