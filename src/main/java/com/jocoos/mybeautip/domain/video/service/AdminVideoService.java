package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.service.dao.VideoDao;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminVideoService {

    private final VideoDao videoDao;
    private final VideoCommentDeleteService commentDeleteService;

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
    public Long hide(Long videoId, boolean isHide) {
        Video video = videoDao.getVideo(videoId);
        video.hide(isHide);
        commentDeleteService.hide(videoId, isHide);
        return video.getId();
    }

    @Transactional
    public Long delete(Long videoId) {
        Video video = videoDao.getVideo(videoId);
        video.delete();
        commentDeleteService.delete(videoId);
        return video.getId();
    }
}
