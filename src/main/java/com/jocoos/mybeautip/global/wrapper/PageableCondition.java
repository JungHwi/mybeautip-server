package com.jocoos.mybeautip.global.wrapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuperBuilder
public class PageableCondition {

    @Getter
    protected Pageable pageable;

    public PageableCondition(int page, int size) {
        this.pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
    }

    public PageableCondition(int page, int size, Sort sort) {
        this.pageable = PageRequest.of(page - 1, size, sort);
    }

    public PageableCondition(Pageable pageable) {
        this.pageable = pageable;
    }
}
