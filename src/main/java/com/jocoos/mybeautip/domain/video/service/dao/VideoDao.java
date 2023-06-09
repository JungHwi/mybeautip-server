package com.jocoos.mybeautip.domain.video.service.dao;

import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.dto.VideoCategoryResponse;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static com.jocoos.mybeautip.domain.video.code.VideoStatus.OPEN;
import static com.jocoos.mybeautip.domain.video.code.VideoStatus.RESERVE;

@Service
@RequiredArgsConstructor
public class VideoDao {

    private final VideoRepository repository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Video> getVideos(VideoCategoryResponse category, ZonedDateTime cursor, int size) {
        VideoSearchCondition condition = VideoSearchCondition.builder()
                .category(category)
                .cursor(cursor)
                .size(size)
                .build();
        return getVideos(condition);
    }

    @Transactional(readOnly = true)
    public List<Video> getRecommendedVideos() {
        VideoSearchCondition condition = VideoSearchCondition.builder()
                .isRecommended(true)
                .build();
        return repository.getVideos(condition);
    }

    public List<Video> getVideos(VideoSearchCondition condition) {
        return repository.getVideos(condition);
    }

    @Transactional(readOnly = true)
    public Page<AdminVideoResponse> getVideos(AdminVideoSearchCondition condition) {
        return repository.getVideos(condition);
    }

    @Transactional(readOnly = true)
    public List<Video> getVideos(int size) {
        VideoSearchCondition condition = VideoSearchCondition.builder()
                .size(size)
                .build();
        return getVideos(condition);
    }

    @Transactional(readOnly = true)
    public Video getVideo(long videoId) {
        return repository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("No such video. id - " + videoId));
    }

    @Transactional(readOnly = true)
    public SearchResult<Video> search(VideoSearchCondition condition) {
        return repository.search(condition);
    }

    @Transactional(readOnly = true)
    public Long count(String keyword) {
        return repository.countBy(keyword);
    }

    @Transactional
    public void commentCount(Long videoId, int count) {
        repository.updateCommentCount(videoId, count);
    }

    @Transactional
    public void setCommentCount(Long videoId, int count) {
        repository.setCommentCount(videoId, count);
    }

    @Transactional
    public List<Long> arrangeByIndex(List<Long> sortedIds) {
        return repository.arrangeByIndex(sortedIds);
    }

    @Transactional
    public void fixAndAddToLastOrder(Long videoId) {
        repository.fixAndAddToLastOrder(videoId);
    }

    @Transactional
    public void unFixAndSortingToNull(Long videoId) {
        repository.unFixAndSortingToNull(videoId);
    }

    @Transactional(readOnly = true)
    public List<Video> findVideosToOpen() {
        return repository.findByStatusAndStartedAtLessThanEqualAndDeletedAtIsNull(RESERVE, new Date());
    }

    @Transactional
    public long openVideos(List<Video> videos) {
        return repository.bulkUpdateStatus(videos, OPEN);
    }

    @Transactional
    public void addViewCount(Video video) {
        repository.addViewCount(video.getId(), 1);
    }

    @Transactional(readOnly = true)
    public Video getWithFlushAndClear(Long videoId) {
        entityManager.flush();
        entityManager.clear();
        return getVideo(videoId);
    }
}
