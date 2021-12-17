package com.jocoos.mybeautip.video.scrap;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.Visibility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoScrapService {

  private final VideoRepository videoRepository;
  private final VideoScrapRepository videoScrapRepository;

  @Transactional
  public VideoScrap scrapVideo(Video video, Long memberId) {
    if (videoScrapRepository.existsByVideoIdAndCreatedById(video.getId(), memberId)) {
      throw new BadRequestException("already_scrap");
    }
    return scrapVideo(video);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public VideoScrap scrapVideo(Video video) {
    video.setScrapCount(video.getScrapCount() + 1);
    videoRepository.save(video);
    return videoScrapRepository.save(new VideoScrap(video));
  }

  @Transactional
  public void deleteScrap(Video video, Long memberId) {
    VideoScrap scrap = videoScrapRepository.findByVideoIdAndCreatedById(video.getId(), memberId)
        .orElseThrow(() -> new NotFoundException("scrap_not_found", ""));

    videoScrapRepository.delete(scrap);

    video.setScrapCount(video.getScrapCount() - 1);
    videoRepository.save(video);
  }

  public List<VideoScrap> findByMemberId(Long memberId, String cursor, Visibility visibility, Pageable pageable) {
    String visibilityName = visibility != null ? visibility.name() : Visibility.PUBLIC.name();
    if (!Strings.isNullOrEmpty(cursor)) {
      Date createdAtBefore = DateUtils.toDate(cursor);
      log.debug("cursor: {}", createdAtBefore);
      return videoScrapRepository.findByCreatedByIdAndCreatedAtBeforeAndVideoVisibilityAndVideoDeletedAtIsNull(memberId, createdAtBefore, visibilityName, pageable);
    }
    return videoScrapRepository.findByCreatedByIdAndVideoVisibilityAndVideoDeletedAtIsNull(memberId, visibilityName, pageable);
  }
}
