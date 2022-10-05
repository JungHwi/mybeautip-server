package com.jocoos.mybeautip.domain.search.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.service.CommunityConvertService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.global.wrapper.CursorResultResponse;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final CommunityDao communityDao;
    private final CommunityConvertService convertService;

    @Transactional(readOnly = true)
    public CursorResultResponse<CommunityResponse> searchCommunity(KeywordSearchCondition condition, Member member) {
        SearchResult result = communityDao.search(condition);
        List<CommunityResponse> responses = convertService.toResponse(member, result.getCommunities());
        return new CursorResultResponse<>(responses).withCount(result.getCount());
    }
}
