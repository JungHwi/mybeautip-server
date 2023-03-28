package com.jocoos.mybeautip.domain.search.service;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;

import javax.annotation.Nullable;


public interface DomainSearchService<T extends CursorInterface > {
    SearchType getType();
    SearchResponse<T> search(KeywordSearchRequest condition);
    long count(String keyword, @Nullable Member member);
}
