package com.jocoos.mybeautip.domain.video.service;

import com.jocoos.mybeautip.domain.video.converter.VideoCategoryConverter;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoCategoryService {
    private final VideoCategoryRepository repository;
    private final VideoCategoryConverter converter;

    @Transactional(readOnly = true)
    public List<VideoCategoryResponse> getVideoCategoryList() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "sort"));
        List<VideoCategory> categories = repository.findAllBy(pageable);

        return converter.convert(categories);
    }

    @Transactional(readOnly = true)
    public VideoCategoryResponse getVideoCategory(int id) {
        VideoCategory videoCategory = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(""));
        return converter.convert(videoCategory);
    }

    @Transactional(readOnly = true)
    public List<VideoCategoryResponse> getVideoCategoryList(List<Integer> ids) {
        List<VideoCategory> videoCategories = repository.findAllByIdIn(ids);
        return converter.convert(videoCategories);
    }
}
