package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.notification.service.impl.VideoUploadNotificationService;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminVideoService {

    private final VideoDao videoDao;
    private final VideoCommentDeleteService commentDeleteService;
    private final SlackService slackService;
    private final VideoUploadNotificationService notificationService;

    @Transactional(readOnly = true)
    public PageResponse<AdminVideoResponse> getVideos(AdminVideoSearchCondition condition) {
        Page<AdminVideoResponse> page = videoDao.getVideos(condition);
        return new PageResponse<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public AdminVideoResponse getVideo(Long videoId) {
        Video video = videoDao.getVideo(videoId);
        return new AdminVideoResponse(video, video.getMember(), video.getCategories());
    }

    @Transactional
    public Long changeVisibility(Long videoId, boolean isVisible) {
        Video video = videoDao.getVideo(videoId);
        video.visible(isVisible);
        commentDeleteService.hide(videoId, isVisible);
        sendMessages(video);
        return video.getId();
    }

    @Transactional
    public Long delete(Long videoId) {
        Video video = videoDao.getVideo(videoId);
        video.delete();
        commentDeleteService.delete(videoId);
        return video.getId();
    }

    @Transactional
    public Long topFix(Long videoId, boolean isTopFix) {
        Video video = videoDao.getVideo(videoId);
        fixAndChangeSortOrder(video, isTopFix);
        return video.getId();
    }

    @Transactional
    public Long recommend(Long videoId, boolean isRecommended) {
        Video video = videoDao.getVideo(videoId);
        video.recommend(isRecommended);
        return video.getId();
    }

    @Transactional
    public List<Long> arrange(List<Long> sortedIds) {
        return videoDao.arrangeByIndex(sortedIds);
    }

    private void fixAndChangeSortOrder(Video video, boolean isTopFix) {
        if (isTopFix) {
            video.validNotDelete();
            videoDao.fixAndAddToLastOrder(video.getId());
        } else {
            videoDao.unFixAndSortingToNull(video.getId());
        }
    }

    private void sendMessages(Video video) {
        if (video.isOpenAndVisible()) {
            // aop 도입 고려 (slack 및 notification 공통화)
            slackService.makeVideoPublic(video);
            notificationService.send(video);
        }
    }
}
