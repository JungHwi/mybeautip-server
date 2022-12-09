package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.notification.service.impl.VideoUploadNotificationService;
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
    public long openVideos() {
        List<Video> videos = videoDao.findVideosToOpen();
        long openVideoCount = videoDao.openVideos(videos);
        sendMessages(videos);
        return openVideoCount;
    }

    private void sendMessages(List<Video> videos) {
        for (Video video : videos) {
            slackService.makeVideoPublic(video);
            notificationService.send(video);
        }
    }
}
