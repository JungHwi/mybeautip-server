package com.jocoos.mybeautip.domain.home.service.video;

import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.service.VideoConvertService;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class VideoSummary {

    private final VideoDao videoDao;
    private final VideoConvertService videoConvertService;

    @Transactional(readOnly = true)
    public List<VideoResponse> summaryVideo(int summaryVideoNum) {
        List<Video> videos = videoDao.getVideos(summaryVideoNum);
        return videoConvertService.toResponses(videos);
    }
}
