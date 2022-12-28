package com.jocoos.mybeautip.video.view;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface VideoViewRepository extends JpaRepository<VideoView, Long> {

    Optional<VideoView> findByVideoIdAndCreatedById(Long videoId, Long createdById);

    Optional<VideoView> findByVideoAndCreatedBy(Video video, Member createdBy);

    Optional<VideoView> findByVideoIdAndGuestName(Long videoId, String guestName);

    Slice<VideoView> findByVideoIdAndAndCreatedByIsNotNullAndModifiedAtBefore(Long videoId, Date time, Pageable pageable);

    int countByVideoIdAndCreatedByIsNull(Long id);

    int countByVideoIdAndGuestNameIsNotNull(Long id);

    @Modifying
    @Query("update VideoView view set view.viewCount = view.viewCount + :addCount where view.id = :id")
    void addViewCount(Long id, int addCount);
}
