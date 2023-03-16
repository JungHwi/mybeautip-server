package com.jocoos.mybeautip.domain.vod.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public record PagingFilter(int page, int size, Direction order, String sort) {
    public Pageable pageable() {
        return PageRequest.of(page - 1, size, order, sort, "id");
    }
}
