package com.jocoos.mybeautip.domain.search.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResult<T> {
    private final List<T> searchResults;
    private final Long count;
}
