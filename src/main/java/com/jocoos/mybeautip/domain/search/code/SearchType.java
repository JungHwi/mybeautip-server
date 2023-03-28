package com.jocoos.mybeautip.domain.search.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType implements CodeValue {
    COMMUNITY("커뮤니티 검색"),
    VIDEO("비디오 검색"),
    BROADCAST("방송 검색"),
    VOD("VOD 검색"),
    ALL("전체");

    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
