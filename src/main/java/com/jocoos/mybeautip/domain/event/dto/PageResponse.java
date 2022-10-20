package com.jocoos.mybeautip.domain.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PageResponse<T> {
    private final Long total;
    private final List<T> content;
}
