package com.jocoos.mybeautip.domain.search.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.service.CommunityConvertService;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.service.VideoConvertService;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final CommunityDao communityDao;
    private final VideoDao videoDao;
    private final CommunityConvertService communityConvertService;
    private final VideoConvertService videoConvertService;

    @Transactional(readOnly = true)
    public SearchResponse<?> search(SearchType type, KeywordSearchCondition condition, Member member) {
        if (SearchType.COMMUNITY.equals(type)) {
            return searchCommunity(condition, member);
        }
        return searchVideo(condition);
    }

    private SearchResponse<CommunityResponse> searchCommunity(KeywordSearchCondition condition, Member member) {
        SearchResult<Community> result = communityDao.search(condition);
        List<CommunityResponse> responses = communityConvertService.toResponse(member, result.getSearchResults());
        return new SearchResponse<>(responses, result.getCount()).contentJsonNameCommunity();
    }

    private SearchResponse<VideoResponse> searchVideo(KeywordSearchCondition condition) {
        SearchResult<Video> result = videoDao.search(condition);
        List<VideoResponse> responses = videoConvertService.toResponses(result.getSearchResults());
        return new SearchResponse<>(responses, result.getCount()).contentJsonNameVideo();
    }
}
