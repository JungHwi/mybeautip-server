package com.jocoos.mybeautip.video.scrap;

import com.google.common.base.Strings;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoScrapService {

  private final MessageService messageService;
  private final VideoService videoService;
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

  public List<VideoScrap> findByMemberId(Long memberId, String cursor, Pageable pageable) {
    if (!Strings.isNullOrEmpty(cursor)) {

      return videoScrapRepository.findByCreatedByIdAndCreatedAtBefore(memberId, new Date(cursor), pageable);
    }
    return videoScrapRepository.findByCreatedById(memberId, pageable);
  }

  public static void main(String[] args) {
    String longValue = "1637836275304";
    LocalDateTime ldt =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(longValue)), ZoneId.systemDefault());

    Date date = Date.from(ldt.atZone(ZoneId.systemDefault())
        .toInstant());
    System.out.println(date);
  }
}
