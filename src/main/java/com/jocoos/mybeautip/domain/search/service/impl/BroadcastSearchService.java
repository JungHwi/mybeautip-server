package com.jocoos.mybeautip.domain.search.service.impl;

import com.jocoos.mybeautip.domain.broadcast.converter.BroadcastConverter;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastListResponse;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastRelationInfo;
import com.jocoos.mybeautip.domain.broadcast.service.child.BroadcastRelationService;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastDao;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.DomainSearchService;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField.SORTED_STATUS;
import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus.DEFAULT_SEARCH_STATUSES;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RequiredArgsConstructor
@Service
public class BroadcastSearchService implements DomainSearchService<BroadcastListResponse> {

    private final BroadcastDao broadcastDao;
    private final BroadcastRelationService relationService;
    private final BroadcastConverter converter;

    @Override
    public SearchType getType() {
        return SearchType.BROADCAST;
    }

    // 2023-03-28
    // Broadcast Currently Supports Only Title For Search So In BroadcastRepositoryCustomImpl It Hardcoded Field Title
    // In Future If Search Field Added Change Search.onlyKeyword()
    @Override
    public SearchResponse<BroadcastListResponse> search(KeywordSearchRequest request) {

        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .searchOption(SearchOption.onlyKeyword(request.keyword()))
                .statuses(DEFAULT_SEARCH_STATUSES)
                .cursor(request.idCursor())
                .pageable(PageRequest.of(0, request.size(), SORTED_STATUS.getSort(ASC)))
                .build();

        Page<BroadcastSearchResult> results = broadcastDao.getPageList(condition);

        List<BroadcastSearchResult> contents = results.getContent();
        Map<Long, BroadcastRelationInfo> relationInfoMap = relationService.getRelationInfoMap(
                request.tokenUsername(),
                contents);
        List<BroadcastListResponse> responses = converter.toListResponse(contents, relationInfoMap);
        return new SearchResponse<>(responses, results.getTotalElements()).contentJsonNameTo(getType());
    }

    @Override
    public long count(String keyword, Member member) {
        BroadcastSearchCondition condition = BroadcastSearchCondition.builder()
                .searchOption(SearchOption.onlyKeyword(keyword))
                .statuses(DEFAULT_SEARCH_STATUSES)
                .build();
        return broadcastDao.count(condition);
    }
}
