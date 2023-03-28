package com.jocoos.mybeautip.domain.search.service;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.CountResponse;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final SearchServiceFactory searchServiceFactory;

    @Transactional(readOnly = true)
    public <T extends CursorInterface> SearchResponse<T> search(SearchType type, KeywordSearchRequest condition) {
        DomainSearchService<T> searchService = searchServiceFactory.get(type);
        return searchService.search(condition);
    }

    @Transactional(readOnly = true)
    public <T extends CursorInterface> CountResponse count(SearchType type,
                                                           String keyword,
                                                           Member member) {
        if (type == SearchType.ALL) {
            return new CountResponse(countAll(keyword, member));
        }

        DomainSearchService<T> searchService = searchServiceFactory.get(type);
        return new CountResponse(searchService.count(keyword, member));
    }

    private long countAll(String keyword, Member member) {
        return searchServiceFactory.getAll()
                .stream()
                .mapToLong(service -> service.count(keyword, member))
                .sum();
    }
}
