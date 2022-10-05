package com.jocoos.mybeautip.domain.search.vo;

import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResult {
    private final List<Community> communities;
    private final Long count;
}
