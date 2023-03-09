package com.jocoos.mybeautip.global.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class QuerydslUtil {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> OrderSpecifier<?>[] getOrders(Sort sort, Class<T> clazz, Path<T> parentPath) {
        return sort.stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    SimplePath<T> path = Expressions.path(clazz, parentPath, order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .toArray(OrderSpecifier[]::new);
    }
}
