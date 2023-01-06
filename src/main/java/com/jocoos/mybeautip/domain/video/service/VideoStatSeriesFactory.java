package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.service.dao.VideoViewDao;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.util.MemberUtil.getCurrentMember;
import static com.jocoos.mybeautip.global.util.MemberUtil.isGuest;

@RequiredArgsConstructor
@Component
public class VideoStatSeriesFactory {

    private final VideoViewDao videoViewDao;

    @Transactional
    public void addViewCount(Video video, String username) {
        if (isGuest(username)) {
            videoViewDao.save(video, username);
        } else {
            videoViewDao.addViewCountIfPresentOrSave(video, getCurrentMember());
        }
    }
}
