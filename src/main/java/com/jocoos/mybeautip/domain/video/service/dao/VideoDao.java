package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.code.VideoCategoryType;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.service.VideoCategoryService;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoDao {

    private final VideoCategoryService categoryService;
    private final VideoRepository repository;

    @Transactional(readOnly = true)
    public List<Video> getAnyoneAllVideos(Integer categoryId, String cursor, Pageable pageable) {
        Date startCursor = StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
        VideoCategoryResponse category = categoryService.getVideoCategory(categoryId);

        if (category.getType() == VideoCategoryType.GROUP) {
            return repository.getAnyoneAllVideos(startCursor, pageable).getContent();
        } else {
            return repository.getCategoryVideo(categoryId, startCursor, pageable).getContent();
        }
    }

    @Transactional(readOnly = true)
    public Video getVideo(long videoId) {
        return repository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("No such video. id - " + videoId));
    }

    public SearchResult<Video> search(KeywordSearchCondition condition) {
        return repository.search(condition);
    }

    public Long count(String keyword) {
        return repository.countBy(keyword);
    }
}
