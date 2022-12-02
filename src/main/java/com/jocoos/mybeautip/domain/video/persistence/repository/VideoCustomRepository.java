package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.video.Video;

import java.util.List;

public interface VideoCustomRepository {
    SearchResult<Video> search(VideoSearchCondition condition);
    Long countBy(String keyword);
    List<Video> getVideos(VideoSearchCondition condition);
}
