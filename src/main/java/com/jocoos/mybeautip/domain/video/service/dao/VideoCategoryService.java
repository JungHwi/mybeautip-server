package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.domain.video.converter.VideoCategoryConverter;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.persistence.domain.VideoCategory;
import com.jocoos.mybeautip.domain.video.persistence.repository.VideoCategoryRepository;
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
}
