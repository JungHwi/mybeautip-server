package com.jocoos.mybeautip.domain.search.service.impl;

import com.jocoos.mybeautip.domain.search.code.SearchType;
import com.jocoos.mybeautip.domain.search.dto.SearchResponse;
import com.jocoos.mybeautip.domain.search.service.DomainSearchService;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchRequest;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.service.VideoConvertService;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VideoSearchService implements DomainSearchService<VideoResponse> {

    private final VideoDao videoDao;
    private final VideoConvertService convertService;

    @Override
    public SearchType getType() {
        return SearchType.VIDEO;
    }

    @Override
    public SearchResponse<VideoResponse> search(KeywordSearchRequest condition) {
        SearchResult<Video> result = videoDao.search(VideoSearchCondition.from(condition));
        List<VideoResponse> responses = convertService.toResponses(result.getSearchResults());
        return new SearchResponse<>(responses, result.getCount()).contentJsonNameTo(getType());
    }

    @Override
    public long count(String keyword, Member member) {
        return videoDao.count(keyword);
    }
}
