package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.view.VideoView;
import com.jocoos.mybeautip.video.view.VideoViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class VideoViewDao {

    private final VideoViewRepository repository;

    @Transactional
    public VideoView get(Video video, Member member) {
        return find(video, member)
                .orElseThrow(() -> new NotFoundException("video view not found, video id " + video.getId() + "member id " + member.getId()));
    }

    @Transactional
    public Optional<VideoView> find(Video video, Member member) {
        return repository.findByVideoAndCreatedBy(video, member);
    }

    @Transactional
    public VideoView save(Video video) {
        return repository.save(new VideoView(video));
    }

    @Transactional
    public VideoView save(Video video, String guestName) {
        return repository.save(new VideoView(video, guestName));
    }

    @Transactional
    public void addViewCount(VideoView view) {
        repository.addViewCount(view.getId(), 1);
    }

    @Transactional
    public void addViewCountIfPresentOrSave(Video video, Member member) {
        find(video, member).ifPresentOrElse(this::addViewCount, () -> save(video));
    }
}
