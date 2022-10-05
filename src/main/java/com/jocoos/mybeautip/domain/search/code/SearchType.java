package com.jocoos.mybeautip.domain.search.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType implements CodeValue {
    COMMUNITY("커뮤니티 검색"),
    VIDEO("비디오 검색");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
