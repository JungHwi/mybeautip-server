package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.dto.VideoResponse;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoDao videoDao;
    private final VideoConvertService videoConvertService;

    @Transactional(readOnly = true)
    public List<VideoResponse> findVideos(Integer categoryId, ZonedDateTime cursor, Pageable pageable) {
        List<Video> videoList = videoDao.getAnyoneAllVideos(categoryId, cursor, pageable);
        return videoConvertService.toResponses(videoList);
    }

    @Transactional(readOnly = true)
    public VideoResponse getVideo(long videoId) {
        Video video = videoDao.getVideo(videoId);
        return videoConvertService.toResponse(video);
    }
}
