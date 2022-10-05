package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.video.Video;

public interface VideoCustomRepository {
    SearchResult<Video> search(KeywordSearchCondition condition);
    Long countBy(String keyword);
}
