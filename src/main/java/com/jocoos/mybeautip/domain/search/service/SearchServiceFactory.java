package com.jocoos.mybeautip.domain.search.service;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.global.wrapper.CursorInterface;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SearchServiceFactory {

    private final Map<SearchType, DomainSearchService<?>> domainSearchServiceMap;


    public SearchServiceFactory(List<DomainSearchService<?>> domainSearchServices) {
        this.domainSearchServiceMap = domainSearchServices.stream()
                .collect(Collectors.toMap(DomainSearchService::getType, domainSearchService -> domainSearchService));
    }

    @SuppressWarnings("unchecked")
    public <T extends CursorInterface> DomainSearchService<T> get(SearchType type) {
        return (DomainSearchService<T>) domainSearchServiceMap.get(type);
    }

    public Collection<DomainSearchService<?>> getAll() {
        return domainSearchServiceMap.values();
    }
}
