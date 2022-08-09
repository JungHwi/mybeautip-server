package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.global.code.LikeStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Optional<VideoLike> findByVideoIdAndCreatedByIdAndStatus(Long videoId, Long memberId, LikeStatus status);

    Optional<VideoLike> findByIdAndVideoIdAndCreatedById(Long likeId, Long videoId, Long memberId);

    Slice<VideoLike> findByCreatedAtBeforeAndCreatedByIdAndVideoDeletedAtIsNullAndStatus(Date createdAt, Long createdBy, Pageable pageable, LikeStatus status);

    Slice<VideoLike> findByVideoIdAndCreatedAtBeforeAndVideoDeletedAtIsNullAndStatus(Long videoId, Date createdAt, Pageable pageable, LikeStatus status);

    Slice<VideoLike> findByCreatedByIdAndVideoDeletedAtIsNullAndStatus(Long createdBy, Pageable pageable, LikeStatus status);

    Integer countByCreatedByIdAndVideoDeletedAtIsNullAndStatus(Long memberId, LikeStatus status);

    boolean existsByVideoIdAndCreatedById(long videoId, long memberId);

    void deleteByVideoId(Long videoId);

    Optional<VideoLike> findByVideoIdAndCreatedById(Long videoId, Long memberId);
}
