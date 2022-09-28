package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoCategoryDao {

    private final VideoCategoryRepository repository;

    @Transactional(readOnly = true)
    public List<VideoCategory> findByIds(List<Integer> ids) {
        return repository.findAllByIdIn(ids);
    }
}
