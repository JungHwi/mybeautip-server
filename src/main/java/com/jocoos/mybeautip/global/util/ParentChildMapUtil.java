package com.jocoos.mybeautip.global.util;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ParentChildMapUtil {

    public static <T, R> Map<T, R> parentChildMapFrom(Collection<T> parents,
                                                      Function<T, Long> childIdExtractorFromParent,
                                                      Function<Set<Long>, List<R>> getChildrenByIdFunction,
                                                      Function<R, Long> parentIdExtractorFromChild) {
        validateNonNull(parents, childIdExtractorFromParent, getChildrenByIdFunction, parentIdExtractorFromChild);
        List<R> children = getChildren(parents, childIdExtractorFromParent, getChildrenByIdFunction);
        Map<Long, R> parentIdChildMap = children.stream().collect(toMap(parentIdExtractorFromChild, child -> child));
        return toParentChildMap(parents, parentIdChildMap, childIdExtractorFromParent::apply);
    }

    public static <T, R> Map<T, List<R>> parentChildListMapFrom(Collection<T> parents,
                                                                Function<T, Long> childIdExtractorFromParent,
                                                                Function<Set<Long>, List<R>> getChildrenByIdFunction,
                                                                Function<R, Long> parentIdExtractorFromChild) {
        validateNonNull(parents, childIdExtractorFromParent, getChildrenByIdFunction, parentIdExtractorFromChild);
        List<R> children = getChildren(parents, childIdExtractorFromParent, getChildrenByIdFunction);
        Map<Long, List<R>> parentIdChildrenMap = children.stream().collect(groupingBy(parentIdExtractorFromChild));
        return toParentChildMapWithDefault(parents, parentIdChildrenMap, childIdExtractorFromParent::apply, List.of());
    }

    private static <T, R> List<R> getChildren(Collection<T> parents,
                                              Function<T, Long> childIdExtractorFromParent,
                                              Function<Set<Long>, List<R>> getChildByIdFunction) {
        Set<Long> ids = getIds(parents, childIdExtractorFromParent);
        return getChildByIdFunction.apply(ids);
    }


    private static <T> Set<Long> getIds(Collection<T> entities,
                                        Function<T, Long> idExtractor) {
        return entities.stream().map(idExtractor).collect(toSet());
    }

    private static <T, R> Map<T, R> toParentChildMapWithDefault(Collection<T> parents,
                                                     Map<Long, R> childIdChildMap,
                                                     ToLongFunction<T> childIdExtractorFromParent,
                                                     R defaultValue) {
        return toParentChildMap(
                parents,
                (T parent) -> childIdChildMap.getOrDefault(childIdExtractorFromParent.applyAsLong(parent), defaultValue));
    }

    private static <T, R> Map<T, R> toParentChildMap(Collection<T> parents,
                                                     Map<Long, R> childIdChildMap,
                                                     ToLongFunction<T> childIdExtractorFromParent) {
        return toParentChildMap(
                parents,
                (T parent) -> childIdChildMap.get(childIdExtractorFromParent.applyAsLong(parent)));
    }

    private static <T, R> Map<T, R> toParentChildMap(Collection<T> parents,
                                                     Function<T, R> valueMapper) {
        return parents.stream()
                .collect(toMap(x -> x, valueMapper));
    }

    private static void validateNonNull(Object... objects) {
        for (Object object : objects) {
            requireNonNull(object);
        }
    }
}
