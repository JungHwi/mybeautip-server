package com.jocoos.mybeautip.global.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {

    @SafeVarargs
    public static <T> Set<T> newHashSet(T... objs) {
        Set<T> set = new HashSet<T>();
        Collections.addAll(set, objs);
        return set;
    }
}
