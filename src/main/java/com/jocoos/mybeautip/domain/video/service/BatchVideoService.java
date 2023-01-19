package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.notification.aspect.service.impl.VideoUploadNotificationService;
import com.jocoos.mybeautip.domain.video.dto.VideoOpenBatchResult;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BatchVideoService {

    private final VideoDao videoDao;
    private final SlackService slackService;
    private final VideoUploadNotificationService notificationService;

    @Transactional
    public VideoOpenBatchResult openVideos() {
        List<Video> videos = videoDao.findVideosToOpen();
        videoDao.openVideos(videos);

        List<Video> publicVideos = getOpenAndPublicVideos(videos);
        sendMessages(publicVideos);
        int privateVideoCount = videos.size() - publicVideos.size();

        return new VideoOpenBatchResult(publicVideos.size(), privateVideoCount);
    }

    private List<Video> getOpenAndPublicVideos(List<Video> videos) {
        return videos.stream().filter(Video::isPublic).toList();
    }

    private void sendMessages(List<Video> videos) {
        for (Video video : videos) {
            slackService.makeVideoPublic(video);
            notificationService.send(video);
        }
    }
}
