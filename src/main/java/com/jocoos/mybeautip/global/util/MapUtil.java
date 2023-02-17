package com.jocoos.mybeautip.global.util;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MapUtil {

    public static <T, R> Map<T, R> mapFrom(Collection<T> keys,
                                           Function<T, Long> valueIdExtractorFromKey,
                                           Function<Set<Long>, List<R>> getValuesByIdsFunction,
                                           Function<R, Long> keyIdExtractorFromValue) {
        List<R> values = getValues(keys, valueIdExtractorFromKey, getValuesByIdsFunction);
        Map<Long, R> valueMap = values.stream().collect(toMap(keyIdExtractorFromValue, x -> x));
        return toKeyValueMap(keys, valueMap, valueIdExtractorFromKey::apply, null);
    }

    public static <T, R> Map<T, List<R>> listValueMapFrom(Collection<T> keys,
                                                          Function<T, Long> valueIdExtractorFromKey,
                                                          Function<Set<Long>, List<R>> getValuesByIdsFunction,
                                                          Function<R, Long> keyIdExtractorFromValue) {
        List<R> values = getValues(keys, valueIdExtractorFromKey, getValuesByIdsFunction);
        Map<Long, List<R>> valueMap = values.stream().collect(groupingBy(keyIdExtractorFromValue));
        return toKeyValueMap(keys, valueMap, valueIdExtractorFromKey::apply, List.of());
    }

    private static <T, R> List<R> getValues(Collection<T> keys,
                                            Function<T, Long> valueIdExtractorFromKey,
                                            Function<Set<Long>, List<R>> getValuesByIdsFunction) {
        Set<Long> ids = getIds(keys, valueIdExtractorFromKey);
        return getValuesByIdsFunction.apply(ids);
    }


    private static <T> Set<Long> getIds(Collection<T> iterable,
                                        Function<T, Long> longExtractor) {
        return iterable.stream().map(longExtractor).collect(toSet());
    }

    private static <T, R> Map<T, R> toKeyValueMap(Collection<T> keys,
                                                  Map<Long, R> valueMap,
                                                  ToLongFunction<T> valueMapKeyFunction,
                                                  R defaultValue) {
        return keys.stream()
                .collect(
                        toMap(
                                x -> x,
                                x -> valueMap.getOrDefault(valueMapKeyFunction.applyAsLong(x), defaultValue)
                        ));
    }
}
