package com.jocoos.mybeautip.domain.search.service.impl;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.CommunityConvertService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.DomainSearchService;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommunitySearchService implements DomainSearchService<CommunityResponse> {

    private final CommunityDao communityDao;
    private final CommunityConvertService convertService;

    @Override
    public SearchType getType() {
        return SearchType.COMMUNITY;
    }

    @Override
    public SearchResponse<CommunityResponse> search(KeywordSearchRequest condition) {
        SearchResult<Community> result = communityDao.search(condition);
        List<CommunityResponse> responses = convertService.toResponse(condition.member(), result.getSearchResults());
        return new SearchResponse<>(responses, result.getCount()).contentJsonNameTo(getType());
    }

    @Override
    public long count(String keyword, Member member) {
        Long memberId = member == null ? null : member.getId();
        return communityDao.countBy(keyword, memberId);
    }
}
