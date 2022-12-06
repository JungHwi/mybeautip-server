package com.jocoos.mybeautip.global.wrapper;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record PageResponse<T>(Long total,
                              List<T> content) {

    public PageResponse(Page<T> result) {
        this(result.getTotalElements(), result.isEmpty() ? new ArrayList<>() : result.getContent());
    }
}
