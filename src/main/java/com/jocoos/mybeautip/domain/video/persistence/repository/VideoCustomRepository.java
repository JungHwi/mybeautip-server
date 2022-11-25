package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.video.Video;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VideoCustomRepository {
    Page<AdminVideoResponse> getVideos(AdminVideoSearchCondition condition);

    SearchResult<Video> search(VideoSearchCondition condition);
    Long countBy(String keyword);
    List<Video> getVideos(VideoSearchCondition condition);
}
